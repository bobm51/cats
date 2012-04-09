/* Name: OccupancySpectrum.java
 *
 * What:
 *  This file defines a Singleton that is a crude spectrum analyzer of the
 *  duration of occupancy reports.  It divides time into buckets.  Detection blocks
 *  time how long an occupancy report exists and call this object's analyze method.
 *  The analyze method determines which bucket the report should be filed in and
 *  increments its counter.
 *  
 * Special Considerations:
 */
package cats.layout;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import cats.gui.CounterFactory;
import cats.gui.TraceFactory;
import cats.gui.TraceFlag;

/**
 *  This file defines a Singleton that is a crude spectrum analyzer of the
 *  duration of occupancy reports.  It divides time into buckets.  Detection blocks
 *  time how long an occupancy report exists and call this object's analyze method.
 *  The analyze method determines which bucket the report should be filed in and
 *  increments its counter.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class OccupancySpectrum {

  /**
   * are the buckets.  Each bucket is one second.  The first
   * bucket records the number of occupancy events between 0 and 1
   * second in duration, the second for between 1 and 2 seconds, etc.
   */
  private PegCounter[] Buckets;

  /**
   * is the Singleton
   */
  private static OccupancySpectrum Spectrum;

  /**
   * is the checkbox on the GUI
   */
  private TraceFlag EnableBox;
  
  /**
   * is the prefix on the JMRI User Name for the Memory Object recording
   * a peg count.
   */
  public static final String JMRI_NAME = "OccupancyLength_";
  
  /**
   * is the ctor
   */
  private OccupancySpectrum() {
    Buckets = new PegCounter[CounterFactory.CountKeeper.findSequence(CounterFactory.DEBOUNCETAG).getSequenceSize()];
    for (int i = 0; i < (Buckets.length - 1); ++i) {
      Buckets[i] = new PegCounter(JMRI_NAME + String.valueOf(i + 1));
    }
    Buckets[Buckets.length - 1] = new PegCounter(JMRI_NAME + "max");
    EnableBox = TraceFactory.Tracer.createTraceItem("Occupancy Duration",
      "OCCUPANCY_DURATION");
    EnableBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent arg0) {
        if (EnableBox.getFlagValue()) {
          restart();
        }
      }
    });
  }
  
  /**
   * is used to locate the singleton
   * 
   * @return the singleton.  If it does not exist, this method
   * creates it.
   */
  public static OccupancySpectrum instance() {
    if (Spectrum == null) {
      Spectrum = new OccupancySpectrum();
    }
    return Spectrum;
  }
  
  /**
   * classifies the duration length into a bucket.
   * 
   * @param duration is how long the occupancy report
   * lasted in milliseconds
   */
  public void classify(long duration) {
    int bucket = (int)(duration / 1000);
    if ((bucket < 0) || (bucket >= Buckets.length)) {
      bucket = Buckets.length - 1;
    }
    Buckets[bucket].bumpCounter();
  }
  
  /**
   * sets the counts in all buckets back to 0.
   */
  public void restart(){
    for (int i = 0; i < Buckets.length; ++i) {
      Buckets[i].zeroCounter();
    }
  }
  
  /**
   * retrieves the JCheckBox for enabling and disabling
   * analysis of the occupancy hold times
   * @return the JCheckBox that is used to configure the spectrum
   */
  public TraceFlag getCheckBox() {
    return EnableBox;
  } 
  
  /**
   * is called to query the state of analysis
   * @return true if occupancy durations are being recorded
   * and false if they are not.
   */
  public boolean isRecording() {
    return EnableBox.getFlagValue();
  }
}
/* @(#)OccupancySpectrum.java */