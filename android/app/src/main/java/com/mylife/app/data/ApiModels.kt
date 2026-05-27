package com.mylife.app.data

import com.google.gson.annotations.SerializedName
import java.util.Date

data class SyncPayload(
    val records: List<RecordDto>
)

data class RecordDto(
    val id: String,
    @SerializedName("user_id") val userId: Long = 0,
    val store: String,
    val name: String,
    val category: String = "",
    val season: String = "",
    val calories: String = "",
    val cost: String = "",
    val note: String = "",
    val photo: String = "",
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("updated_at") val updatedAt: String = "",
)

data class AuthResponse(
    val token: String,
    val username: String,
    @SerializedName("user_id") val userId: Long,
)

data class StatsResponse(
    @SerializedName("total_records") val totalRecords: Int,
    @SerializedName("total_cost") val totalCost: Double,
    @SerializedName("by_store") val byStore: Map<String, StoreStat>,
)

data class StoreStat(
    val count: Int,
    val cost: Double,
)
