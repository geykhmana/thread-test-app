package com.multithreading;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.*;

public class App extends JFrame {
    private JProgressBar[] progressBars;
    private JLabel[] threadLabels;
    private JLabel grandTotalLabel;
    private JButton startButton;

    public App() {
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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        startButton = new JButton("Start");
        startButton.setPreferredSize(new Dimension(100, 35));

        startButton.addActionListener(e -> {
            AtomicInteger grandTotalCounter = new AtomicInteger(0);
            Object grandTotalSync = new Object();
            grandTotalLabel.setText("0");

            CounterThread thread1 = new CounterThread(progressBars[0], threadLabels[0], grandTotalLabel, 25,
                    grandTotalSync, grandTotalCounter);
            CounterThread thread2 = new CounterThread(progressBars[1], threadLabels[1], grandTotalLabel, 50,
                    grandTotalSync, grandTotalCounter);
            CounterThread thread3 = new CounterThread(progressBars[2], threadLabels[2], grandTotalLabel, 100,
                    grandTotalSync, grandTotalCounter);
            CounterThread thread4 = new CounterThread(progressBars[3], threadLabels[3], grandTotalLabel, 150,
                    grandTotalSync, grandTotalCounter);

            thread1.start();
            thread2.start();
            thread3.start();
            thread4.start();

            CounterThread.startGrandTotalUpdater(grandTotalLabel, grandTotalSync, grandTotalCounter,
                    () -> startButton.setEnabled(true), thread1, thread2, thread3, thread4);

            startButton.setEnabled(false);
        });

        buttonPanel.add(startButton);

        bottomPanel.add(buttonPanel, BorderLayout.WEST);

        JPanel grandTotalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            App app = new App();
            app.setVisible(true);
        });
    }
}
