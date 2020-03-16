package com.example.mvvm.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mvvm.model.base.BaseViewModel
import com.example.mvvm.model.base.exts.livedata.setFalse
import com.example.mvvm.model.base.redux.AppStore
import org.junit.After
import org.junit.Before
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

/**
 *
 * @author beemo
 * @since 2020/03/17
 */

object UserLoginFailedState : TestingAuthState()

class LoginViewModel(
    private val appStore: AppStore
) : BaseViewModel<TestingAuthState>() {
    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String>
        get() = _userId

    private val _passWord = MutableLiveData<String>()
    val passWord: LiveData<String>
        get() = _passWord

    private val _isLoginSuccess = MutableLiveData<Boolean>()
    val isLoginSuccess: LiveData<Boolean>
        get() = _isLoginSuccess

    override fun render(state: TestingAuthState): Boolean {
        when (state) {
            is UserLoginState -> {
                _userId.value = ""
                _passWord.value = ""
                _isLoginSuccess.setFalse()
                return true
            }
            is UserLoginFailedState -> {
                _userId.value = ""
                _passWord.value = ""
                _isLoginSuccess.setFalse()
                return true
            }
        }
        return false
    }
}

class ViewModelBaseTest: KoinTest {
    private val testModules = module {

    }

    private lateinit var vm: LoginViewModel
    private val appStore: AppStore by inject()

    @Before
    fun setUp() {
        startKoin {
            modules(testModules)
        }
        vm = LoginViewModel(appStore)
    }

    @After
    fun tearDown() {

    }

}

