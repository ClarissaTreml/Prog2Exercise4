package at.ac.fhcampuswien.newsanalyzer.downloader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallelDownloader extends Downloader{ //Strategy Pattern
    @Override
    public int process(List<String> urls) {
        int count = 0;

        int availableProcessors = Runtime.getRuntime().availableProcessors();//number of processors available

        ExecutorService pool = Executors.newFixedThreadPool(availableProcessors);//allows you to create a thread pool

        List<Callable<String>> callables = new ArrayList<>();
        for(int i = 0; i < urls.size(); i++){ // create tasks dynamically
            int idx = i;
            Callable<String> task = () -> saveUrl2File(urls.get(idx)); // pass the async function as a lambda
            callables.add(task); // pool.submit returns Future objects -> add all Future objects to array
        }

        //ERWEITERUNG -> Verbesserung des Exception Handling
        try {
            List<Future<String>> allFutures = pool.invokeAll(callables); //Future Interface erlaubt Variablen für laufende Threads zu definieren und
            // trotzdem im Ablauf des Programms fortzufahren.
            for(Future<String> f : allFutures){
                String result = f.get();
                if(result != null)
                    count++;
            }
        } catch (Exception e) {
            System.out.println("Error occured");
        }
        pool.shutdown();
        return count;
    }
}
