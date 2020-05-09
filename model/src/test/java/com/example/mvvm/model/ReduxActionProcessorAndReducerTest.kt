package com.example.mvvm.model

import com.example.mvvm.model.utils.given
import com.example.mvvm.model.utils.mock
import com.example.mvvm.model.utils.shouldEqualTo
import com.example.mvvm.model.base.exts.actionTransformer
import com.example.mvvm.model.base.exts.createActionProcessor
import com.example.mvvm.model.base.redux.*
import com.example.mvvm.model.domain.common.*
import com.example.mvvm.model.utils.willReturn
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.observers.TestObserver
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.ArgumentMatchers.anyString

/**
 * Redux-MVVM 구조 테스트
 *
 * 가상의 서버를 통한 로그인 을 테스팅 하여 성공, 실패, 오류 등에 대한 상황이 정상적으로
 * 동작하는지 테스팅 하고 테스팅 소스에 대한 솔루션을 만든다.
 *
 * 테스팅 하기 위한 흐름은 다음과 같다.
 * 1. 최초 로그인 화면
 * 2. 사용자가 user id, pw 를 입력 하고 로그인 버튼 을 누름 (Action event occured)
 * 3. Middle(ActionProcessor) 를 통해 가상의 서버와 통신 하여 성공, 실패, 오류에 대해서 핸들링 하고
 * 각각에 대한 Result Action 을 Reducer 에 전달 한다.
 * 4. Reducer 에서 전달받은 Result Action 을 State 로 정상 생산 하여 구독 중인 TestSubscriber 에
 * 정상적으로 State Observable 이 도착 한다.
 *
 * - Success Action -> 로그인 성공 토스트 출력 + 받아온 유저 데이터 갱신 + 새로운 화면 랜딩
 * - Failed Action -> 로그인 실패 토스트 출력 -> 현상 유지
 * - Error Action -> 오류 메시지 출력 -> 현상 유지 (로컬에 로깅 혹은 특정 서버에 오류 로깅 전달 시
 * 해당 코드에 대한 테스팅 필요)
 *
 * - 주의점
 *  1. state 가 단 한개라도 발행 되면 모든 reducer 를 거치게 된다. 거치는 reducer 에서 핸들링 하지 않는다면
 *  무조건 oldState 를 리턴 하므로 state subscriber 의 value count assertion 은 oldState 를 포함한
 *  모든 state 의 발행 갯수와 맞추어야 한다.
 *
 * @author beemo
 * @since 2020-03-04
 */

/**
 * for testing Actions.
 */
sealed class TestingAuthAction : Action

object InitializedAction : TestingAuthAction()

data class RequestLoginAction(
    val id: String,
    val pw: String
) : TestingAuthAction()

data class LoginSuccessAction(
    val userInfo: String // and more datas...
) : TestingAuthAction()

/**
 * for testing States.
 */
sealed class TestingAuthState : State

object UserLoginState : TestingAuthState()

object UserLoginFailedState : TestingAuthState()

data class LobbyState(
    val userInfo: String
) : TestingAuthState()


/**
 * for testing reducer
 */
class AuthReducer(
    override val initializeState: TestingAuthState = UserLoginState
) : Reducer<TestingAuthState> {
    override fun reduce(oldState: TestingAuthState, resultAction: Action): TestingAuthState {
        return when (resultAction) {
            is InitializedAction -> UserLoginState
            is LoginSuccessAction -> LobbyState(resultAction.userInfo)
            else -> oldState
        }
    }
}

/**
 * for testing auth repository.
 */
interface AuthRepository {
    fun requestLogin(id: String, pw: String): Single<String>
}

/**
 * for testing auth action processor.
 */
class AuthActionProcessor(
    val authRepo: AuthRepository
) : ActionProcessor<AppState> {
    override fun run(action: Observable<Action>, store: Store<AppState>): Observable<out Action> {
        return action.compose(actionProcessor)
    }

    private val actionProcessor = createActionProcessor { shared ->
        arrayOf(
            shared.ofType(RequestLoginAction::class.java).compose(requestLogin)
        )
    }

    private val requestLogin = actionTransformer<RequestLoginAction> { action ->
        authRepo.requestLogin(action.id, action.pw)
            .map<Action> {
                LoginSuccessAction(it)
            }
            .onErrorReturn { handleError(it) }
            .toObservable()
    }

    private fun handleError(throwable: Throwable, action: Action? = null): MessageAction {
        return ShowingErrorToast(errorMessageStr = throwable.message)
    }
}

/**
 * example unit test codes.
 */
class ReduxActionProcessorAndReducerTest : KoinTest {
    private val authRepo: AuthRepository = mock()

    private val testModules = module {
        single { AppState(mapOf()) }
        single { AppStore(get(), AppReducer(get())) }
        single<Array<MiddleWare<AppState>>> {
            arrayOf(
                ActionProcessorMiddleWare(
                    CombinedActionProcessor(
                        listOf(
                            AuthActionProcessor(authRepo)
                        )
                    )
                )
            )
        }
        single {
            listOf(
                MessageReducer(),
                AuthReducer()
            )
        }
    }

    lateinit var stateSubscriber: TestObserver<TestingAuthState>
    lateinit var messagerStateSubscriber: TestObserver<MessageState>

    private val appStore: AppStore by inject()
    private val scheduler = TestScheduler()

    @Before
    fun setUp() {
        startKoin {
            modules(testModules)
        }
        RxAndroidPlugins.setMainThreadSchedulerHandler { schedulerCallable -> Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { schedulerCallable -> Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { ignore -> scheduler }

        stateSubscriber = appStore.stateListener()
            .map { it.getCurrentState<TestingAuthState>() ?: UserLoginState }
            .test()
        messagerStateSubscriber = appStore.stateListener()
            .map { it.getCurrentState<MessageState>() ?: HandledMessageState }
            .test()
    }

    @After
    fun tearDown() {
        stateSubscriber.dispose()
        messagerStateSubscriber.dispose()
        RxAndroidPlugins.reset()
        RxJavaPlugins.reset()
        stopKoin()
    }

    @Test
    fun readyToLogin_GivenNothing_ThenUserLoginState() {
        appStore.dispatch(InitializedAction)

        stateSubscriber.assertNoErrors()
        stateSubscriber.assertValueCount(1)
        stateSubscriber.assertValueAt(0) { it is UserLoginState }
        messagerStateSubscriber.assertValueCount(1)
        messagerStateSubscriber.assertValueAt(0) { it is HandledMessageState}
    }

    @Test
    fun requestLogin_GivenLoginSuccessAction_ThenLobbyStateSubscribed() {
        val id = "asdf"
        val pw = "1q2w3e4r"
        val userInfo = "UserInfos...."
        given(authRepo.requestLogin(anyString(), anyString())) willReturn Single.just(userInfo)

        appStore.dispatch(RequestLoginAction(id, pw))

        stateSubscriber.assertNoErrors()
        stateSubscriber.assertValueCount(2)
        stateSubscriber.assertValueAt(1) {
            if (it is LobbyState) {
                it.userInfo shouldEqualTo userInfo
                return@assertValueAt true
            }
            false
        }
        messagerStateSubscriber.assertValueCount(2)
        messagerStateSubscriber.assertValueAt(1) { it is HandledMessageState}
    }

    @Test
    fun requestLogin_GivenLoginFailedAction_ThenShowingErrorMessageState() {
        val id = "asdf"
        val pw = "1q2w3e4r"
        given(authRepo.requestLogin(anyString(), anyString())) willReturn Single.error<Exception>(
            RuntimeException("error!")    // fixme : error 타입 정해지면 변경할 것
        )

        appStore.dispatch(RequestLoginAction(id, pw))

        stateSubscriber.assertNoErrors()
        stateSubscriber.assertValueCount(2)
        stateSubscriber.assertValueAt(1) { it is UserLoginState }
        messagerStateSubscriber.assertValueCount(2)
        messagerStateSubscriber.assertValueAt(1) { it is ShowingErrorToastState }
    }

}