/*
 * Name: LocalEnforcement.java
 * 
 * What:
 *  LocalEnforcement is a Singleton object with a boolean value.  It is set
 *  to true if CATS should countermand local operation of a turnout when not
 *  permitted.  Local operation is not permitted when
 *  <ul>
 *  <li>the control discipline is CTC or DTC and
 *  <li>track authority has not been granted and
 *  <li>the boolean value is true
 *  </ul>
 *  When the above conditions are met and CATS sees a turnout moving, it
 *  will resend the last command sent to the turnout; thus, overriding the
 *  local operation.
 *  <p>
 *  By default, the boolean is false, for backwards compatibility.
 *   
 * Special Considerations:
 */
package cats.gui;

/**
 *  LocalEnforcement is a Singleton object with a boolean value.  It is set
 *  to true if CATS should countermand local operation of a turnout when not
 *  permitted.  Local operation is not permitted when
 *  <ul>
 *  <li>the control discipline is CTC or DTC and
 *  <li>track authority has not been granted and
 *  <li>the boolean value is true
 *  </ul>
 *  When the above conditions are met and CATS sees a turnout moving, it
 *  will resend the last command sent to the turnout; thus, overriding the
 *  local operation.
 *  <p>
 *  By default, the boolean is false, for backwards compatibility.
 * <p>Title: cats</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class LocalEnforcement extends BooleanGui {
  /**
   * is the tag for identifying a LocalEnforcement Object in the XML file.
   */
  static final String XMLTag = "LOCALENFORCEMENT";
  
  /**
   * is the label on the JCheckBoxMenuItem
   */
  static final String Label = "Reverse Local Operations";

  /**
   * is the singleton.
   */
  public static LocalEnforcement TheEnforcementType;

  /**
   * constructs the factory.
   */
  public LocalEnforcement() {
    super(Label, XMLTag, false);
    TheEnforcementType = this;
  }

}
/* @(#)LocalEnforcement.java */