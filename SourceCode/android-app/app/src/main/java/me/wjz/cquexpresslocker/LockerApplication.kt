package me.wjz.cquexpresslocker

import android.app.Application
import me.wjz.cquexpresslocker.utils.TokenManager

class LockerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 初始化 TokenManager
        TokenManager.initialize(this)
    }
}
