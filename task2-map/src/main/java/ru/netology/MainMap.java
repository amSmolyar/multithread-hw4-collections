package ru.netology;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainMap {
    static final int N_ARRAY_NUMBER = 20000;
    static final int AVAILABLE_THREAD_CNT = Runtime.getRuntime().availableProcessors();
    static final int MAX_THREAD_CNT = (AVAILABLE_THREAD_CNT > 2) ? (AVAILABLE_THREAD_CNT - 2) : AVAILABLE_THREAD_CNT;

    public static void main(String[] args) throws InterruptedException {
        // ==========================    concurrentHashMap    ============================
        System.out.println("\n\n==================== concurrentHashMap ====================");

        Map<Integer, String> concurrentMap = new ConcurrentHashMap<>();
        long totalTime = mapReadWrite(concurrentMap);
        System.out.println("Время, затраченное на запись и чтение: " + totalTime);

        // ==========================    synchronizedMap    ============================
        System.out.println("\n\n==================== synchronizedMap ====================");

        Map<Integer, String> syncMap = Collections.synchronizedMap(new HashMap<>());
        totalTime = mapReadWrite(syncMap);
        System.out.println("Время, затраченное на запись и чтение: " + totalTime);
    }

    public static long mapReadWrite(Map<Integer, String> map) throws InterruptedException {
        long startTime = System.nanoTime();
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREAD_CNT);
        for (int cntThread = 0; cntThread < MAX_THREAD_CNT; cntThread++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    for (int ii = 0; ii < N_ARRAY_NUMBER; ii++) {
                        Integer currentValue = (int) Math.round(Math.random()*N_ARRAY_NUMBER);
                        String fromMap = map.get(currentValue);
                        map.put(currentValue, String.valueOf(currentValue));
                    }
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        long endTime = System.nanoTime();
        return (endTime - startTime);
    }
}
