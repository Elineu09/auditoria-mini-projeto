package audit.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppConfig {
    private final Path inputFile = Paths.get("data/input/transactions.csv");
    private final Path outputFile = Paths.get("data/output/transactions_sorted.csv");
    private final Path tempDir = Paths.get("data/temp");

    private final int maxLinesPerChunk = 100_000;
    private final double memorySafetyFactor = 0.7;

    public AppConfig() {
        createDirectories();
    }

    private void createDirectories() {
        try {
            Files.createDirectories(outputFile.getParent());
            Files.createDirectories(tempDir);
        } catch (IOException e) {
            System.err.println("Erro ao criar diretórios: " + e.getMessage());
        }
    }

    public Path getInputFile() { return inputFile; }
    public Path getOutputFile() { return outputFile; }
    public Path getTempDir() { return tempDir; }
    public int getMaxLinesPerChunk() { return maxLinesPerChunk; }
    public double getMemorySafetyFactor() { return memorySafetyFactor; }
}