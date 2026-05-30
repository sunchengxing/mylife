package com.mylife.app.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.mylife.app.MyLifeApp
import com.mylife.app.data.Record
import com.mylife.app.data.StatsResponse
import com.mylife.app.util.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

data class RecordsState(
    val items: List<Record> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = false,
)

data class StatsState(
    val totalRecords: Int = 0,
    val totalCost: Double = 0.0,
    val clothingCount: Int = 0,
    val foodCount: Int = 0,
    val housingCount: Int = 0,
    val transportCount: Int = 0,
    val clothingCost: Double = 0.0,
    val foodCost: Double = 0.0,
    val housingCost: Double = 0.0,
    val transportCost: Double = 0.0,
)

data class AuthState(
    val isLoggedIn: Boolean = ApiClient.getToken() != null,
    val username: String = "",
    val isSyncing: Boolean = false,
    val lastSync: String = "",
)

class RecordViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = (application as MyLifeApp).database.recordDao()

    private val _state = MutableStateFlow(RecordsState())
    val state: StateFlow<RecordsState> = _state

    private var currentStore = "clothing"

    fun setStore(store: String) {
        currentStore = store
        loadRecords()
    }

    fun loadRecords() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val items = dao.getByStore(currentStore)
            _state.value = RecordsState(items = items, isLoading = false)
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            val all = dao.getByStore(currentStore)
            val filtered = if (query.isBlank()) all
            else all.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.note.contains(query, ignoreCase = true)
            }
            _state.value = RecordsState(items = filtered, query = query)
        }
    }

    fun saveRecord(record: Record) {
        viewModelScope.launch {
            dao.upsert(record)
            loadRecords()
        }
    }

    fun deleteRecord(id: String) {
        viewModelScope.launch {
            dao.deleteById(id, currentStore)
            loadRecords()
        }
    }
}

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = (application as MyLifeApp).database.recordDao()

    private val _stats = MutableStateFlow(StatsState())
    val stats: StateFlow<StatsState> = _stats

    private val _auth = MutableStateFlow(AuthState(isLoggedIn = ApiClient.getToken() != null))
    val auth: StateFlow<AuthState> = _auth

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            val cc = dao.countByStore("clothing")
            val fc = dao.countByStore("food")
            val hc = dao.countByStore("housing")
            val tc = dao.countByStore("transport")

            val clothingItems = dao.getByStore("clothing")
            val foodItems = dao.getByStore("food")
            val housingItems = dao.getByStore("housing")
            val transportItems = dao.getByStore("transport")

            fun calcCost(items: List<Record>) = items.sumOf { it.cost.toDoubleOrNull() ?: 0.0 }

            _stats.value = StatsState(
                totalRecords = cc + fc + hc + tc,
                totalCost = calcCost(clothingItems) + calcCost(foodItems) + calcCost(housingItems) + calcCost(transportItems),
                clothingCount = cc, foodCount = fc, housingCount = hc, transportCount = tc,
                clothingCost = calcCost(clothingItems), foodCost = calcCost(foodItems),
                housingCost = calcCost(housingItems), transportCost = calcCost(transportItems),
            )
        }
    }

    fun login(username: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val resp = ApiClient.api.login(mapOf("username" to username, "password" to password))
                ApiClient.setToken(resp.token)
                _auth.value = AuthState(isLoggedIn = true, username = resp.username)
                onResult(true)
            } catch (_: Exception) {
                onResult(false)
            }
        }
    }

    fun register(username: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val resp = ApiClient.api.register(mapOf("username" to username, "password" to password))
                ApiClient.setToken(resp.token)
                _auth.value = AuthState(isLoggedIn = true, username = resp.username)
                onResult(true)
            } catch (_: Exception) {
                onResult(false)
            }
        }
    }

    fun logout() {
        ApiClient.setToken(null)
        _auth.value = AuthState(isLoggedIn = false)
    }

    fun sync() {
        viewModelScope.launch {
            _auth.value = _auth.value.copy(isSyncing = true)
            try {
                val since = Date(0)
                val localChanges = dao.getUpdatedSince(since)
                if (localChanges.isNotEmpty()) {
                    ApiClient.api.syncPush(com.mylife.app.data.SyncPayload(
                        records = localChanges.map { r ->
                            com.mylife.app.data.RecordDto(
                                id = r.id, store = r.store, name = r.name,
                                category = r.category, season = r.season, calories = r.calories,
                                cost = r.cost, note = r.note, photo = r.photo,
                            )
                        }
                    ))
                }
                val serverRecords = ApiClient.api.syncPull().records
                if (serverRecords.isNotEmpty()) {
                    dao.upsertAll(serverRecords.map { dto ->
                        Record(
                            id = dto.id, store = dto.store, name = dto.name,
                            category = dto.category, season = dto.season, calories = dto.calories,
                            cost = dto.cost, note = dto.note, photo = dto.photo,
                        )
                    })
                }
                loadStats()
                _auth.value = _auth.value.copy(isSyncing = false, lastSync = Date().toString())
            } catch (_: Exception) {
                _auth.value = _auth.value.copy(isSyncing = false)
            }
        }
    }
}
