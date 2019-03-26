/* Name: FastClock.java
 *
 * What;
 *  FastClock is a Singleton object with a boolean value.  It is set
 *  to true if all logging and timing is done via a fast clock and
 *  false if the real time clock is used.
 */
package cats.layout;

import cats.layout.xml.XMLEleFactory;
import cats.layout.xml.XMLEleObject;
import cats.layout.xml.XMLReader;

import java.awt.Component;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;
import jmri.InstanceManager;
import jmri.Timebase;

/**
 *  FastClock is a Singleton object with a boolean value.  It is set
 *  to true if all logging and timing is done via a fast clock and
 *  false if the real time clock is used.
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2005, 2018</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version 1.0
 */

public class FastClock
    implements XMLEleObject {

  /**
   * is the tag for identifying a FastClock Object in the XMl file.
   */
  static final String XML_TAG = "FASTCLOCK";

  /**
   * is the singleton.
   */
  public static FastClock TheClock;

  /**
   * records which clock (computer or layout) is being used.
   */
  private boolean UsingLayout;

  /**
   * is the fastclock provided by the layout.
   */
  private Timebase LayoutClock;

  /**
   * is the time at which the operating session began.  It will initially
   * be the start time on the computer clock, but if a fast clock is set,
   * it will change to the intial value of the fast clock.
   */
  private String StartingTime;

  /**
   * is the timestamp on a log entry.  It is used until the clock
   * is restarted or changed.
   */
  private Date RecordedTOD;
  /**
   * constructs a 1:1 fast clock (uses the computer time of day clock).
   */
  public FastClock() {
    this(false);
  }

  /**
   * the constructor.
   *
   * @param clock is the value of the FastClock setting.  True will trigger
   * a search for an existing fast clock.  False will use the computer
   * time of day clock.
   */
  public FastClock(boolean clock) {
    if (TheClock == null) {
      init();
      TheClock = this;
    }
    setTimeBase(clock);
    StartingTime = new SimpleDateFormat("HH:mm").format(getTOD());
    RecordedTOD= null;
  }

  /**
   * sets the fast clock flag.
   *
   * @param source is the new value.  It is true if the fast clock
   * is being used and false if the time of day clock is being used.
   */
  public void setTimeBase(boolean source) {
    UsingLayout = source;
    if (UsingLayout) {
      if (LayoutClock == null) {
        LayoutClock = InstanceManager.getDefault(jmri.Timebase.class);
        if (LayoutClock == null) {
          JOptionPane.showMessageDialog( (Component)null,
              "No fast clock was detected.  Be sure to start one.",
              "Fast Clock no Detected",
              JOptionPane.WARNING_MESSAGE
              );
        }
      }
    }
  }

  /**
   * returns the clock source.
   *
   * @return true if the timing is coming from the layout and false
   * if it is coming from the computer's time of day.
   */
  public boolean isFastTime() {
    return (UsingLayout && (LayoutClock != null));
  }

  /**
   * returns the Time of Day (TOD) based on the clock configured.  If timing
   * is supposed to use a fast clock, but one has not been found, this
   * code will try to find one.  It will use the computer time of day
   * clock if it is configured to do so, or a layout fast clock is not
   * found.
   * @return the time of day, which can be either computer
   * clock or fasttime.
   */
  public Date getTOD() {
    if (RecordedTOD == null) {
      if (UsingLayout) {
        if (LayoutClock == null) {
          if ( (LayoutClock = InstanceManager.getDefault(jmri.Timebase.class)) == null) {
            return Calendar.getInstance().getTime();
          }
        }
        return LayoutClock.getTime();
      }
      return Calendar.getInstance().getTime();
    }
    return RecordedTOD;
  }

  /**
   * returns the time the operating session started in HH:MM format.  If the
   * fast clock is enabled after CATS has loaded, then it will be the value of
   * the fast clock when enabled.
   *
   * @return the time the operating session began.
   */
  public String getStartTime() {
    return StartingTime;
  }

  /**
   * fixes the timestamp at a value and holds it there until it
   * is set tosomething else or the resumeClock() is called.  The
   * intent is that a replay file will set the clock on every entry
   * so that the old log can be merged into the current log.  When
   * the replay is complete, the clock will be resumed.
   * 
   * @param tod is the value to hold the clock at.  It should be
   * a legal DateFormat.format String, matching that generated in
   * Logger.timeStamp().
   * 
   * @see java.text.DateFormat
   * @see cats.layout.Logger
   */
  public void fixClock(String tod) {
    Date trialDate;
    try {
      trialDate = DateFormat.getTimeInstance().parse(tod);
      RecordedTOD = trialDate;
    } catch (ParseException e) {
      // do nothing
    }
  }
  
  /**
   * resumes using a running clock.
   *
   */
  public void resumeClock() {
    RecordedTOD = null;
  }
  
  
  /*
   * is the method through which the object receives the text field.
   *
   * @param eleValue is the Text for the Element's value.
   *
   * @return if the value is acceptable, then null; otherwise, an error
   * string.
   */
  public String setValue(String eleValue) {
    return new String("A " + XML_TAG + " cannot contain a text field ("
                      + eleValue + ").");
  }

  /*
   * is the method through which the object receives embedded Objects.
   *
   * @param objName is the name of the embedded object
   * @param objValue is the value of the embedded object
   *
   * @return null if the Object is acceptible or an error String
   * if it is not.
   */
  public String setObject(String objName, Object objValue) {
    String resultMsg = new String("A " + XML_TAG + " cannot contain an Element ("
                           + objName + ").");
    return resultMsg;
  }

  /*
   * returns the XML Element tag for the XMLEleObject.
   *
   * @return the name by which XMLReader knows the XMLEleObject (the
   * Element tag).
   */
  public String getTag() {
    return new String(XML_TAG);
  }

  /*
   * tells the XMLEleObject that no more setValue or setObject calls will
   * be made; thus, it can do any error chacking that it needs.
   *
   * @return null, if it has received everything it needs or an error
   * string if something isn't correct.
   */
  public String doneXML() {
    return null;
  }

  /**
   * registers a FastClock with the XMLReader.
   */
  static public void init() {
    XMLReader.registerFactory(XML_TAG, new FastClockFactory());
  }
}

/**
 * is a Class known only to the FastClock class for creating a true
 * value from an XML file.
 */
class FastClockFactory
    implements XMLEleFactory {

  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
  }

  /*
   * gives the factory an initialization value for the created XMLEleObject.
   *
   * @param tag is the name of the attribute.
   * @param value is it value.
   *
   * @return null if the tag:value are accepted; otherwise, an error
   * string.
   */
  public String addAttribute(String tag, String value) {
      return new String("A " + FastClock.XML_TAG +
                             " XML Element cannot have a " + tag +
                             " attribute.");
  }

  /*
   * tells the factory that the attributes have been seen; therefore,
   * return the XMLEleObject created.
   *
   * @return the newly created XMLEleObject or null (if there was a problem
   * in creating it).
   */
  public XMLEleObject getObject() {
    FastClock.TheClock.setTimeBase(true);
    return FastClock.TheClock;
  }
}
/* @(#)FastClock.java */
