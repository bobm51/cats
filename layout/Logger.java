/* Name: Logger.java
 *
 * What:
 *   This class defines a Singleton facility for logging Strings to a
 *   file.
 *
 * Special Considerations:
 *   To make the logging a background task, the actual writing is performed
 *   in a low priority thread.  Also, to keep the overhead down and provide
 *   some reliability, Strings are cached and written in bunches of 10, then
 *   the file is closed and reopened.
 */
package cats.layout;

import java.awt.Point;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.text.DateFormat;

import cats.common.Constants;
import cats.common.VersionList;
import cats.layout.store.AbstractStoreWatcher;

/**
 * is a Singleton for logging Strings to a file.  It is intended to log
 * events so that a timestampped record can be made of what happened
 * when during an operating session.  The results of this record can be
 * used for identifying bottlenecks in the schedule and improving the
 * schedule.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009, 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class Logger
    implements Runnable {
  
  /**
   * is the Singleton
   */
  private static Logger Singleton;
  
  /**
   * a FIFO for holding Strings, waiting to be written to disk.  Strings
   * are buffered so that there are fewer performance hits due to disk
   * activity.
   */
  private static Queue LogQue = new Queue();

  /**
   * is the update filter
   */
  private LogStoreWatcher Filter;
  
  /**
   * The asynchronous thread that periodically flushes the Queue
   * to disk.
   */
  public static Thread LogThread;

  /**
   * The following constants are tags for recognizing entries in the log
   * file.
   */

  /**
   * is the version identifier for the log file format.
   */
  public static final int VERSION = 4;
  
  /**
   * is the Version number of log.  It is used to keep fomr using
   * an obsolete log.
   */
  public static final String VERSION_TAG = "Version:";
  
  /**
   * is the message tag for the date of log creation.
   */
  public static final String CREATE_TAG = "Created:";

  /**
   * is the message tag for timestamping when the session ended.
   */
  public static final String FINISH_TAG = "Ended:";

  /**
   * is the marker that coordinates for a track follow.
   */
  public static final String TRACK_MARKER = "{}";

  /**
   * are the number of messages to accumulate before writing.
   */
  private final static int CACHE_SIZE = 10;

  /**
   * is the OS name of the file.
   */
  private String FileName;

  /**
   * is end of line delmiter.
   */
  private static final byte[] EOL = {
      '\r', '\n'};

  /**
   * The class constructor.
   */
  private Logger() {
  }

  /**
   * is the Singleton accessor.  Create an instance
   * of Logger, if one does not exist.
   * 
   * @return the Singleton.
   */
  public static Logger instance() {
    if (Singleton == null) {
      Singleton = new Logger();
    }
    return Singleton;
  }
  
  /* Name: void run()
   *
   * What:
   *   This is the Thread
   *
   * Inputs:
   *   There are none
   *
   * Returns:
   *   None
   *
   * Special Considerations:
   */
  public void run() {
    String buffer[] = new String[CACHE_SIZE];
    FileOutputStream outFile = null;
    int count;
    int i;
    boolean doit;
    while (true) {
      for (count = 0; count < CACHE_SIZE; ++count) {
        buffer[count] = (String) LogQue.get();
        if (buffer[count] == null) {
          break;
        }
      }
      doit = true;
      if (FileName != null) {
        try {
          outFile = new FileOutputStream(FileName, true);
        }
        catch (IOException except) {
          doit = false;
          System.out.println("Problem opening " + FileName);
          log.info("Problem opening " + FileName);
        }
        if (doit && (outFile != null)) {
          try {
            for (i = 0; i < count; ++i) {
              outFile.write(buffer[i].getBytes());
              outFile.write(EOL);
              buffer[i] = null;
            }
            // If quitting, write the final timestamp.
            if (count < (CACHE_SIZE - 1)) {
              outFile.write(new String(FINISH_TAG + Constants.FS +
                                       Calendar.getInstance().getTime()
                                       ).getBytes());
              outFile.write(EOL);
            }
            outFile.close();
          }
          catch (IOException except) {
            System.out.println("Write error on " + FileName);
            log.info("Write error on " + FileName);
          }
        }
      }
      if (count < (CACHE_SIZE - 1)) {
        break;
      }
    }
  }

  /**
   * is a method for assigning the file name to the log file.  The caller
   * is responsible for ensuring that the name is good.
   *
   * @param name is the name of the file.
   */
  public void setFileName(String name) {
    FileOutputStream outFile = null;
    FileName = name;
    boolean success = true;

    try {
      outFile = new FileOutputStream(FileName, true);
    }
    catch (IOException except) {
      success = false;
      System.out.println("Problem creating " + FileName);
      log.info("Problem creating " + FileName);
    }
    if (success && (outFile != null)) {
      try {
        outFile.write(new String(CREATE_TAG + Constants.FS +
            DateFormat.getInstance().format(Calendar.getInstance().getTime())
            + Constants.FS + VERSION_TAG + Constants.FS + VERSION + Constants.FS +
            Constants.CATS_TAG + Constants.FS_STRING + VersionList.CATS_VERSION).getBytes());
        outFile.write(EOL);
        outFile.close();
        Filter = new LogStoreWatcher();
        Filter.dumpAllStores();
        Filter.register();
      }
      catch (IOException except) {
        System.out.println("Write error on " + FileName);
        log.info("Write error on " + FileName);
      }
    }
  }

  /**
   * is a method for reporting if logging is turned on or not.
   *
   * @return true if logging is turned on and false if it is not.
   */
  public static boolean isLogging() {
    return (Singleton != null) && (Singleton.FileName != null);
  }

  /**
   * is the method called when the application shuts down.  It flushes
   * the queue.
   */
  static public void finishUp() {
    if (Logger.LogThread != null) {
      Logger.LogQue.flush();
      try {
        Logger.LogThread.join();
      }
      catch (InterruptedException ie) {
      }
    }
  }

  /**
   * creates a timestamped String.  A timestamped String is the
   * following:
   * <p>
   * header timestamp message
   * <p>
   * header is a tag identifying the kind of message, to assist in
   * post processing.
   * <p>
   * timestamp is HH:MM:SS, generated by this class.  It could be
   * real time or fast clock time.  If fast clock, it is marked with an
   * "*".
   * <p>
   * message is the message to be recorded.
   *
   * @param tag is the message tag which should be a static from this
   * class.
   * @param message is the message being recorded.
   * @return a time stamped String constructed from the two parameters
   */
  public static String timeStamp(String tag, String message) {
    String now = DateFormat.getTimeInstance().format(FastClock.TheClock.getTOD());
    String clkSource = (FastClock.TheClock.isFastTime()) ? "*" : " ";
//    LogQue.append(new String(tag + FS + now + clkSource + FS + message));
    return new String(tag + Constants.FS + now + clkSource + Constants.FS + message);
  }

  /**
   * creates a timestamped String using the time of day clock.
   * A timestamped String is the following:
   * <p>
   * header timestamp message
   * <p>
   * header is a tag identifying the kind of message, to assist in
   * post processing.
   * <p>
   * timestamp is HH:MM:SS, generated by this class.  It is the computer's
   * time of day clock.
   * <p>
   * message is the message to be recorded.
   *
   * @param tag is the message tag which should be a static from this
   * class.
   * @param message is the message being reccorded.
   */
  public static void realTimeStamp(String tag, String message) {
    String now = DateFormat.getTimeInstance().format(Calendar.getInstance().
        getTime());
    LogQue.append(new String(tag + Constants.FS + now + Constants.FS + message));
  }

  /**
   * formats the location of where a train moved to.
   * @param section is the coordinates in designer of the section
   * @param edge is the edge of the section the train moved to
   * @return a String recording the movement in a format expected
   *   by the replay manager.
   */
  public static String formatLocation(Point section, String edge) {
    String s = new String("(" + section.x + "," +
        section.y);
    if (edge == null) {
      return s +")";
    }
    return s + "):" + edge;
  }

  /**
   * is a hidden class for filtering and processing log messages
   */
  private class LogStoreWatcher extends AbstractStoreWatcher {

    /**
     * is the method that forwards the changes to
     * a logger or across the network.  This is the method
     * that is tailored to the concrete implementations.
     * 
     * @param msg is the status update in String form.
     */
    protected void forward(String msg) {
      LogQue.append(msg);
    }    
    /**
     * records that a record has been removed.  The logger
     * does not record them.
     * @param tag is the XML tag of the Store
     * @param data is the key field of the record removed
     */
//    public void removeData(String tag, String data) {
//    }
    
    /**
     * records the changes in the data.  The Logger ignores them.
     * @param tag is the XML tag of the Store
     * @param data is the new contents of the data
     */
//    public void changeData(String tag, String data) {
//    }
    
    /**
     * records status changes to properties not
     * in the Store.  For backwards compatibility,
     * the Logger uses the timeStamp routine for
     * everything but Job assignments.  The latter
     * use the realTimeStamp to record how long someone
     * actually worked the job.
     * 
     * @tag is a tag on the status
     * @param status is the change
     */
//    public void statusChange(String tag, String status) {
//      if (ASSIGN_TAG.equals(tag)) {
//        if (status.contains(RUNNING)) {
//          realTimeStamp(tag, status);
//        }
//        else {
//          timeStamp(tag, status);
//        }
//      }
//      else {
//        timeStamp(tag, status);
//      }
//     }
  }
  
  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      Logger.class.getName());
}
/* @(#)Logger.java */
