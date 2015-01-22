package co.forsaken.projectindigo.util.concurrent;

import java.util.Observer;

/**
 * Represents a task that can provide progress updates.
 */
public interface ProgressObservable {

  /**
   * Return the current progress of the task.
   *
   * @return a number between 0 and 1, inclusive, or -1 if indeterminate
   */
  double getProgress();

  /**
   * Return a localized title for this progress.
   *
   * @return a title, or null if none is available
   */
  String getLocalizedTitle();

  /**
   * Return a localized status message describing the current state of the task.
   *
   * @return a message, or null if none is available
   */
  String getLocalizedStatus();

  /**
   * Return whether attempts to cancel this task should first be confirmed with
   * the user, even if the user initiated the cancellation.
   *
   * @return true to suggest a confirmation
   */
  boolean shouldConfirmInterrupt();

  /**
   * Adds an observer to the set of observers for this object, provided that it
   * is not the same as some observer already in the set. The order in which
   * notifications will be delivered to multiple observers is not specified. See
   * the class comment.
   *
   * @param o
   *          an observer to be added.
   */
  void addObserver(Observer o);

  /**
   * Deletes an observer from the set of observers of this object. Passing
   * <CODE>null</CODE> to this method will have no effect.
   *
   * @param o
   *          the observer to be deleted
   */
  void deleteObserver(Observer o);

}
