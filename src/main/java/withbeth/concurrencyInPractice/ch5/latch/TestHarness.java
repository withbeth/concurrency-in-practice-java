package withbeth.concurrencyInPractice.ch5.latch;

import java.util.concurrent.CountDownLatch;

public class TestHarness {

    public long measureExecutionTime(
        final int nThreads,
        final Runnable task
    ) throws InterruptedException {
        // Start latch as a binary latch
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(nThreads);

        for (int i = 0; i < nThreads; i++) {
            Thread thread = createStartWaitingThread(startLatch, endLatch, task);
            thread.start();
        }

        final long start = System.nanoTime();
        startLatch.countDown();
        endLatch.await();
        final long end = System.nanoTime();
        return end - start;
    }

    private Thread createStartWaitingThread(
        final CountDownLatch startLatch,
        final CountDownLatch endLatch,
        final Runnable task
    ) {
        return new Thread(() -> {
            try {
                startLatch.await();
                try {
                    task.run();
                } finally {
                    endLatch.countDown();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    public static void main(String[] args) throws InterruptedException {
        TestHarness testHarness = new TestHarness();
        long executedTime = testHarness.measureExecutionTime(10, () ->
            System.out.println("running task..."));
        System.out.println(executedTime / Math.pow(10, 9));

    }

}
