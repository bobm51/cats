/* Name: FieldPair.java
 *
 * What;
 *  This file defines a data structure for pairing a Field value to the
 *  Name of the Field.
 */
package cats.layout.store;

import cats.common.Constants;
import cats.gui.store.ClassSpec;

/**
 *  This file defines a data structure for pairing a Field value to the
 *  Name of the Field.
 * <p>Title: CATS - Computer Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class FieldPair {

  /**
   * is the separator between the key and the value in toString()
   */
  public static final char SEP = '=';
  
  /**
   * is the Field Name.
   */
  public String FieldTag;

  /**
   * is the value of the Field.
   */
  public Object FieldValue;

  /**
   * the constructor.
   * @param tag is the name of the Field.
   * @param value is the value of the Field.
   */
  public FieldPair(String tag, Object value) {
    FieldTag = tag;
    FieldValue = value;
  }

  /**
   * strips a leading and trailing quote (") from a string
   * @param s is the string
   * @return the string with a single leading and trailing
   * quote removed.
   */
  private String stripQuotes(String s) {
      StringBuffer temp = new StringBuffer(s);
      if (temp.charAt(0) == '"') {
          temp.deleteCharAt(0);
      }
      if (temp.charAt(temp.length() - 1) == '"') {
          temp.setLength(temp.length() - 1);
      }
      return temp.toString();
  }
  
  /**
   * the ctor, given a single string.  The string contains a tag followed
   * by a "=", followed by the value of the pair.
   * @param pair is tag=value
   */
  public FieldPair(String pair) {
    int index = pair.indexOf(SEP);
    if (index > 0) {  // identified a tag field
        FieldTag = pair.substring(0, index);
        if (index < (pair.length() - 1)) {
            FieldValue = stripQuotes(pair.substring(index + 1));
        }
        else {
            FieldValue = "";
        }
    }
    else if (index == 0) {  // missing tag field
        FieldTag = "";
        if (pair.length() > 1) {
            FieldValue = stripQuotes(pair.substring(1));
        }
        else {
            FieldValue = "";
        }
    }
    else {  // missing separator
        FieldTag = new String(pair);
        FieldValue = "";
    }
  }
  
  /**
   * is called to ensure that FieldValue is an instance of the required Class.
   * If it is, then there are no changes.  If it is not, then it is set to the
   * "default" value for instances of the required Class.
   *
   * @param newClass is the class
   */
  public void verifyClass(Class<?> newClass) {
    if (newClass != null) {
      if (FieldValue == null) {
        FieldValue = new String("");
      }
      Class<? extends Object> myClass = FieldValue.getClass();
      if (Boolean.class.equals(newClass)) {
        if (myClass != Boolean.class) {
          if (myClass == String.class) {
            if (FieldValue.equals("true")) {
              FieldValue = new Boolean(true);
            }
            else {
              FieldValue = new Boolean(false);
            }
          }
          else {
            FieldValue = new Boolean(false);
          }
        }
      }
      else if (Integer.class.equals(newClass)) {
        if (myClass != Integer.class) {
          if (myClass == String.class) {
            try {
              FieldValue = Integer.valueOf((String) FieldValue);
            }
            catch (NumberFormatException nfe) {
              FieldValue = new Integer(0);
            }
          }
          else {
            FieldValue = new Integer(0);
          }
        }
      }
      else if (String.class.equals(newClass)) {
        if (myClass != String.class) {
          FieldValue = new String(FieldValue.toString());
        }
      }
      else if (cats.gui.store.ClassSpec.class.isAssignableFrom(newClass)) {
        if (newClass != myClass) {
          if (myClass == String.class) {
            if (FieldValue.equals("")) {
              FieldValue = String.class;
            }
            else {
              FieldValue = cats.gui.store.ClassSpec.toClass((String) FieldValue);                          
            }
          }
          else {
            FieldValue = String.class;
          }
        }
      }
      else if (cats.layout.store.StoredObject.class.isAssignableFrom(newClass)) {
        if (newClass != myClass) {
          if (myClass == String.class) {
            try {
              FieldValue = newClass.newInstance();
            } catch (InstantiationException e) {
              e.printStackTrace();
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
  }

  /**
   * converts the FieldPair to a String, of the form
   * "tag=value"
   * 
   * @return the FieldPair as a String
   */
  public String toString() {
    String value;
    if (FieldValue != null) {
      value = FieldValue.toString();
      if (Class.class.isInstance(FieldValue)) {
        value = ClassSpec.normalizeClassName(value);
      }
      return FieldTag + SEP + Constants.QUOTE + value + Constants.QUOTE;
    }
    return FieldTag+SEP+"\"\"";
  }
}
/* @(#)FieldPair.java */