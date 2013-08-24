package co.zmc.projectindigo.managers;

import javax.swing.SwingWorker;

public class ServerManager extends SwingWorker<Boolean, Void> {
    private String _status;
    private int    _percentComplete;

    @Override
    protected Boolean doInBackground() {

        return true;
    }

    public String getStatus() {
        return _status;
    }

    public int getPercentComplete() {
        return _percentComplete;
    }

}
