package ch3.reodering;

public class NoVisibility {
    private static boolean ready;
    private static int number;
    private static class ReaderThread extends Thread {
        @Override
        public void run() {
            while (!ready) {
                // 스레드 실행준비가 안됬다면 다른 스레드에게 양보
                Thread.yield();
            }
            System.out.println(number);
        }
    }

    // Main Thread
    public static void main(String[] args) {
        new ReaderThread().start();
        number = 42;
        ready = true;
    }

}
