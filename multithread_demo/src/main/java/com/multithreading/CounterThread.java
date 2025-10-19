package com.multithreading;

import javax.swing.*;

public class CounterThread extends Thread {
    private JProgressBar progressBar;
    private JLabel threadTotal;
    private int grandTotal;
    private int interval;

    public CounterThread(JProgressBar progressBar, JLabel threadTotal, int grandTotal, int interval) {
        this.progressBar = progressBar;
        this.threadTotal = threadTotal;
        this.grandTotal = grandTotal;
        this.interval = interval;
    }

    @Override
    public void run() {
        for (int i = 0; i <= 100; i++) {
            final int currentValue = i;

            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(currentValue);
                threadTotal.setText(String.valueOf(currentValue));
            });

            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
