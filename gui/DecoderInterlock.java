/*
 * Name: DecoderInterlock.java
 * 
 * What:
 *  DecoderInterlock is a Singleton object with a boolean value.  It is set
 *  to true if CATS should perform interlocking at the decoder level.
 *  That means, CATS will remember when would move a decoder for a
 *  turnout that is locked and not allow any other turnout to move, if
 *  it would send one of those commands.  This is needed only if some
 *  turnouts are ganged together.  If not, this flag should be false
 *  because moving a turnout will be a little faster and there is
 *  no danger of a turnout being locked with no way to unlock.
 */
package cats.gui;

import java.awt.event.ActionEvent;

import cats.layout.items.LockedDecoders;

/**
 *  DecoderInterlock is a Singleton object with a boolean value.  It is set
 *  to true if CATS should perform interlocking at the decoder level.
 *  That means, CATS will remember when would move a decoder for a
 *  turnout that is locked and not allow any other turnout to move, if
 *  it would send one of those commands.  This is needed only if some
 *  turnouts are ganged together.  If not, this flag should be false
 *  because moving a turnout will be a little faster and there is
 *  no danger of a turnout being locked with no way to unlock.
 * <p>Title: cats</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class DecoderInterlock
extends BooleanGui {

  /**
   * is the tag for identifying a DecoderInterlock Object in the XMl file.
   */
  static final String XMLTag = "DECODERINTERLOCK";
  
  /**
   * is the label on the JCheckBoxMenuItem
   */
  static final String Label = "Lock Turnout Decoders";

  /**
   * is the singleton.
   */
  public static DecoderInterlock TheInterlockType;

  /**
   * constructs the factory.
   */
  public DecoderInterlock() {
    super(Label, XMLTag, false);
    TheInterlockType = this;
  }

  /**
   * is the ActionListener for setting or clearing the flag.
   */
  public void actionPerformed(ActionEvent arg0) {
    if (!getState()) {
      LockedDecoders.BlackList.flushLocks();
    }
  }
}
/* @(#)DecoderInterlock.java */