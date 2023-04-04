package withbeth.me.ch5.futuretask;

public final class ExceptionUtils {

    private ExceptionUtils() {}

    public static RuntimeException launderThrowable(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            return (RuntimeException) throwable;
        }
        if (throwable instanceof Error) {
            throw (Error) throwable;
        }
        throw new IllegalArgumentException("Could not identify exception", throwable);
    }
}
