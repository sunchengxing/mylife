package com.mylife.app.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mylife.app.data.ApiService
import com.mylife.app.data.recipe.RecipeApiService
import com.mylife.app.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = BuildConfig.API_BASE_URL
    private const val RECIPE_URL = BuildConfig.RECIPE_API_URL

    private var jwtToken: String? = null

    fun setToken(token: String?) {
        jwtToken = token
    }

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

    val api: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(ApiService::class.java)

    val recipeApi: RecipeApiService = Retrofit.Builder()
        .baseUrl(RECIPE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(RecipeApiService::class.java)
}
