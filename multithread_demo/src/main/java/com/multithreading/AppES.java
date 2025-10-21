package com.multithreading;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.*;
import javax.swing.*;

public class AppES extends JFrame {
    private ExecutorService executorService;
    private CountDownLatch completionLatch;
    private JProgressBar[] progressBars;
    private JLabel[] threadLabels;
    private JLabel grandTotalLabel;
    private JButton startButton;
    private JButton pauseButton;
    private JButton resumeButton;
    private CounterTask[] tasks;

    public AppES() {
        setTitle("Thread Test Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new GridLayout(4, 1, 10, 15));

        progressBars = new JProgressBar[4];
        threadLabels = new JLabel[4];

        for (int i = 0; i < 4; i++) {
            JPanel rowPanel = new JPanel(new BorderLayout(10, 0));

            JLabel label = new JLabel((i + 1) + ":");
            label.setFont(new Font("Arial", Font.BOLD, 16));
            label.setPreferredSize(new Dimension(30, 30));
            rowPanel.add(label, BorderLayout.WEST);

            progressBars[i] = new JProgressBar(0, 100);
            progressBars[i].setStringPainted(false);
            progressBars[i].setValue(0);
            rowPanel.add(progressBars[i], BorderLayout.CENTER);

            threadLabels[i] = new JLabel("0");
            threadLabels[i].setFont(new Font("Arial", Font.BOLD, 16));
            threadLabels[i].setPreferredSize(new Dimension(50, 50));
            threadLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            rowPanel.add(threadLabels[i], BorderLayout.EAST);

            progressPanel.add(rowPanel);
        }

        mainPanel.add(progressPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 0));
        startButton = new JButton("Start");
        startButton.setPreferredSize(new Dimension(100, 35));
        pauseButton = new JButton("Pause");
        pauseButton.setPreferredSize(new Dimension(100, 35));
        resumeButton = new JButton("Resume");
        resumeButton.setPreferredSize(new Dimension(100, 35));

        executorService = Executors.newFixedThreadPool(5); // thread pool, 4 for the counters and 1 for the grand total
                                                           // updater

        tasks = new CounterTask[4];

        startButton.addActionListener(e -> {
            AtomicInteger grandTotalCounter = new AtomicInteger(0);
            Object grandTotalSync = new Object();
            grandTotalLabel.setText("0");

            for (int i = 0; i < 4; i++) {
                progressBars[i].setValue(0);
                threadLabels[i].setText("0");
            }

            // Count down to all counter tasks finishing
            completionLatch = new CountDownLatch(4);

            tasks[0] = new CounterTask(progressBars[0], threadLabels[0], 25,
                    grandTotalCounter, completionLatch);
            tasks[1] = new CounterTask(progressBars[1], threadLabels[1], 50,
                    grandTotalCounter, completionLatch);
            tasks[2] = new CounterTask(progressBars[2], threadLabels[2], 100,
                    grandTotalCounter, completionLatch);
            tasks[3] = new CounterTask(progressBars[3], threadLabels[3], 150,
                    grandTotalCounter, completionLatch);

            for (CounterTask task : tasks) {
                executorService.submit(task);
            }

            startButton.setEnabled(false);

            executorService.submit(() -> {
                try {
                    while (completionLatch.getCount() > 0) {
                        synchronized (grandTotalSync) {
                            SwingUtilities.invokeAndWait(
                                    () -> grandTotalLabel.setText(String.valueOf(grandTotalCounter.get())));
                        }
                    }
                } catch (Exception ex) { // e already used as the event above
                    ex.printStackTrace();
                }
            });
        });

        pauseButton.addActionListener(e -> {
            for (CounterTask task : tasks) {
                if (task != null) {
                    task.pauseTask();
                }
            }
        });

        resumeButton.addActionListener(e -> {
            for (CounterTask task : tasks) {
                if (task != null) {
                    task.resumeTask();
                }
            }
        });

        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resumeButton);

        bottomPanel.add(buttonPanel, BorderLayout.WEST);

        JPanel grandTotalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        JLabel gtLabel = new JLabel("Grand Total:");
        gtLabel.setFont(new Font("Arial", Font.BOLD, 16));
        grandTotalPanel.add(gtLabel);

        grandTotalLabel = new JLabel("0");
        grandTotalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        grandTotalLabel.setPreferredSize(new Dimension(80, 35));
        grandTotalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        grandTotalPanel.add(grandTotalLabel);

        bottomPanel.add(grandTotalPanel, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    public JProgressBar getJProgressBar(int index) {
        return progressBars[index];
    }

    public JLabel getThreadLabel(int index) {
        return threadLabels[index];
    }

    public JLabel getGrandTotalLabel() {
        return grandTotalLabel;
    }

    public JButton getStartButton() {
        return startButton;
    }

    @Override
    public void dispose() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppES app = new AppES();
            app.setVisible(true);
        });
    }
}
