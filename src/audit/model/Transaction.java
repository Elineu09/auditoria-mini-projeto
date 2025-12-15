package audit.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Transaction implements Comparable<Transaction> {
    private final String clientId;
    private final double amount;
    private final long timestamp;        // Para ordenação
    private final String originalTimestamp; // Para manter formato original

    public Transaction(String clientId, double amount, long timestamp, String originalTimestamp) {
        this.clientId = clientId;
        this.amount = amount;
        this.timestamp = timestamp;
        this.originalTimestamp = originalTimestamp;
    }

    public String getClientId() { return clientId; }
    public double getAmount() { return amount; }
    public long getTimestamp() { return timestamp; }
    public String getOriginalTimestamp() { return originalTimestamp; }

    @Override
    public int compareTo(Transaction o) {
        return Long.compare(this.timestamp, o.timestamp);
    }

    public String toCSV() {
        // Usa o formato original do timestamp
        return clientId + ",$" + String.format("%.2f", amount) + "," + originalTimestamp;
    }

    /**
     * Cria uma Transaction parseando o timestamp string
     */
    public static Transaction create(String clientId, String amountStr, String timestampStr) 
            throws IllegalArgumentException {
        double amount = Double.parseDouble(amountStr.trim().replace("$", ""));
        long timestamp = parseTimestamp(timestampStr);
        return new Transaction(clientId.trim(), amount, timestamp, timestampStr.trim());
    }

    /**
     * Converte diferentes formatos de timestamp para long (para ordenação)
     * Suporta:
     * - Timestamp Unix (milissegundos): 1234567890123
     * - Hora (HH:MM:SS ou H:MM:SS): 22:52:52 ou 9:03:19
     * - Data/hora ISO: 2024-12-15T22:52:52
     */
    private static long parseTimestamp(String timestampStr) throws IllegalArgumentException {
        timestampStr = timestampStr.trim();
        
        // Tentar parse direto como número (timestamp Unix)
        try {
            return Long.parseLong(timestampStr);
        } catch (NumberFormatException e) {
            // Não é um número, tentar outros formatos
        }
        
        // Tentar parse como hora (HH:MM:SS, H:MM:SS, HH:M:SS, etc.)
        if (timestampStr.contains(":")) {
            try {
                // Adiciona zero à esquerda se necessário para formato HH:MM:SS
                String[] parts = timestampStr.split(":");
                if (parts.length == 3) {
                    String normalized = String.format("%02d:%02d:%02d",
                        Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]));
                    
                    LocalTime time = LocalTime.parse(normalized);
                    // Converter para milissegundos desde meia-noite
                    return time.toSecondOfDay() * 1000L;
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Formato de hora inválido: " + timestampStr);
            }
        }
        
        // Tentar parse como data/hora ISO (2024-12-15T22:52:52)
        try {
            return java.time.Instant.parse(timestampStr).toEpochMilli();
        } catch (DateTimeParseException e) {
            // Não é ISO datetime
        }
        
        throw new IllegalArgumentException("Formato de timestamp não reconhecido: " + timestampStr);
    }
}