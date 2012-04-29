/* Name: ReplayHandler.java
 *
 * What:
 *   This class defines a Singleton facility for reading back an
 *   operating session recording and replaying it in much reduced
 *   time.  It is intended to be used in the event CATS crashes
 *   to restore the operating session to somewhere close to its
 *   state at the time of the crash.
 *
 * Special Considerations:
 *   For this to work right, the layout must be the same as when
 *   CATS crashed.  In other words, the XML file should not be
 *   edited from the one recorded.  The recording must have started
 *   from the beginning.
 * 
 */
package cats.layout.replay;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import cats.common.Constants;
import cats.layout.FastClock;
import cats.layout.Logger;
import cats.layout.store.FieldPair;
import cats.layout.store.GenericRecord;

/**
 *   This class defines a Singleton facility for reading back an
 *   operating session recording and replaying it in much reduced
 *   time.  It is intended to be used in the event CATS crashes
 *   to restore the operating session to somewhere close to its
 *   state at the time of the crash.
 *
 * Special Considerations:
 *   For this to work right, the layout must be the same as when
 *   CATS crashed.  In other words, the XML file should not be
 *   edited from the one recorded.  The recording must have started
 *   from the beginning.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2008, 2009, 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class ReplayHandler {
  
  /**
   * is a flag used for debugging this
   */
  static final boolean Debug = false;

  /**
   * is the string indicating the user does not want to continue.
   */
  static private final String CANCEL = "cancel";
  
  /**
   * is true if the timestamps from the log file should be
   * used to set the logger clock; false to use the current time.
   */
  static private boolean adjustClock;
 
  /**
   * is the parser to be used for replaying the log.
   */
  static private ReplayStrategy CurrentStrategy;
  
  /**
   * reads a log file and replays the events logged.
   * @param recording is the log file
   * @return null if the playback had no problems or an
   * error message if there was a problem.
   */
  static public String playback(File recording) {
    String resultMsg = null;
    BufferedReader reader;
    String line;
    String tag;
    String errMsg = null;
    StringTokenizer tokens;
    adjustClock = true;
    try {
      reader = new BufferedReader(new FileReader(recording));
      while ((line = reader.readLine()) != null) {
        tokens = new StringTokenizer(line, Constants.FS_STRING);
        if (Debug) {
          System.out.println(line);
        }
        tag = tokens.nextToken();
        if (tag.startsWith(Logger.CREATE_TAG)) {
          resultMsg = processCreate(tokens);
          if (resultMsg != null) {
            if (resultMsg == CANCEL) {
              resultMsg = null;
            }
            break;
          }
        }
        else if (null == CurrentStrategy) {
          log.info("Missing create event - cannot select a parser");
          break;
        }
        else {
          if (Constants.ASSIGN_TAG.equals(tag)) {
            errMsg = CurrentStrategy.processAssign(tokens);
          }
          else if (Logger.FINISH_TAG.equals(tag)) {
            CurrentStrategy.processFinish();
          }
          else if (Constants.MOVE_TAG.equals(tag)) {
            errMsg = CurrentStrategy.processMove(tokens);
          }
          else if (Constants.RERUN_TAG.equals(tag)) {
            errMsg = CurrentStrategy.processRerun(tokens);
          }
          else if (Constants.TERMINATED_TAG.equals(tag)) {
            errMsg = CurrentStrategy.processTerminated(tokens, Constants.TERMINATED_TAG);
          }
          else if (Constants.TIEDDOWN_TAG.equals(tag)) {
            errMsg = CurrentStrategy.processTerminated(tokens, Constants.TIEDDOWN_TAG);
          }
          else if (Constants.OOS_TAG.equals(tag)) {
            errMsg = CurrentStrategy.processOOS(tokens);
          }
          else if (Constants.TNT_TAG.equals(tag)) {
            errMsg = CurrentStrategy.processTNT(tokens);
          }
          else if (Constants.ADD_TO_STORE.equals(tag)) {
            errMsg = CurrentStrategy.processAddRecord(tokens);
          }
          else if (Constants.CHANGE_STORE.equals(tag)) {
            errMsg = CurrentStrategy.processChangeRecord(tokens);
          }
          else if (Constants.REMOVE_FROM_STORE.equals(tag)) {
            errMsg = CurrentStrategy.processDeleteRecord(tokens);
          }
          else {
            errMsg = new String("Unrecognized log entry: \"" + line + Constants.QUOTE);
          }
          if (null != errMsg) {
            System.out.println(errMsg);
            log.info(errMsg);
          }
        }
      }
      if (CurrentStrategy != null) {
        processFinish();
      }
      reader.close();
    }
    catch (FileNotFoundException nfne) {
      resultMsg = recording + " was not found";
    }
    catch (IOException ie) {
      resultMsg = recording + " could not be read.";
    }
    return resultMsg;
  }

  /**
   * The first record.  It contains a date stamp on the file and
   * the log file's format identifier.
   * @param tokens is a tokenized String, containing the timestamp
   * and version of the log.
   * @return null if everything went well or an error message if
   * the version is bad.
   */
  static private String processCreate(StringTokenizer tokens) {
    String start = new String();
    String s;
    int rev;
    while (tokens.hasMoreTokens() && 
        !Logger.VERSION_TAG.equals(s = tokens.nextToken())) {
      start = start.concat(s + " ");
    }
    if (tokens.hasMoreTokens()) { 
      rev = Integer.parseInt(tokens.nextToken());
      if (2 == rev) {
        CurrentStrategy = new ReplayVersion2();
      }
      else if (3 <= rev) {
        CurrentStrategy = new ReplayVersion3();
      }
      if (CurrentStrategy != null) {
        int result = JOptionPane.showConfirmDialog((Component) null,
            "Keep timestamps of replay log?", "keep timestamps?",
            JOptionPane.YES_NO_CANCEL_OPTION);
        if (result == JOptionPane.CANCEL_OPTION) {
          CurrentStrategy = null;
          return CANCEL;
        }
        adjustClock = (result == JOptionPane.YES_OPTION);
        return null;
      }
      return "Incompatible log file (version = " + rev + ").  Log was ignored.";
    }
    return "Truncated creation record.";
  }

  /**
   * All log records have been read.  Cleanup.
   */
  static void processFinish() {
    if (adjustClock) {
      FastClock.TheClock.resumeClock();
      CurrentStrategy = null;
    }
  }

  /**
   * determines if the timestamp on the log entry should be used
   * to temporarily set the master clock and if so, sets it.  The
   * intent is that by setting the clock, if logging is enabled,
   * the replay log can be merged into the new log.
   * @param stamp is a String containing the timestamp from
   * the replay log.
   */
  static void handleTimeStamp(String stamp) {
    if (adjustClock) {
      FastClock.TheClock.fixClock(stamp);
    }
  }
  
  /**
   * extract the first two tokens from the input.  They are the
   * timestamp.
   * @param tokens is the tokenized String.
   * @return the timestamp or null.
   */
  static String getTimeStamp(StringTokenizer tokens) {
    if (tokens.countTokens() >= 1) {
      return tokens.nextToken();
    }
    return null;
  }
  
  /**
   * fetches a quote delimited string from the StringTokenizer.  The
   * StringTokenizer should be positioned at a FieldSeparator, which will
   * be skipped.
   * 
   * @param tokens is the StringTokenizer
   * @return the quoted string (without the quotes) if one is found or
   * null.
   */
  static String fetchQuotedString(StringTokenizer tokens) throws NoSuchElementException {
      String s = tokens.nextToken();
      if (s.startsWith(Constants.QUOTE)) {
          while ((s.length() < 2) && !s.endsWith(Constants.QUOTE)) {
              s = s.concat(tokens.nextToken());
          }
      }
      if (s.length() < 3) {
          return "";
      }
      return s.substring(1, s.length() - 1);
  }
  
  /**
   * constructs an AbstractRecord of FieldPairs from the tokenized String
   * There will be one FieldPair per token.
   * Because of the FieldPair constructor, format checking is very loose.
   * @param tokens is the tokenized String
   * @param rec is the blank AbstractRecord
   */
  static void toFieldPairs(StringTokenizer tokens, GenericRecord rec) {
      while (tokens.hasMoreTokens()) {
          rec.add(new FieldPair(tokens.nextToken()));
      }
  }

  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      ReplayHandler.class.getName());
  
}
/* @(#)ReplayHandler.java */
