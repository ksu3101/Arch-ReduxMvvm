package com.example.mvvm.arch_reduxmvvm.base.di

import com.example.mvvm.arch_reduxmvvm.base.BaseActivity
import com.example.mvvm.arch_reduxmvvm.base.helper.MessageHelperImpl
import com.example.mvvm.common.DEFAULT_TIMEOUT_SEC
import com.example.mvvm.common.KOIN_CURRENT_ACTIVITY
import com.example.mvvm.model.base.BaseViewModel
import com.example.mvvm.model.base.helper.MessageHelper
import com.example.mvvm.model.base.redux.*
import com.example.mvvm.model.domain.common.MessageReducer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.Koin
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

/**
 * @author beemo
 * @since 2020-03-02
 */

val appModule = module {
    single {
        AppState(listOf())
    }
    single {
        AppStore(get(), AppReducer(get()))
    }
    single<Array<MiddleWare<AppState>>> {
        arrayOf(
            // todo
        )
    }
}

val repositoriesModule = module {
    single {
        OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT_SEC, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT_SEC, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }
    // todo
}

val helpersModule = module {
    single<MessageHelper> { MessageHelperImpl(androidApplication()) }
}

val reducersModule = module {
    single<List<Reducer<*>>> { listOf(
        MessageReducer()
    )}
}

val viewModelsModules = module {
    /* fixme
    viewModel<BaseViewModel<SomeDomainState>> {
        // create instance of ViewModel
    }
    */
}

fun Koin.currentActivity(): BaseActivity {
    return getProperty(KOIN_CURRENT_ACTIVITY)
        ?: throw NullPointerException("CurrentActivity is Null.")
}