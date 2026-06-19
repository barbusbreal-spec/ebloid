package com.ebloid.store.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    val id: Int,
    val username: String,
    val email: String? = null,
    @Json(name = "display_name") val displayName: String? = null,
    @Json(name = "is_developer") val isDeveloper: Boolean = false,
    @Json(name = "is_admin") val isAdmin: Boolean = false,
)

@JsonClass(generateAdapter = true)
data class AuthResponse(val token: String, val user: User)

@JsonClass(generateAdapter = true)
data class AppSummary(
    val id: Int,
    @Json(name = "package_name") val packageName: String,
    val title: String,
    @Json(name = "short_description") val shortDescription: String? = null,
    val category: String? = null,
    val version: String? = null,
    @Json(name = "icon_url") val iconUrl: String? = null,
    val rating: Double = 0.0,
    @Json(name = "reviews_count") val reviewsCount: Int = 0,
    val downloads: Int = 0,
    val developer: String? = null,
)

@JsonClass(generateAdapter = true)
data class AppDetail(
    val id: Int,
    @Json(name = "package_name") val packageName: String,
    val title: String,
    @Json(name = "short_description") val shortDescription: String? = null,
    val description: String? = null,
    val category: String? = null,
    val version: String? = null,
    @Json(name = "icon_url") val iconUrl: String? = null,
    val rating: Double = 0.0,
    @Json(name = "reviews_count") val reviewsCount: Int = 0,
    val downloads: Int = 0,
    val developer: String? = null,
    val screenshots: List<String> = emptyList(),
    @Json(name = "apk_url") val apkUrl: String? = null,
)

@JsonClass(generateAdapter = true)
data class Review(
    val id: Int,
    val rating: Int,
    val text: String? = null,
    val author: String,
    @Json(name = "created_at") val createdAt: String? = null,
)

@JsonClass(generateAdapter = true)
data class AppsResponse(val apps: List<AppSummary>)

@JsonClass(generateAdapter = true)
data class AppDetailResponse(val app: AppDetail)

@JsonClass(generateAdapter = true)
data class ReviewsResponse(val reviews: List<Review>)

@JsonClass(generateAdapter = true)
data class ReviewResponse(val review: Review)

@JsonClass(generateAdapter = true)
data class CategoriesResponse(val categories: List<String>)

@JsonClass(generateAdapter = true)
data class MeResponse(val user: User)

@JsonClass(generateAdapter = true)
data class DownloadResponse(
    val downloads: Int,
    @Json(name = "apk_url") val apkUrl: String? = null,
)

@JsonClass(generateAdapter = true)
data class ErrorResponse(val error: String? = null)

// Тела запросов
@JsonClass(generateAdapter = true)
data class RegisterRequest(val username: String, val email: String, val password: String)

@JsonClass(generateAdapter = true)
data class LoginRequest(val username: String, val password: String)

@JsonClass(generateAdapter = true)
data class ReviewRequest(val rating: Int, val text: String)
