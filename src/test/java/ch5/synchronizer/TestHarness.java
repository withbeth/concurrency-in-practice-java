package ch5.synchronizer;

import java.util.concurrent.CountDownLatch;

// CountDownLatch 동기화 클래스 이용한, n개 스레드가 동시에 동작해 끝나는 작업 시간 측정
public class TestHarness {

    private static class TaskRunner implements Runnable {
        final Runnable task;
        final CountDownLatch startLatch;
        final CountDownLatch endLatch;

        private TaskRunner(Runnable task, CountDownLatch startLatch, CountDownLatch endLatch) {
            this.task = task;
            this.startLatch = startLatch;
            this.endLatch = endLatch;
        }

        @Override
        public void run() {
            try {
                // 동시 시작을 위해, start latch count가 0이 될 때까지 대기
                startLatch.await();
                try {
                    // 위임받은 작업 시작
                    task.run();
                } finally {
                    // 작업 종료 후 end latch countdown
                    endLatch.countDown();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public long estimateTaskTime(final int nThreads, final Runnable task) throws InterruptedException {

        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(nThreads);

        for (int i = 0; i < nThreads; i++) {
            new Thread(new TaskRunner(task, startLatch, endLatch)).start();
        }

        final long start = System.nanoTime();
        startLatch.countDown();
        endLatch.await();
        final long end = System.nanoTime();
        return end - start;
    }

    public static void main(String[] args) throws InterruptedException {
        TestHarness testHarness = new TestHarness();
        final long executedTime = testHarness.estimateTaskTime(50, new Runnable() {
            @Override
            public void run() {
                System.out.println("run task");
            }
        });
        System.out.println(executedTime);
    }


}
