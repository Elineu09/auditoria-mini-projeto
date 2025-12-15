package audit.sort;

import audit.model.Transaction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class MergeManager {

    private static class Node {
        Transaction tx;
        BufferedReader reader;

        Node(Transaction tx, BufferedReader reader) {
            this.tx = tx;
            this.reader = reader;
        }
    }

    public static void merge(List<Path> chunks, Path output) throws Exception {
        if (chunks.isEmpty()) {
            System.err.println("ERRO: Nenhum chunk para fazer merge!");
            return;
        }
        
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingLong(n -> n.tx.getTimestamp()));
        List<BufferedReader> readers = new ArrayList<>();
        int totalMerged = 0;

        try (BufferedWriter bw = Files.newBufferedWriter(output)) {
            // Adiciona header ao output
            bw.write("clientId,amount,timestamp");
            bw.newLine();

            // Inicializa leitores
            for (Path p : chunks) {
                BufferedReader br = Files.newBufferedReader(p);
                readers.add(br);
                String line = br.readLine();
                if (line != null && !line.trim().isEmpty()) {
                    // Parse correto
                    int lastComma = line.lastIndexOf(',');
                    if (lastComma > 0) {
                        String timestampPart = line.substring(lastComma + 1);
                        String remaining = line.substring(0, lastComma);
                        int secondLastComma = remaining.lastIndexOf(',');
                        
                        if (secondLastComma > 0) {
                            String clientId = remaining.substring(0, secondLastComma);
                            String amountPart = remaining.substring(secondLastComma + 1);
                            
                            try {
                                pq.add(new Node(Transaction.create(clientId, amountPart, timestampPart), br));
                            } catch (Exception e) {
                                System.err.println("Erro ao ler chunk: " + line);
                            }
                        }
                    }
                }
            }

            System.out.println("Priority queue inicializada com " + pq.size() + " elementos");

            // K-way merge
            while (!pq.isEmpty()) {
                Node n = pq.poll();
                bw.write(n.tx.toCSV());
                bw.newLine();
                totalMerged++;
                
                if (totalMerged % 10000 == 0) {
                    System.out.println("Merged: " + totalMerged + " linhas...");
                }

                String line = n.reader.readLine();
                if (line != null && !line.trim().isEmpty()) {

                    int lastComma = line.lastIndexOf(',');
                    if (lastComma > 0) {
                        String timestampPart = line.substring(lastComma + 1);
                        String remaining = line.substring(0, lastComma);
                        int secondLastComma = remaining.lastIndexOf(',');
                        
                        if (secondLastComma > 0) {
                            String clientId = remaining.substring(0, secondLastComma);
                            String amountPart = remaining.substring(secondLastComma + 1);
                            
                            try {
                                pq.add(new Node(Transaction.create(clientId, amountPart, timestampPart), n.reader));
                            } catch (Exception e) {
                                System.err.println("Erro ao ler linha: " + line);
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Total de linhas merged: " + totalMerged);

        // Fecha todos os leitores
        for (BufferedReader br : readers) {
            br.close();
        }
        
        // Apaga ficheiros temporários
        for (Path p : chunks) {
            try {
                Files.deleteIfExists(p);
            } catch (IOException e) {
                System.err.println("Erro ao eliminar chunk: " + e.getMessage());
            }
        }
    }
}