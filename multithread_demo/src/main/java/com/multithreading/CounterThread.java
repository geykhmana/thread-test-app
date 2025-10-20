package com.multithreading;

import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;

public class CounterThread extends Thread {
    private JProgressBar progressBar;
    private JLabel threadTotalLabel;
    private JLabel grandTotalLabel;
    private int interval;
    private AtomicInteger grandTotalCounter;
    private Object grandTotalSync;

    private volatile boolean paused = false;
    private final Object pauseLock = new Object();

    public CounterThread(JProgressBar progressBar, JLabel threadTotalLabel, JLabel grandTotalLabel, int interval,
            Object grandTotalSync,
            AtomicInteger grandTotalCounter) {
        this.progressBar = progressBar;
        this.threadTotalLabel = threadTotalLabel;
        this.grandTotalLabel = grandTotalLabel;
        this.interval = interval;
        this.grandTotalSync = grandTotalSync;
        this.grandTotalCounter = grandTotalCounter;
    }

    public static Thread startGrandTotalUpdater(JLabel grandTotalLabel,
            Object grandTotalSync, AtomicInteger grandTotalCounter, Runnable onComplete, CounterThread... threads) {
        Thread updater = new Thread(() -> {
            boolean allFinished = false;
            while (!allFinished) {
                synchronized (grandTotalSync) {
                    try {
                        SwingUtilities.invokeAndWait(() -> {
                            grandTotalLabel.setText(String.valueOf(grandTotalCounter.get()));
                        });

                        Thread.sleep(50);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                allFinished = true;
                for (CounterThread thread : threads) {
                    if (thread.isAlive()) {
                        allFinished = false;
                        break;
                    }
                }
            }

            SwingUtilities.invokeLater(() -> {
                grandTotalLabel.setText(String.valueOf(grandTotalCounter.get()));
                if (onComplete != null) {
                    onComplete.run();
                }
            });
        });

        updater.start();
        return updater;
    }

    public void pauseThread() {
        paused = true;
    }

    public void resumeThread() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

    @Override
    public void run() {
        for (int i = 1; i <= 100; i++) {
            synchronized (pauseLock) {
                while (paused) {
                    try {
                        pauseLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }

            final int currentValue = i;

            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(currentValue);
                threadTotalLabel.setText(String.valueOf(currentValue));
            });

            grandTotalCounter.incrementAndGet();

            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
