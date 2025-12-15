package audit.sort;

import audit.config.AppConfig;
import audit.io.CSVReader;
import audit.model.Transaction;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

public class InMemorySorter {
    private final AppConfig config;

    public InMemorySorter(AppConfig config) {
        this.config = config;
    }

    public void sort() throws Exception {
        List<Transaction> data = CSVReader.readAll(config.getInputFile());
        Collections.sort(data); // TimSort
        
        // Adiciona header
        try (BufferedWriter bw = Files.newBufferedWriter(config.getOutputFile())) {
            bw.write("clientId,amount,timestamp");
            bw.newLine();
            for (Transaction t : data) {
                bw.write(t.toCSV());
                bw.newLine();
            }
        }
    }
}