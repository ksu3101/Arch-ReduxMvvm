# Redux Based android architecture

작성중..

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

#### 1.1 Common Action

안드로이드에서 공통적으로 사용 되는 뷰에 대한 Action 들 이다.

- Toast
- Dialog 
- Snackbar
- 그 외 커스텀 메시지 뷰 등...

#### 1.2 Domain Action

도메인에 특화되어 각 use-case 를 정의한 Action 들 이다.

### 2. State

Store 에서는 단 한개의 State 만 을 가질 수 있다. 하지만 공통 state 나 각 도메인별 state 가 증가 하게 된다면 Store 의 State 엔 모두 저장 할 수 없다. 그렇기 때문에 State 는 `AppState` 라는 한개의 State 를 Store 에 저장 하되, AppState 의 내부에 공통, 도메인 State 를 갖게 해 준다. 

하지만 문제가 있다면 각 도메인 등 에서 자신의 State 를 구독 할때를 제외할 때  `getCurrentState()` 을 통해서 State 를 얻을 때 이다. get 하려 할 때 어떤 State 를 얻어와야 하는지 모르므로 AppState 에서 필요한 State 를 꺼내올 수 있는 최소한의 정보가 필요하다. 

#### 2.1 Common State

안드로이드 공통적으로 사용 되는 뷰에 대한 상태들 이다. 위 Action 을 통해서 보여줄 메시지 및 데이터를 받아 ViewModel 등 에서 핸들링 할 수 있게 한다.

#### 2.2 Domain State

도메인에 특화되어 각 use-case 에 대응 되는 뷰 변화를 상태 데이터 로 정의 한 State 이다.

### 3. Middleware

#### 3.1 ActionProcessor Middleware

dispatch 된 Action 들을 받아 내부에서 프로세싱 하고 난 뒤 다음 미들웨어에 전달 한다. Action processor middleware 내 에는 여러개의 Action processor 가 존재 하며 이 들을 이터레이셔닝 하고 나온 Success 혹은 Failed Action 을 다음 미들웨어에 전달 하거나 다시 새로운 Action 을 dispatch 하게 할 수도 있다. 

#### 3.2 Logger Middlewar

최초 dispatch 된 Action 과 Success/Failed Action 등 Result Action 까지의 변화를 로깅 해 주는 미들웨어 이다. 이 미들웨어는 실제 Action 의 생성 에 관여하지 않으며 단순히 내부상태만 읽고 디버깅용 로그메시지로 출력 하도록 한다. 

### 4. Reducer 

#### 4.1 AppReducer



#### 4.2 Domain Reducer

### 5. Store

#### 5.1 AppStore

