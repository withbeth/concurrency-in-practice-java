package withbeth.me.ch5.blockingqueue;


import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class FileCrawler {

    private static class Crawler implements Runnable {

        private final BlockingQueue<File> queue;
        private final FileFilter fileFilter;
        private final File startRoot;

        public Crawler(BlockingQueue<File> queue, FileFilter fileFilter, File root) {
            this.queue = queue;
            this.fileFilter = fileFilter;
            this.startRoot = root;
        }

        @Override
        public void run() {
            try {
                crawl(startRoot);
            } catch (InterruptedException e) {
                // Q: 왜 예외 전파하지 않고 인터럽트 하고 끝내는지?
                // A:
                // - 애초에 run메서드가 해당예외를 던지지않기에, overriding한 이 메서드에서 전파할수 없다.
                // - 따라서, 여기서 할수 있는 최선은, 현재 스레드의 interrupt()를 호출하여,
                //   인터럽트 상태를 설정해, 상위 호출 메서드가 인터럽트 상황이 발생했음을 알리는 것뿐이다.
                Thread.currentThread().interrupt();
            }
        }

        private void crawl(File root) throws InterruptedException {
            File[] files = root.listFiles(fileFilter);
            if (files == null) {
                return;
            }
            for (File entry : files) {
                if (entry.isDirectory()) {
                    crawl(entry);
                }
                else if (!isAlreadyIndexed(entry)) {
                    System.out.println(Thread.currentThread().getName() + " thread is putting following file to the blocking queue : " + entry);
                    queue.put(entry);
                }
            }
        }

        private boolean isAlreadyIndexed(File entry) {
            return false;
        }
    }

    private static class Indexer implements Runnable {

        private final BlockingQueue<File> queue;

        public Indexer(BlockingQueue<File> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                // TODO : we can do better - 7장 참고
                while (true) {
                    indexFile(queue.take());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void indexFile(File take) {
            System.out.println(Thread.currentThread().getName() + " thread is indexing file : " + take);
        }
    }

    public static void startIndexing(
        File[] roots,
        final int queueSize,
        final int nConsumers
    ) {
        LinkedBlockingDeque<File> queue = new LinkedBlockingDeque<>(queueSize);
        FileFilter fileFilter = pathname -> true;

        // Start Producer thread per file root
        for (File root : roots) {
            new Thread(new Crawler(queue, fileFilter, root)).start();
        }

        // Start Consumer threads
        for (int i = 0; i < nConsumers; i++) {
            new Thread(new Indexer(queue)).start();
        }
    }

    public static void main(String[] args) {
        File[] files = new File("src").listFiles();
        System.out.println("Start indexing below files...");
        System.out.println(Arrays.toString(files));
        startIndexing(files, 10, 10);
    }

}
