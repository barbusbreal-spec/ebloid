package com.ebloid.store.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.HttpException

/**
 * Единая точка доступа к данным для UI. Оборачивает сетевые вызовы в [Result]
 * и аккуратно достаёт текст ошибки из тела ответа API.
 */
class Repository(
    private val api: ApiService,
    private val session: SessionManager,
) {
    private val errorAdapter = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
        .adapter(ErrorResponse::class.java)

    suspend fun register(username: String, email: String, password: String): Result<User> =
        runApi {
            val r = api.register(RegisterRequest(username, email, password))
            session.save(r.token, r.user.displayName ?: r.user.username)
            r.user
        }

    suspend fun login(username: String, password: String): Result<User> =
        runApi {
            val r = api.login(LoginRequest(username, password))
            session.save(r.token, r.user.displayName ?: r.user.username)
            r.user
        }

    suspend fun logout() = session.clear()

    suspend fun apps(query: String? = null, category: String? = null, sort: String? = null) =
        runApi { api.apps(query?.ifBlank { null }, category, sort).apps }

    suspend fun categories() = runApi { api.categories().categories }

    suspend fun appDetail(id: Int) = runApi { api.appDetail(id).app }

    suspend fun reviews(id: Int) = runApi { api.reviews(id).reviews }

    suspend fun addReview(id: Int, rating: Int, text: String) =
        runApi { api.addReview(id, ReviewRequest(rating, text)).review }

    suspend fun registerDownload(id: Int) = runApi { api.registerDownload(id) }

    private inline fun <T> runApi(block: () -> T): Result<T> = try {
        Result.success(block())
    } catch (e: HttpException) {
        Result.failure(Exception(extractError(e)))
    } catch (e: Exception) {
        Result.failure(Exception("Нет связи с сервером. Проверьте интернет."))
    }

    private fun extractError(e: HttpException): String = try {
        val body = e.response()?.errorBody()?.string()
        body?.let { errorAdapter.fromJson(it)?.error } ?: "Ошибка ${e.code()}"
    } catch (_: Exception) {
        "Ошибка ${e.code()}"
    }
}
