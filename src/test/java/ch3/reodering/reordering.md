# NoVisibility Example
- MAIN 스레드와, READ 스레드가 존재.
- 스레드 간에 공유 변수가 존재하지만 동기화 X.
- 따라서, reordering문제가 발생해 stale data를 보게되어 활동성 문제 발생.

# YesVisibility
- 스레드 간에 공유 변수의 동기화.
- Q. 이때 어떤 동기화 수단을 이용해 동기화를 해야 할까?
- `volatile`? 'Atomic'?
- Q. How to test thread-safety, liveness?

