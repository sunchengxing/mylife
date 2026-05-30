package com.mylife.app.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mylife.app.data.ApiService
import com.mylife.app.data.recipe.RecipeApiService
import com.mylife.app.BuildConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private var _apiBaseUrl = BuildConfig.API_BASE_URL
    private var _recipeApiUrl = BuildConfig.RECIPE_API_URL

    var apiBaseUrl: String
        get() = _apiBaseUrl
        set(value) { _apiBaseUrl = value; rebuildApi() }

    var recipeApiUrl: String
        get() = _recipeApiUrl
        set(value) { _recipeApiUrl = value; rebuildRecipeApi() }

    private var jwtToken: String? = null

    fun setToken(token: String?) { jwtToken = token }
    fun getToken(): String? = jwtToken

    private val authInterceptor = Interceptor { chain ->
        val req = chain.request().newBuilder()
        jwtToken?.let { req.addHeader("Authorization", "Bearer $it") }
        chain.proceed(req.build())
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val gson: Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .create()

    private var _api: ApiService = buildApi()
    val api: ApiService get() = _api

    private var _recipeApi: RecipeApiService = buildRecipeApi()
    val recipeApi: RecipeApiService get() = _recipeApi

    private fun buildApi(): ApiService = Retrofit.Builder()
        .baseUrl(_apiBaseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(ApiService::class.java)

    private fun buildRecipeApi(): RecipeApiService = Retrofit.Builder()
        .baseUrl(_recipeApiUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(RecipeApiService::class.java)

    private fun rebuildApi() { _api = buildApi() }
    private fun rebuildRecipeApi() { _recipeApi = buildRecipeApi() }
}
