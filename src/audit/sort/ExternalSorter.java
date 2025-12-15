package audit.sort;

import audit.config.AppConfig;
import audit.model.Transaction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExternalSorter {
    private final AppConfig config;

    public ExternalSorter(AppConfig config) {
        this.config = config;
    }

    public void sort() throws Exception {
        List<Path> chunks = splitAndSort();
        MergeManager.merge(chunks, config.getOutputFile());
    }

    private List<Path> splitAndSort() throws Exception {
        List<Path> chunks = new ArrayList<>();
        List<Transaction> buffer = new ArrayList<>();
        int index = 0;

        try (BufferedReader br = Files.newBufferedReader(config.getInputFile())) {
            String line;
            int lineNumber = 0;
            
            while ((line = br.readLine()) != null) {
                lineNumber++;
                
                if (line.trim().isEmpty()) continue;
                
                if (lineNumber == 1 && line.toLowerCase().contains("clientid")) {
                    continue;
                }
                
                String[] p = line.split(",");
                if (p.length != 3) {
                    System.err.println("Linha " + lineNumber + " inválida (formato incorreto): " + line);
                    continue;
                }
                
                try {
                    buffer.add(Transaction.create(p[0], p[1], p[2]));
                } catch (Exception e) {
                    System.err.println("Linha " + lineNumber + " inválida: " + line + " - Erro: " + e.getMessage());
                    continue;
                }

                if (buffer.size() >= config.getMaxLinesPerChunk()) {
                    chunks.add(writeChunk(buffer, index++));
                    buffer.clear();
                }
            }
            
            if (!buffer.isEmpty()) {
                chunks.add(writeChunk(buffer, index));
            }
        }
        System.out.println("Total de chunks criados: " + chunks.size());
        return chunks;
    }

    private Path writeChunk(List<Transaction> buffer, int index) throws Exception {
        Collections.sort(buffer);
        Path chunk = config.getTempDir().resolve("chunk_" + index + ".csv");
        
        try (BufferedWriter bw = Files.newBufferedWriter(chunk)) {
            for (Transaction t : buffer) {
                bw.write(t.toCSV());
                bw.newLine();
            }
        }
        return chunk;
    }
}