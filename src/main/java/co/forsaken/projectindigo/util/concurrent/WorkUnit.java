package co.forsaken.projectindigo.util.concurrent;

import java.util.Observable;
import java.util.Observer;

import lombok.Getter;

public class WorkUnit extends Observable implements ProgressObservable, Observer {

  @Getter private double       offset;
  @Getter private final double percentage;
  @Getter private final double interval;

  @Getter private double       total;

  private double               progress               = -1;
  private String               localizedStatus;
  private String               localizedTitle;
  private boolean              shouldConfirmInterrupt = false;

  public WorkUnit() {
    this.offset = 0;
    this.percentage = 0;
    this.interval = 0;
  }

  private WorkUnit(double offset, double percentage, double interval) {
    this.offset = offset;
    this.percentage = percentage;
    this.interval = interval;
  }

  public synchronized WorkUnit split(double percentage) {
    WorkUnit workUnit = new WorkUnit(total, percentage, 0);
    total += percentage;
    workUnit.addObserver(this);
    return workUnit;
  }

  public synchronized WorkUnit split(double totalPercentage, double count) {
    WorkUnit workUnit = new WorkUnit(total, totalPercentage / count, totalPercentage / count);
    total += totalPercentage;
    workUnit.addObserver(this);
    return workUnit;
  }

  public void advance() {
    offset += interval;
  }

  public double getProgress() {
    return progress;
  }

  public void setProgress(double progress) {
    this.progress = progress;
    setChanged();
    notifyObservers();
  }

  public String getLocalizedStatus() {
    return localizedStatus;
  }

  public void setLocalizedStatus(String localizedStatus) {
    this.localizedStatus = localizedStatus;
    setChanged();
    notifyObservers();
  }

  public void push(double progress, String localizedStatus) {
    this.progress = progress;
    this.localizedStatus = localizedStatus;
    setChanged();
    notifyObservers();
  }

  public String getLocalizedTitle() {
    return localizedTitle;
  }

  public void setLocalizedTitle(String localizedTitle) {
    this.localizedTitle = localizedTitle;
    setChanged();
    notifyObservers();
  }

  public boolean shouldConfirmInterrupt() {
    return shouldConfirmInterrupt;
  }

  public void setShouldConfirmInterrupt(boolean shouldConfirmInterrupt) {
    this.shouldConfirmInterrupt = shouldConfirmInterrupt;
    setChanged();
    notifyObservers();
  }

  public void update(Observable o, Object arg) {
    if (o instanceof ProgressObservable) {
      ProgressObservable worker = (ProgressObservable) o;
      WorkUnit workUnit;

      double progress = worker.getProgress();

      if (progress >= 0) {
        if (worker instanceof WorkUnit && (workUnit = (WorkUnit) worker).getPercentage() > 0) {
          progress = (worker.getProgress() * workUnit.getPercentage() + workUnit.getOffset()) / getTotal();
        }

        this.progress = progress;
      }

      this.localizedStatus = worker.getLocalizedStatus();
      this.localizedTitle = worker.getLocalizedTitle();
      this.shouldConfirmInterrupt = worker.shouldConfirmInterrupt();

      setChanged();
      notifyObservers();
    }
  }

}