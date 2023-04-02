# Goal

BlockingQueue의 Producer-Consumer 패턴을 적용하기 좋은 예제 작성

### FileCrawler.java

[ What ]

- 지정된 디렉토리계층구조에 포함된 파일을 전부 읽어 들여, 해당 파일의 Index를 만드는 작업 수행 
- Producer : Searching FILE (검색 대상 파일 이름을 모두 BlockingQueue에 쌓아넣기)
- Consumer : Indexing File (BlockingQueue에서 파일 이름을 하나씩 꺼내서 Indexing)

[ Pros ]

- 객체 역햘과 책임에 맞게 클래스 분리를 통해, SRP, 재사용성, 가독성 향상.
- Producer와 Consumer가 서로 독립적으로 실행 가능.
- 즉, CPU bound task와, IO bound task를 나누어 독립적으로 실행하여 성능 향상.
- 이때, 해당 객체의 소유권 이전 및 작업 흐름 조절은 BlockingQueue에게 전임하며,
- 큐 사이즈에 제한을 둠으로써, 정해진 메모리로만 안전하게 동작하도록 제한 가능.

[ TODO ]

1. Consumer의 스레드는 파일을 모두 찾아내도 끝나지 않는 문제
- `take()` 메서드는 BlockingQueue가 비어있을 때, 무한정 대기하기 때문이다.
- 7장 참조하여 문제 해결 예정

2. Executor Framework적용
- Executor Framework은 내부적으로 Producer-Consumer패턴 적용되어 있다.

3. InterruptedException handling 방법?

