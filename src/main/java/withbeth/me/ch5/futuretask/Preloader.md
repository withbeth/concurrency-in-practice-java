# Goal

- FutureTask(Future Pattern)을 이용한 상품정보 로딩을 미리 요청하는 예제.
- 시간이 많이 걸리는 작업을 미리 Load요청하여, 실제 결과를 필요로 하는 시점이 됐을때 기다리는 시간을 줄일 수 있다.

### FutureTask

[ What ]

- 다음의 상태등을 갖는 동기화 클래스
  - `시작전 대기`, `시작됨`, `종료됨`
- 한번 종료상태에 들어가면 더이상 상태가 바뀌는 일은 없다. (Latch처럼)
  - 응용패턴 : Bulking Pattern or 매번 현시점의 데이터를 설정할수있도록 응용 가능
- `get()`도 현재 상태정보에 따라 행동이 달라진다.
  - if status == TERMINATE, 결과를 바로 얻을수 있다(대기하는 시간 X)
  - else, 상태 종료될때까지 blocking-waiting

[ Impl ]

- `Future` 인터페이스와, `Runnable` 인터페이스를 갖는 구현체.
- 내부 속성으로 `Callable`인터페이스를 Composition으로 갖는다.
- `run()` : callable의 `call()`호출 후, `동기`적으로 결과값을 받아 결과 값을 `set()` or `setException()`.

[ PROS ]

- 응답성 향상.
- FutureTask객체로 원하는 작업을 wrap함으로써, 기존 작업은 멀티스레딩을 의식하지 않아도 된다.

[ When to use? ]

- Executor Framework에서, `비동기`작업 실행시 사용.
- 실제 결과 필요한 시점 전에, 미리 작업시켜두는 용도로 사용하여 응답성 향상을 목표로 한다.

[ 주의사항 ]

1. 작업 수행시 예외 원인을 찾기 어려운 경우가 있다.
   - `Callable`의 내부작업에서 어떤 예외를 발생시켰던 간에 해당 내용은, `get()`에서 `ExecutionException`으로 한번 포장되어 전파되게 된다.
   - 따라서,
     - `ExecutionException`을 catch해야 하며,
     - 실질적인 예외 원인을 알기 위해선, `ExecutionException.getCause()`를 통해 원인을 받아올때 `Throwable`로 받아와야한다.
     - 즉, 실제로 어떤 예외가 발생했는지 확인이 어렵다.

2. `ExecutionException`발생시, 원인은 다음 3가지 중 하나여야 하기에, 적절히 케이스에 맞게 예외처리가 필요하다.
   - `Callable`이 던지는 예외
   - `RuntimeException`
   - `Error`

### Preloader

[ What ]

- DB에서 제품 정보를 로딩하는 기능을 FutureTask를 통해 실제 필요한 시점전에 미리 요청.

[ Flow ]

- 제품정보를 끌어오는 기능의 FutureTask 및 해당 태스크를 실행할 스레드를 미리 생성.
- `start()` : 제품 정보를 가져오는 스레드 실행
- `get()` : 실제 제품 정보 필요시점에 호출시, 제품정보 반환.(또는 제품정보 받을때까지 blocking후 반환)

- Q. 스레드를 생성자 or 스태틱 초기화 영역에서 실행시키지 않는 이유? 
  - A. 객체 유출 방지를 위해.

[ 예외처리 ]

- 위 주의사항에서 언급한것처럼 `ExecutionException`발생시, 햬당 예외 원인에 맞게 적절한 예외처리가 필요하다.
- 따라서 예제에서는 편의상 예외처리 유틸리티 메서드를 이용한다.
- 이 유틸리티 메서드 호출전에, 
  - 자신이 처리할수 있는 예외에 있는지 먼저 체크하여,
    - 처리가능한 예외일 경우, 상위 메소드에 전파. (`Callable`이 던지는 예외는 처리 가능하다.)
    - 처리불가능 예외일 경우, 유틸리티 메서드에게 넘겨 `RunTimeException`으로 캐스팅 후 전파.
- 해당 유틸리티에서는, 다음과 같이 예외 처리를 시도한다.
  - 예외 원인이 
    - `RuntimeException`일 경우, 그대로 캐스팅하여 반환.
    - `Error`일 경우, 그대로 캐스팅하여 저낲.
    - 그 외의 경우, `IllegalStateException`으로 변환하여 전파.