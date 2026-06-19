package com.ebloid.store

import android.app.Application
import com.ebloid.store.data.Network
import com.ebloid.store.data.Repository
import com.ebloid.store.data.SessionManager

/**
 * Корневой контейнер зависимостей. Простой ручной DI без библиотек —
 * для приложения такого размера этого достаточно.
 */
class EbloidApp : Application() {
    lateinit var session: SessionManager
        private set
    lateinit var repository: Repository
        private set

    override fun onCreate() {
        super.onCreate()
        session = SessionManager(this)
        repository = Repository(Network.createApi(session), session)
    }
}
