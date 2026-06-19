package com.ebloid.store.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @GET("auth/me")
    suspend fun me(): MeResponse

    @GET("apps")
    suspend fun apps(
        @Query("q") query: String? = null,
        @Query("category") category: String? = null,
        @Query("sort") sort: String? = null,
    ): AppsResponse

    @GET("categories")
    suspend fun categories(): CategoriesResponse

    @GET("apps/{id}")
    suspend fun appDetail(@Path("id") id: Int): AppDetailResponse

    @GET("apps/{id}/reviews")
    suspend fun reviews(@Path("id") id: Int): ReviewsResponse

    @POST("apps/{id}/reviews")
    suspend fun addReview(@Path("id") id: Int, @Body body: ReviewRequest): ReviewResponse

    @POST("apps/{id}/download")
    suspend fun registerDownload(@Path("id") id: Int): DownloadResponse
}
