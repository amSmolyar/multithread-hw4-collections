package ru.netology;

import java.util.concurrent.*;

public class MainCallCenter {
    static final int PROCESS_LIFETIME = 10000;          // milliseconds (время работы)
    static final long CALL_INITIAL_DELAY = 1000;        // milliseconds
    static final long CALL_PERIOD = 1000;               // milliseconds
    static final int TIME_FOR_ANSWER = 3000;            // milliseconds

    static BlockingQueue<String> callQueue;
    static int callNumber = 1;

    public static void main(String[] args) throws InterruptedException {

        callQueue = new ArrayBlockingQueue<>(100, true);

        // поток - генератор звонков:
        ScheduledExecutorService executorServiceCaller = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> callerFuture = executorServiceCaller.scheduleAtFixedRate(() -> {
            try {
                System.out.println("Поступил звонок номер " + callNumber);
                callQueue.put("Звонок " + callNumber++);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }, CALL_INITIAL_DELAY, CALL_PERIOD, TimeUnit.MILLISECONDS);

        executorServiceCaller.schedule(() -> callerFuture.cancel(true), PROCESS_LIFETIME, TimeUnit.MILLISECONDS);

        // пул потоков специалистов:
        ExecutorService executorServiceSpec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        Runnable specRun = () -> {
            String data;
            try {
                data = callQueue.take();
                Thread.sleep(TIME_FOR_ANSWER);
                System.out.println(data + " обработан");
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        };

        while (!executorServiceCaller.isTerminated()) {
            if (!callQueue.isEmpty())
                executorServiceSpec.submit(specRun);
        }

        executorServiceCaller.shutdown();
        executorServiceSpec.shutdown();
    }
}
