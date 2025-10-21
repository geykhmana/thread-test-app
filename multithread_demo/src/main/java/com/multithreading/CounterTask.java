package com.multithreading;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;

public class CounterTask implements Runnable {
    private JProgressBar progressBar;
    private JLabel threadLabel;
    private AtomicInteger grandTotalCounter;
    private int interval;
    private CountDownLatch latch;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();

    public CounterTask(JProgressBar progressBar, JLabel threadLabel, int interval, AtomicInteger grandTotalCounter,
            CountDownLatch latch) {
        this.progressBar = progressBar;
        this.threadLabel = threadLabel;
        this.grandTotalCounter = grandTotalCounter;
        this.interval = interval;
        this.latch = latch;
    }

    public void pauseTask() {
        paused = true;
    }

    public void resumeTask() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i <= 100; i++) {
                final int currentValue = i;

                SwingUtilities.invokeLater(() -> {
                    progressBar.setValue(currentValue);
                    threadLabel.setText(String.valueOf(currentValue));
                });

                grandTotalCounter.incrementAndGet();
                Thread.sleep(interval);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            latch.countDown();
        }
    }

}
