# Redux Based android architecture

이 프로젝트는 Redux 를 기반으로 한 안드로이드 어플리케이션 아키텍쳐를 정리 한 프로젝트 이다. [Android architecture blueprint](https://github.com/android/architecture-samples)를 기반으로 만들어졌다. 

사용된 도구 및 라이브러리르 간단하게 정리 하면 다음과 같다. (계속 수정될 수 있음)

* Architecture
  - androidx
  - MVVM
  - Databinding
  - Koin (DI)
* Event handler 
  - Rx2.x 
* Network 
  - retrofit 2.x
  - moshi
  - glide
* Testing
  - jUnit
  - mockito (대체 할 수 있는 좋은 테스팅 도구가 있는지?)
  - Koin-testing

## Redux

기본적인 Redux 에 대한 설명은 [링크](https://github.com/ksu3101/TIL/blob/master/ETC/200305.md)를 참고 하면 된다. 

## Android architecture

Android 에서 redux 를 기반으로 한 uni-directional data flow(UDA) 를 구현하기 위해서는 reactive stream 을 이용 하여 단방향 스트림을 통해 생성되는 객체 data 를 구독하는 방법으로 구현 한다. 

redux 기반 구조 에서는 장, 단점이 존재 하다. 가존 java 로 구현되었던 코드 에서는 반복되는 코드가 너무 많아 골치가 아팠지만 그나마 kotlin 으로 적용 하면서 반복 코드를 많이 줄이기는 했다. 하지만 그럼에도 문제는 여전히 존재 한다. 정리된 장-단점은 아래와 같다. 

- 장점
  - use-case로 정의된 데이터 흐름을 비동기, 동기로 구현하여 적용 할 수 있다. 
  - 비즈니스 코드와 뷰 코드 완전히 분리 되고 dependency 를 정리 하여 코드 가독성을 높여 유지, 보수가 쉬워진다. (이건 MVVM 의 장점) 
- 단점
  - Redux구조 를 알아야 하기 때문에 러닝 커브가 높다. 
  - 테스팅 소스를 작성 하는데 반복되는 코드가 존재 한다. (이 부분은 좀 더 정리가 필요할 듯 하다) 
  - Store 한개애 존재 하는 단일 State 구조에 여러개의 Domain 을 가질 수 있는 안드로이드 앱 구조상 실제 Redux 코드와 달라질 수 밖에 없다. 
  
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
 - Array<State>, List<State> : 
   - 간단한 구조 이지만, State 가 하나라도 갱신 되면 Array 나 List 를 다시 생성해야 한다. 
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

store는 단 한개만 존재 하며 stre 에는 단 1개의 state 만 가질 수 있다. 그것이 `AppState` 이다. 하지만 안드로이드 에서는 여러개의 상태가 존재 할 수 있다. (예를 들어 현재 도메인 화면 과 화면에 업데이트 될 토스트, 다이얼로그 및 상단 바 등 ui 변화 및 비동기 작업 으로 인한 세션 등 변화) 여러개의 상태를 만들되 이 를 store 에서 갖게 하려면 여러개의 `AppState` 가 아닌 AppState 내부에 자료구조(list 나 map 등) 을 두어 각 도메인별 유니크한 상태를 관리 하게 해 준다. 

`AppStore` 에서는 이 `AppState` 를 갖고 있으며 이 state 를 구독 할 수 있는 listener 등을 제공 한다. 그리고 `Action` 을 dispatch 할 수도 있다. 

## MVVM 

작성중...

## Koin 

작성중...

## Testing

작성중... 

### JunitTesting

