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
            Object grandTotalSync, AtomicInteger grandTotalCounter, CounterThread... threads) {
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
            });
        });

        updater.start();
        return updater;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 100; i++) {
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
