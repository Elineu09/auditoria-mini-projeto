package audit.util;

import java.nio.file.*;

public class MemoryMonitor {

    public boolean canLoadFileInMemory(Path file) throws Exception {
        long fileSize = Files.size(file);
        long maxMemory = Runtime.getRuntime().maxMemory();
        return fileSize < maxMemory * 0.7;
    }
}