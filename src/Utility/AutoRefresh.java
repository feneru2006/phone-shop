package Utility;


import javax.swing.Timer;

public class AutoRefresh {
    private Timer timer;
    private Runnable task;

    public AutoRefresh(int intervalMs, Runnable task) {
        this.task = task;
        this.timer = new Timer(intervalMs, e -> {
            if (this.task != null) {
                this.task.run();
            }
        });
    }

    public void start() {
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    public void stop() {
        if (timer.isRunning()) {
            timer.stop();
        }
    }

    public void setInterval(int intervalMs) {
        timer.setDelay(intervalMs);
    }

    public boolean isRunning() {
        return timer.isRunning();
    }
}