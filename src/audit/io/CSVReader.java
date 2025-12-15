package audit.io;

import audit.model.Transaction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {

    public static List<Transaction> readAll(Path file) throws IOException {
        List<Transaction> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file.toFile()))) {
            String line;
            int lineNumber = 0;
            
            while ((line = br.readLine()) != null) {
                lineNumber++;
                
                if (line.trim().isEmpty()) continue;

                if (lineNumber == 1 && line.toLowerCase().contains("clientid")) {
                    continue;
                }
                
                int lastComma = line.lastIndexOf(',');
                if (lastComma == -1) {
                    System.err.println("Linha " + lineNumber + " inválida (sem vírgula): " + line);
                    continue;
                }
                
                String timestampPart = line.substring(lastComma + 1);
                String remaining = line.substring(0, lastComma);
                
                int secondLastComma = remaining.lastIndexOf(',');
                if (secondLastComma == -1) {
                    System.err.println("Linha " + lineNumber + " inválida (formato incorreto): " + line);
                    continue;
                }
                
                String clientId = remaining.substring(0, secondLastComma);
                String amountPart = remaining.substring(secondLastComma + 1);
                
                try {
                    list.add(Transaction.create(clientId, amountPart, timestampPart));
                } catch (Exception e) {
                    System.err.println("Linha " + lineNumber + " inválida: " + line + " - Erro: " + e.getMessage());
                }
            }
        }
        System.out.println("Total de transações carregadas: " + list.size());
        return list;
    }
}