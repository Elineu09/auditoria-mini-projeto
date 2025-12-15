package audit.io;

import audit.model.Transaction;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

public class CSVWriter {

    public static void write(Path file, List<Transaction> data) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.toFile()))) {
            for (Transaction t : data) {
                bw.write(t.toCSV());
                bw.newLine();
            }
        }
    }
}