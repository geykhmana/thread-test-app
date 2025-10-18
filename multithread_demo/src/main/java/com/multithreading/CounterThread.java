package com.multithreading;

public class CounterThread extends Thread {
    private int progressBar;
    private int threadTotal;
    private int grandTotal;
    private int interval;

    public CounterThread(int progressBar, int threadTotal, int grandTotal, int interval) {
        this.progressBar = progressBar;
        this.threadTotal = threadTotal;
        this.grandTotal = grandTotal;
        this.interval = interval;
    }

    @Override
    public void run() {

    }
}
