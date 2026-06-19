package com.ebloid.store.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ebloid.store.data.AppDetail
import com.ebloid.store.data.AppSummary
import com.ebloid.store.data.Repository
import com.ebloid.store.data.Review
import com.ebloid.store.data.SessionManager
import com.ebloid.store.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CatalogState(
    val loading: Boolean = true,
    val apps: List<AppSummary> = emptyList(),
    val categories: List<String> = emptyList(),
    val query: String = "",
    val selectedCategory: String? = null,
    val sort: String = "new",
    val error: String? = null,
)

data class DetailState(
    val loading: Boolean = true,
    val app: AppDetail? = null,
    val reviews: List<Review> = emptyList(),
    val error: String? = null,
    val sending: Boolean = false,
)

class StoreViewModel(
    private val repo: Repository,
    private val session: SessionManager,
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _catalog = MutableStateFlow(CatalogState())
    val catalog: StateFlow<CatalogState> = _catalog.asStateFlow()

    private val _detail = MutableStateFlow(DetailState())
    val detail: StateFlow<DetailState> = _detail.asStateFlow()

    init {
        // Восстанавливаем токен из хранилища и подтягиваем профиль.
        viewModelScope.launch {
            val token = session.tokenFlow.first()
            if (token != null) {
                // cachedToken уже выставится при save; здесь страхуемся для cold start.
                session.save(token, session.usernameFlow.first() ?: "")
            }
        }
        loadCatalog()
    }

    // ---- Каталог -------------------------------------------------------
    fun loadCatalog() {
        _catalog.value = _catalog.value.copy(loading = true, error = null)
        viewModelScope.launch {
            val s = _catalog.value
            repo.apps(s.query, s.selectedCategory, s.sort).onSuccess { apps ->
                _catalog.value = _catalog.value.copy(loading = false, apps = apps)
            }.onFailure {
                _catalog.value = _catalog.value.copy(loading = false, error = it.message)
            }
            repo.categories().onSuccess { cats ->
                _catalog.value = _catalog.value.copy(categories = cats)
            }
        }
    }

    fun onQueryChange(q: String) {
        _catalog.value = _catalog.value.copy(query = q)
    }

    fun onSearch() = loadCatalog()

    fun selectCategory(category: String?) {
        _catalog.value = _catalog.value.copy(selectedCategory = category)
        loadCatalog()
    }

    fun setSort(sort: String) {
        _catalog.value = _catalog.value.copy(sort = sort)
        loadCatalog()
    }

    // ---- Детали приложения --------------------------------------------
    fun loadDetail(appId: Int) {
        _detail.value = DetailState(loading = true)
        viewModelScope.launch {
            repo.appDetail(appId).onSuccess { app ->
                _detail.value = _detail.value.copy(loading = false, app = app)
            }.onFailure {
                _detail.value = _detail.value.copy(loading = false, error = it.message)
            }
            repo.reviews(appId).onSuccess { reviews ->
                _detail.value = _detail.value.copy(reviews = reviews)
            }
        }
    }

    fun submitReview(appId: Int, rating: Int, text: String, onResult: (String?) -> Unit) {
        _detail.value = _detail.value.copy(sending = true)
        viewModelScope.launch {
            repo.addReview(appId, rating, text)
                .onSuccess {
                    _detail.value = _detail.value.copy(sending = false)
                    repo.reviews(appId).onSuccess { reviews ->
                        _detail.value = _detail.value.copy(reviews = reviews)
                    }
                    repo.appDetail(appId).onSuccess { app ->
                        _detail.value = _detail.value.copy(app = app)
                    }
                    onResult(null)
                }
                .onFailure {
                    _detail.value = _detail.value.copy(sending = false)
                    onResult(it.message)
                }
        }
    }

    fun onDownload(appId: Int) {
        viewModelScope.launch { repo.registerDownload(appId) }
    }

    // ---- Аккаунт -------------------------------------------------------
    fun isLoggedIn(): Boolean = session.cachedToken != null

    fun login(username: String, password: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            repo.login(username, password)
                .onSuccess { _user.value = it; onResult(null) }
                .onFailure { onResult(it.message) }
        }
    }

    fun register(username: String, email: String, password: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            repo.register(username, email, password)
                .onSuccess { _user.value = it; onResult(null) }
                .onFailure { onResult(it.message) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repo.logout()
            _user.value = null
        }
    }

    class Factory(
        private val repo: Repository,
        private val session: SessionManager,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            StoreViewModel(repo, session) as T
    }
}
