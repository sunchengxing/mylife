package com.mylife.app.data

import retrofit2.http.*

interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body body: Map<String, String>): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body body: Map<String, String>): AuthResponse

    @GET("{store}")
    suspend fun listRecords(@Path("store") store: String): List<RecordDto>

    @POST("{store}")
    suspend fun createRecord(@Path("store") store: String, @Body record: RecordDto): RecordDto

    @PUT("{store}/{id}")
    suspend fun updateRecord(@Path("store") store: String, @Path("id") id: String, @Body record: RecordDto): RecordDto

    @DELETE("{store}/{id}")
    suspend fun deleteRecord(@Path("store") store: String, @Path("id") id: String)

    @GET("sync")
    suspend fun syncPull(@Query("since") since: String? = null): SyncPayload

    @POST("sync")
    suspend fun syncPush(@Body payload: SyncPayload): Map<String, Int>

    @GET("stats")
    suspend fun getStats(): StatsResponse
}
