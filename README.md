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

Android 에서 redux 를 기반으로 한 uni-directional data flow(UDA) 를 구현하기 위해서는 reactive stream 을 이용 하여 단일 스트림을 구독하는 방법으로 구현 한다. 

![android redux base1](https://github.com/ksu3101/TIL/blob/master/imgs/100110_android_redux_arch2.png)

자세한 순서도

![android redux base2](https://github.com/ksu3101/TIL/blob/master/imgs/100110_android_redux_arch3.png)

직성 중...

