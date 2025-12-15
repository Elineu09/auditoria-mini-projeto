package audit.util;

public class Timer {
    private long start;
    private long end;

    public void start() { start = System.nanoTime(); }
    public void stop() { end = System.nanoTime(); }

    public double getElapsedSeconds() {
        return (end - start) / 1_000_000_000.0;
    }
}