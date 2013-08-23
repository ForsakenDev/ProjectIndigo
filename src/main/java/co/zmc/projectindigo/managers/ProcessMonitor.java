package co.zmc.projectindigo.managers;

public class ProcessMonitor implements Runnable {

    private final Process    proc;
    private final Runnable   onComplete;

    private volatile boolean complete = false;

    private ProcessMonitor(Process proc, Runnable onComplete) {
        this.proc = proc;
        this.onComplete = onComplete;
    }

    public void run() {
        try {
            proc.waitFor();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        complete = true;
        onComplete.run();
    }

    public static ProcessMonitor create(Process proc, Runnable onComplete) {
        ProcessMonitor processMonitor = new ProcessMonitor(proc, onComplete);
        Thread monitorThread = new Thread(processMonitor);
        monitorThread.start();
        return processMonitor;
    }
}
