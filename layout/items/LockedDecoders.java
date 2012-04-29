/**
 * Name: LockedDecoders.java
 *
 * What:
 *  This is a class for holding the commands that should not be sent to the
 *  layout because they would move turnouts that have routes through
 *  them.
 */
package cats.layout.items;

import java.util.Enumeration;
import java.util.Vector;

import cats.gui.TraceFactory;
import cats.gui.TraceFlag;

/**
 * is a class for holding the commands that should not be sent to the
 * layout because they would move turnouts that have routes through
 * them.
 * <p>
 * Though needed only by IOSpec and IOSpecChain, it is visible more
 * globally so that if needed, a GUI button could be used to flush it.
 * The reason is that once a decoder is "black listed", a turnout that
 * uses it cannot be moved.  So, if something goes wrong when clearing
 * a route, there may be no way to "unstick" the turnout.
 * <p>
 * In order to keep overhead down when looking for locked decoders,
 * this class maintains an internal collection.  The collection
 * contains an identifier and count for each locked decoder.  The
 * identifier is derived from the decoder system name and polarity.
 * Though an IOSpec would be more natural to use, multiple IOSpecs
 * could have the same system name and polarity, which is the reason
 * for the existence of this class.  The count is needed to record
 * how many turnouts have locked the decoder.  There could be more than
 * one.  When the turnout locks the decoder, the count is incremented
 * and when it is released, the count is decremented.  Because multiple
 * turnouts may lock a decoder address, a simple flag is not sufficient.
 * <p>
 * Because of a bug (whose cause I have not been able to find), sometimes
 * the lock is never cleared.  So, only the decoders that appear in
 * multiple turnouts are kept in the collection.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2006, 2007, 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class LockedDecoders {
  
  /**
   * is the Singleton.
   */
  static public LockedDecoders BlackList = null;

  /**
   * is the list of decoder ids.  This is a Vector
   * to leverage off the synchronization characteristic.
   */
  private Vector<BlockCount> Ids;
  
  /**
   * is where to find the value of the trace flag.
   */
  private static TraceFlag TraceLock = null;
  
  
  /**
   * is the ctor.
   */
  public LockedDecoders() {
    if (BlackList == null) {
      Ids = new Vector<BlockCount>();
      BlackList = this;
      TraceLock = TraceFactory.Tracer.createTraceItem("Decoder Locks",
          "LOCK_TRACE");
    }
  }

  /**
   * is called to construct an internal identifier from the decoder's
   * system name and command.
   * 
   * @param name is the system name of the decoder that the command
   * is sent to.
   * @param polarity is true if the command throws a turnout and
   * false if it closes a turnout.
   * @return the internal name.
   */
  private String createID(String name, boolean polarity) {
    return name + (polarity ? "_1" : "_0");
  }

  /**
   * searches the list of known decoder commands.
   * 
   * @param ident is the internal identifer for a decoder command
   * @return the data structure containing the lock count, if found
   * or null, if not found.
   */
  private BlockCount findCount(String ident) {
    BlockCount cand;
    for (int i = 0; i < Ids.size(); ++i) {
      cand = Ids.get(i);
      if (ident.equals(cand.decoderName)) {
        return cand;
      }
    }
    return null;
  }
  
  /**
   * locks the decoder.  If it is shared (on the black list)
   * the lock count is incremented.
   * 
   * @param name is the system name of the decoder
   * @param polarity is true if the command throws a turnout and
   * false if it closes a turnout.
   */
  public void lockUpDecoder(String name, boolean polarity){
    if ((name != null) && (name.length() > 0)) {
      String id = createID(name, polarity);
      BlockCount locks = findCount(id);
      if (locks != null){
        ++locks.count;
        if (TraceLock.getTraceValue()) {
          System.out.println("Locking decoder " +
              id + " to " + locks.count + " locks");        
        }
      }
    }
  }
  
  /**
   * unlocks the decoder.  If the decoder is on the black
   * list, this decrements the lock count.
   * 
   * @param name is the system name of the decoder
   * @param polarity is true if the command throws a turnout and
   * false if it closes a turnout.
   */
  public void releaseDecoder(String name, boolean polarity){
    if ((name != null) && (name.length() > 0)) {
      String id = createID(name, polarity);
      BlockCount locks = findCount(id);
      if ((locks != null) && (locks.count > 0)){
        --locks.count;
        if (TraceLock.getTraceValue()) {
          System.out.println("Removing decoder lock on decoder " +
              id + ": " + locks.count + " locks");        
        }
      }
      else if (TraceLock.getTraceValue()) {
        System.out.println("no locks to remove from decoder " + 
            id);        
      }
    }
  }
  
  /**
   * tests if the command should not be sent.
   * 
   * @param name is the system name of decoder the command will be sent to.
   * @param polarity is true if the command throws a turnout and
   * false if it closes a tunout.
   * @return true if the command should not be sent and false if it is
   * safe to send the command to the layout.
   */
  public boolean isLocked(String name, boolean polarity) {
    if ((name != null) && (name.length() > 0)) {
      String id = createID(name, polarity);
      BlockCount locks = findCount(id);
      if (locks != null){
        if (TraceLock.getTraceValue()) {
          System.out.println("Testing decoder lock on decoder " +
              id + ": " + locks.count + " locks");        
        }
        return (locks.count > 0);
      }
      else if (TraceLock.getTraceValue()) {
        System.out.println("Decoder " + id + " is not shared");        
      }
    }    
    return false;
  }
  
  /**
   * flushes all locks.  This is a safety net, in case some decoder is
   * not being cleared.
   */
  public void flushLocks() {
    for (Enumeration<BlockCount> iter = Ids.elements(); iter.hasMoreElements(); ) {
      iter.nextElement().count = 0;
    }
  }

  /**
   * unconditionally adds the decoder to the collection at start up.  If
   * the decoder is already in the collection, the count is incremented.
   * Later, all decoders with a single count will be removed, leaving only
   * shared decoders.
   *  
   * @param name is the system name of decoder the command will be sent to.
   * @param polarity is true if the command throws a turnout and
   * false if it closes a tunout.
   */
  public void registerDecoder(String name, boolean polarity) {
    if ((name != null) && (name.length() > 0)) {
      String id = createID(name, polarity);
      BlockCount locks = findCount(id);
      if (locks == null){
        Ids.add(new BlockCount(id));
      }
      else {
        ++locks.count;
      }
    }    
  }

  /**
   * walks through the collection of decoders.  All that have a count
   * of 1 are removed.  The rest are reset to a 0 count.
   */
  public void pruneSingles() {
    BlockCount lock;
    for (int i = (Ids.size() - 1); i >= 0; --i) {
      if ((lock = Ids.get(i)).count < 2) {
        Ids.remove(lock);
      }
      else {
        lock.count = 0;
      }
    }
  }
  
  /**
   * creates an association between a decoder name and a lock count.
   */
  private class BlockCount {
    int count;
    String decoderName;
    
    /**
     * the ctor.
     * @param name is the internal name for the decoder.
     */
    public BlockCount (String name) {
      decoderName = new String(name);
      count = 1;
    }
  }
}
/* @(#)LockedDecoders.java */