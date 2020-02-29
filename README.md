# Redux Based android architecture

이 프로젝트는 Redux 를 기반으로 한 안드로이드 어플리케이션 아키텍쳐를 정리 한 프로젝트 이다. 

## Redux

Redux 의 흐름을 간단하게 보면 다음과 같다. 

![redux base flow](https://github.com/ksu3101/TIL/blob/master/imgs/100110_android_redux_arch4.png)

### 1. Action

Action 은 View 를 변화시키기 위해 Store 를 통해 dispatch 되는 Event 들 을 정의 한 Immutable Data 인스턴스 이다. Action 은 사용자가 발생시킨 Action 그리고 Action 에서 파생된 Result Action인 Success Action, Failed Action 등 이 존재 할 수 있다.

또한, Action 은 Domain 단위에 따라 재정의 되어 dispatch 할 수 있다. 

### 2. State

State 는 domain별로 단 한개의 최신 상태를 갖게 되는 Immutable Data 인스턴스로서 View 에 변화를 주기 위한 데이터를 포함한 객체이다. State 는 Action 이 dispatch 되어야만 갱신 되어 Store 에 저장된다. 

State 는 Domain 단위에 따라 각 상태에 따라 재정의 된다. 

### 3. Middleware

dispatch 된 Action 을 이터레이셔닝 하면서 핸들링 한다. middleware 는 1개 일수도 그 보다 많을 수 있다. 모든 middleware 를 이터레이셔닝 모두 지나거나 혹은 중간에 기존의 Action 을 토대로 새로운 Action 이 될 수 있다.

### 4. Reducer

Middleware 를 통해서 나온 Action 을 분기 하여 State를 만들어 준다. 생성된 State 는 Store 를 통해서 구독 할 수 있다. 

### 5. Store

최신 상태인 State 와 Reducer들을 갖는 인스턴스이다. Store 를 통해서 새로운 Action 을 dispatcbh 하거나 특정 State 를 subscribe 할 수 있다.

## Android architecture

Android 에서 redux 를 기반으로 한 uni-directional data flow(UDA) 를 구현하기 위해서는 reactive stream 을 이용 하여 단방향 스트림을 통해 생성되는 객체 data 를 구독하는 방법으로 구현 한다. 

![android redux base1](https://github.com/ksu3101/TIL/blob/master/imgs/100110_android_redux_arch2.png)

기본적인 구조는 Redux 를 참고 하였지만 화면상에서 보여지는 상태를 최소화 하여도 팝업 다이얼로그, 토스트 등 domain 에서 벗어난 공통 화면 변경 등이 있어 state, reducer 들의 구조가 약간 다르다. 

자세한 흐름은 아래 이미지와 설명을 참고 하자. 

![android redux base2](https://github.com/ksu3101/TIL/blob/master/imgs/100110_android_redux_arch3.png)


### 1. Action 

```kotlin
interface Action
```

안드로이드에서 정의 된 Action 은 각종 이벤트 와 특정 Action 을 핸들링 하고 난 뒤 다시 핸들링 하기 위해서 Dispatch 된 Success, Failed Action 등 이 존재 한다. 

Action 인터페이스를 구현한 Action 은 Immutable Data class 로서 Action 을 미들웨어, 리듀서 에서 핸들링 하기 위한 불변 데이터들을 담을 수 있다. Action 단순히 뷰에 대한 업데이트를 요구 하는 트리거 이벤트가 될 수도 있고, 데이터를 담아 네트워크 API 를 이용하거나 Database 에서 데이터를 가져 오는 등 비동기 작업을 요청 할 수도 있다. 

#### 1.1 Common Action

안드로이드에서 공통적으로 사용 되는 뷰들에 대한 Action 들 이다.

- Toast
- Dialog 
- Snackbar
- 그 외 커스텀 메시지 뷰 등...

#### 1.2 Domain Action

도메인에 특화되어 각 use-case 를 정의한 Action 들 이다.

### 2. State

```kotlin
interface State
```

Store 에서는 단 한개의 State 만 을 가질 수 있다. 하지만 공통 state 나 각 도메인별 state 가 증가 하게 된다면 Store 의 State 엔 모두 저장 할 수 없다. 그렇기 때문에 State 는 `AppState` 라는 한개의 State 를 Store 에 저장 하되, AppState 의 내부에 공통, 도메인 State 를 갖게 해 준다. 

하지만 문제가 있다면 각 도메인 등 에서 자신의 State 를 구독 할때를 제외할 때  `getCurrentState()` 을 통해서 State 를 얻을 때 이다. get 하려 할 때 어떤 State 를 얻어와야 하는지 모르므로 AppState 에서 필요한 State 를 꺼내올 수 있는 최소한의 정보가 필요하다. 

- AppState 내부에서 sub domain State를 관리 하는 방법
 - Array<State>, List<State> : 간단한 구조 이지만, State 가 하나라도 갱신 되면 Array 나 List 를 다시 생성해야 한다. 
 - Map(String, State) : Key String 으로 State 의 class name 등 유니크 한 값을 기반으로 State 를 저장 하고 갱신하게 한다. 

#### 2.1 Common State

안드로이드 공통적으로 사용 되는 뷰에 대한 상태들 이다. 위 Action 을 통해서 보여줄 메시지 및 데이터를 받아 ViewModel 등 에서 핸들링 할 수 있게 한다.

#### 2.2 Domain State

도메인에 특화되어 각 use-case 에 대응 되는 뷰 변화를 상태 데이터 로 정의 한 State 이다.

### 3. Middleware

```kotlin
typealias Dispatcher = (Action) -> Unit

interface MiddleWare<S: State> {
  fun create(store: Store<S>, next: Dispatcher): Dispatcher
}
```

Action 을 Reducer 에 전달 하기 위해 중간에 개입하여 데이터를 제어 한다. 

자세하게 보면, Store 를 통해 Dispatch 된 Action 을 제어 하여 새로운 Action(Success, Failed) 을 생성하여 Reducer 에 전달 한다. 꼭 생성할 필요는 없이 중간에서 Action 과 관련된 작업 후 해당 Action 을 그대로 리듀서에 전달 해도 상관없다. 

#### 3.1 ActionProcessor Middleware

```kotlin
interface ActionProcessor<S: State> {
  fun run(action: Observable<Action>, store: Store<S>): Observable<out Action>
}
```

dispatch 된 Action 들을 받아 내부에서 프로세싱 하고 난 뒤 다음 미들웨어에 전달 한다. Action processor middleware 내 에는 여러개의 Action processor 가 존재 하며 이 들을 이터레이셔닝 하고 나온 Success 혹은 Failed Action 을 다음 미들웨어에 전달 하거나 다시 새로운 Action 을 dispatch 하게 할 수도 있다. 

#### 3.2 Logger Middlewar

```kotlin
class LoggerMiddleware<S : State> : Middleware<S> {
    override fun create(store: Store<S>, next: Dispatcher): Dispatcher {
        return { action: Action ->
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "action dispatch : [${action.getSuperClassNames()}] $action")
            }
            val prevState = store.getCurrentState()
            next(action)

            if (BuildConfig.DEBUG) {
                val currentState = store.getCurrentState()
                if (prevState != currentState) {
                    (currentState as AppState).printStateLogs()
                }
            }
        }
    }
}
```

최초 dispatch 된 Action 과 Success/Failed Action 등 Result Action 까지의 변화를 로깅 해 주는 미들웨어 이다. 이 미들웨어는 실제 Action 의 생성 에 관여하지 않으며 단순히 내부상태만 읽고 디버깅용 로그메시지로 출력 하도록 한다. 

### 4. Reducer 

```kotlin
interface Reducer<S: State> {
  fun reduce(oldState: S, resultAction: Action): S
}
```

Middleware 를 통해 전달받은 (Result) Action 을 핸들링 하여 단 한개의 State 를 만든다. State 는 이전 State 일 수도 있다. State 는 화면을 그리기 위한 기반 데이터를 담은 Immutable data class 이다. 

#### 4.1 AppReducer

```kotlin
class AppReducer: Reducer<AppState>, KoinComponent {
  override fun reduce(oldState: AppState, resultAction: Action): AppState {
    // ...
  }
}
```

AppState 를 제어 하기 위한 Reducer 이다. 

#### 4.2 Domain Reducer

각 Domain 별로 갖게되는 Reducer 들 이다. 내부에서 Action 을 전달 받아 필요한 경우 핸들링 하고 새로운 State 를 만든다. 

### 5. Store

```kotlin
interface Store<S : State> {
    fun dispatch(action: Action)
    fun getStateListener(): Observable<S>
    fun getCurrentState(): S
}
```

최신 State 가 저장된 Store 이다.


#### 5.1 AppStore

```kotlin
class AppStore(
    initializeAppState: AppState,
    reducer: Reducer<AppState>
) : Store<AppState>, KoinComponent {
    private val stateEmitter: BehaviorSubject<AppState> = BehaviorSubject.create()
    private val middleWares: Array<MiddleWare<AppState>> = getKoin().get()
    private var appState: AppState = initializeAppState
    private var dispatcher: Dispatcher

    init {
        dispatcher = middleWares.foldRight({ dispatchedAction ->
            appState = reducer.reduce(appState, dispatchedAction)
            stateEmitter.onNext(appState)
        }) { middleWare, next ->
            middleWare.create(this, next)
        }
    }

    override fun dispatch(action: Action) {
        dispatcher(action)
    }

    override fun subscribe(): Observable<AppState> =
        stateEmitter.hide().observeOn(AndroidSchedulers.mainThread())

    override fun getState(): AppState = appState
}
```

## MVVM 

## Koin 
