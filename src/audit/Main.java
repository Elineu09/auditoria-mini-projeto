package audit;

import audit.config.AppConfig;
import audit.sort.InMemorySorter;
import audit.sort.ExternalSorter;
import audit.util.MemoryMonitor;
import audit.util.Timer;

import java.nio.file.Files;

public class Main {
    public static void main(String[] args) throws Exception {
        AppConfig config = new AppConfig();
        
        // Verifica se ficheiro existe
        if (!Files.exists(config.getInputFile())) {
            System.err.println("ERRO: Ficheiro não encontrado: " + config.getInputFile());
            System.err.println("Por favor, coloque o ficheiro transactions.csv em data/input/");
            return;
        }
        
        Timer timer = new Timer();
        MemoryMonitor memory = new MemoryMonitor();

        timer.start();

        boolean canFitInMemory = memory.canLoadFileInMemory(config.getInputFile());

        if (canFitInMemory){//false) {
            System.out.println("[INFO] A usar ordenação em memória (TimSort)");
            InMemorySorter sorter = new InMemorySorter(config);
            sorter.sort();
        } else {
            System.out.println("[INFO] A usar External Sort (TimSort + K-way Merge)");
            ExternalSorter sorter = new ExternalSorter(config);
            sorter.sort();
        }

        timer.stop();
        System.out.println("Processamento concluído com sucesso!");
        System.out.println("Tempo total de execução: " + timer.getElapsedSeconds() + " segundos");
        System.out.println("Ficheiro gerado: " + config.getOutputFile());
    }
}