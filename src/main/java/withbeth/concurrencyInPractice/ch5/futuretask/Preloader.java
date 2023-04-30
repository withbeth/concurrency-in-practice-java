package withbeth.concurrencyInPractice.ch5.futuretask;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Preloader {

    private static class ProductInfo {
        private final String name;

        private ProductInfo(String name) {
            this.name = name;
        }
    }

    private final FutureTask<ProductInfo> futureTask =
        new FutureTask<>(new Callable<ProductInfo>() {
            @Override
            public ProductInfo call() throws DataLoadException {
                return loadProductInfo();
            }
        });

    private final Thread thread = new Thread(futureTask);

    private ProductInfo loadProductInfo() {
        System.out.println("loading product info from db...");
        return new ProductInfo("item");
    }

    public void start() {
        thread.start();
    }

    public ProductInfo get() throws InterruptedException {
        try {

            return futureTask.get();

        } catch (ExecutionException e) {
            // Cause might be DataLoadException or RuntimeException or Error or Unknown
            Throwable cause = e.getCause();

            // `Callable`이 던지는 예외는 catch
            if (cause instanceof DataLoadException) {
                throw (DataLoadException) cause;
            }
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new IllegalArgumentException("Could not identify exception", cause);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Preloader preloader = new Preloader();
        // 작업이 필요한 시점 전에 미리 요청
        preloader.start();

        // ... some logic

        // 해당 작업 시점에 호출
        preloader.get();
    }

}
