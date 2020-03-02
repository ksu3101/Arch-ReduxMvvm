package com.example.mvvm.arch_reduxmvvm.base

import androidx.multidex.MultiDexApplication
import com.example.mvvm.arch_reduxmvvm.base.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * fixme : 프로젝트 명에 따라 어플리케이션 클래스 이름을 바꿀 필요가 있으면 rename 해서 사용 할 것.
 *
 * @author beemo
 * @since 2020-03-02
 */
class BaseApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@BaseApplication)
            appModule
            repositoriesModule
            helpersModule
            reducersModule
            viewModelsModules
        }
    }

}