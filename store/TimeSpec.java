/* What: TimeSpec.java
 *
 * What:
 *  This class creates an object that is a time specification.
 *  <p>
 *  A time specification looks like hh:mm, where hh (00-23) specifies an
 *  hour and mm (00-59) specifies a minute.  Without a '+' or '-' prefix, the
 *  time is interpreted as a clock value, using the fast clock (if fast
 *  clock is selected in Appearance) or the time of day clock (if fast clock
 *  is not selected)
 *  <p>
 *  A '+' is interpreted as meaning the time is relative to the clock, when
 *  the operating session begins.  The base clock depends on the fast clock
 *  selection; thus, if a time specification is +00:30, a fast clock is not
 *  being used and the operating session starts at 1:00 pm, then the time
 *  specification refers to 1:30 pm.
 *  <p>
 *  A '-' is interpreted as meaning the time is relative to the clock, before
 *  the operating session begins.  The base clock depends upon the fast clock
 *  selection; thus, if a time specification is -02:00 and a fast clock is
 *  selected, and the fast clock always starts at 12:00 am, then the time
 *  specification refers to 10:00 pm on the fast clock, the previous day.
 *  <p>
 *  A value of all spaces is valid and interpreted as meaning the time
 *  specification is to be ignored.
 *  <p>
 *  There are three critical functions of this class:
 *  <ul>
 *  <li>a parser for identifying the blank string, + relative, - relative,
 *      and the hour and minute fields.
 *  <li>a method for formatting the value in a JTable.
 *  <li>a method for editing the value in a JTable.
 *  </ul>
 */
package cats.gui.store;

import cats.layout.FastClock;
import java.awt.Component;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import javax.swing.DefaultCellEditor;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *  This class creates an object that is a time specification.
 *  <p>
 *  A time specification looks like hh:mm, where hh (00-23) specifies an
 *  hour and mm (00-59) specifies a minute.  Without a '+' or '-' prefix, the
 *  time is interpreted as a clock value, using the fast clock (if fast
 *  clock is selected in Appearance) or the time of day clock (if fast clock
 *  is not selected)
 *  <p>
 *  A '+' is interpreted as meaning the time is relative to the clock, when
 *  the operating session begins.  The base clock depends on the fast clock
 *  selection; thus, if a time specification is +00:30, a fast clock is not
 *  being used and the operating session starts at 1:00 pm, then the time
 *  specification refers to 1:30 pm.
 *  <p>
 *  A '-' is interpreted as meaning the time is relative to the clock, before
 *  the operating session begins.  The base clock depends upon the fast clock
 *  selection; thus, if a time specification is -02:00 and a fast clock is
 *  selected, and the fast clock always starts at 12:00 am, then the time
 *  specification refers to 10:00 pm on the fast clock, the previous day.
 *  <p>
 *  A value of all spaces is valid and interpreted as meaning the time
 *  specification is to be ignored.
 *  <p>
 *  There are three critical functions of this class:
 *  <ul>
 *  <li>a parser for identifying the blank string, + relative, - relative,
 *      and the hour and minute fields.
 *  <li>a method for formatting the value in a JTable.
 *  <li>a method for editing the value in a JTable.
 *  </ul>
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2003, 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

/**
 * is a filter for selecting certain file suffixes.
 */
public class TimeSpec
    extends SpecialType {

  /**
   * is the help message for the Verifier.
   */
  private static final String Usage = "Time format is [+|-]hh:mm";

  /**
   * is an unknown time.
   */
  public static final int UNKNOWN_TIME = (-24) * 60;

  /**
   * is the TimeSpec that represents mid-night.
   */
  public static final String MIDNIGHT = "00:00";

  /**
   * is the specification.
   */
  private String Spec;

  /**
   * is the no argument constructor.
   */
  public TimeSpec() {
    this("");
  }

  /**
   * is the single argument constructor.
   *
   * @param spec is the TimeSpec in String form.
   */
  public TimeSpec(String spec) {
    Spec = new String(spec);
  }

  /*
   * is used for printing it's value in a JTextTextField, as well as the
   * common usage.
   *
   * @return the value of the TimeSpec, as a String.
   */
  public String toString() {
    return Spec;
  }

  /**
   * returns a JTableCellEditor for editing the time specification.
   *
   * @return the editor.
   */
  public DefaultCellEditor getCellEditor() {
    JFormattedTextField ftf = new JFormattedTextField(new TimeFormat());
    ftf.setColumns(10);
    ftf.setInputVerifier(new TimeVerifier(Usage));
    ftf.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
    return new FormattedCellEditor(ftf);
  }

  /**
   * returns a JTableCellRenderer for rendering the time specification.
   *
   * @return the renderer.
   */
  public TableCellRenderer getCellRenderer() {
    return new FormattedCellRenderer(new TimeFormat());
  }

  /**
       * converts a TimeSpec into the number of minutes since midnight.  The leading
   * sign is ignored.  So, the caller should know if the TimeSpec is absolute
   * or relative and if the latter, whether to add or subtract the minutes.
   * <p>
   * The method assumes that the String is correctly formatted and though it
   * does some format checking, it provides little diagnostics.
   *
   * @param timeString is a String, conforming to the TimeSpec format to be
   * converted.
   *
   * @return the number of minutes since (or before) mid-night requested by the
   * timeString.
   */
  private static int toMinutes(String timeString) {
    int hours = 0;
    int minutes = 0;
    int index = 0;

    if ( (timeString.charAt(0) == '-') || (timeString.charAt(0) == '+')) {
      index = 1;
    }
    if (Character.isDigit(timeString.charAt(index))) {
      hours = Character.digit(timeString.charAt(index++), 10);
    }
    else {
      return 0;
    }

    if (Character.isDigit(timeString.charAt(index))) {
      hours = (hours * 10) + Character.digit(timeString.charAt(index++), 10);
    }

    if (timeString.charAt(index++) != ':') {
      return 0;
    }

    if (Character.isDigit(timeString.charAt(index))) {
      minutes = Character.digit(timeString.charAt(index++), 10);
    }
    else {
      return 0;
    }

    if ( (index < timeString.length()) &&
        Character.isDigit(timeString.charAt(index))) {
      minutes = (minutes * 10) + Character.digit(timeString.charAt(index++), 10);
    }

    minutes = (hours * 60) + minutes;
    return minutes;
  }

  /**
   * is a conversion routine.  It converts a TimeSpec into the number of
   * minutes since midnight.  If the TimeSpec is absolute, then the conversion
   * is a simple (hours * 60) + minutes.  If the Timespec is relative, then
   * the TimeSpec is converted to minutes and added to the current time, where
   * the current time is the real time (no fastclock) or the fast clock
   * reading. So, if TimeSpec is negative, it is that long ago.  If TimeSpec
   * is positive, then it is that many minutes in the future.
   *
   * @param offset is a TimeSpec being converted to the number of minutes since
   * or before midnight.  It can be absolute, in which case base is ignored.
   * If it is relative, then the number of minutes is sign added to base.
   *
   * @param base is the base time to be used in computing a relative time.  It
   * can be null to force the current time.  The current time is either the
   * computer time or the fast clock time, if it is being used.
   *
   * @return TimeSpec as minutes.
   */
  public static int convertString(String offset, String base) {
    boolean negative = false;
    int minutes = toMinutes(offset);
    int baseTime;
    if (offset.charAt(0) == '-') {
      negative = true;
    }
    else if (offset.charAt(0) != '+') {
      return minutes;
    }

    // It is relative at this point, so we need to figure out what it is
    // relative to.
    if (base == null) {
      baseTime = currentTime();
    }
    else {
      baseTime = toMinutes(base);
    }
    return (negative) ? (baseTime - minutes) : (baseTime + minutes);
  }

  /**
   * converts an int assumed to be the number of minutes since (before)
   * midnight into a String of the form hh:mm.  If the minutes are negative,
   * they are subtracted from midnight.
   *
   * @param interval is the number of minutes since (before) midnight.
   *
   * @return a String in the absolute TimeSpec format.
   */
  public static String convertMinutes(int interval) {
    boolean negative = false;
    int hours;
    int minutes;
    String sMinutes;
    if (interval == UNKNOWN_TIME) {
      return new String("");
    }
    if (interval < 0) {
      negative = true;
      interval = -interval;
    }
    hours = interval / 60;
    minutes = interval % 60;
    if (negative) {
      hours = 23 - hours;
      minutes = 60 - minutes;
    }
    if (minutes < 10) {
      sMinutes = new String("0" + String.valueOf(minutes));
    }
    else {
      sMinutes = String.valueOf(minutes);
    }
    return new String(hours + ":" + sMinutes);
  }

  /**
   * calculates the number of minutes of the current time since midnight.
   * The current time is the computer clock if a fast clock is not being used,
   * or the fast clock reading.
   *
   * @return the current time as the number of minutes since midnight.
   */
  public static int currentTime() {
    String now;
    now = new SimpleDateFormat("HH:mm").format(FastClock.TheClock.getTOD());
    return toMinutes(now);
  }
}

/**
 * is an internal class for editing a TimeSpec.
 */
class FormattedCellEditor
    extends DefaultCellEditor {
  /**
   * @param timeField is the TimeField being formatted.
   */
	public FormattedCellEditor(final JFormattedTextField timeField) {
    super(timeField);
    timeField.removeActionListener(delegate);
    delegate = new EditorDelegate() {
      public void setValue(Object value) {
        timeField.setValue(value);
      }

      public Object getCellEditorValue() {
        return timeField.getValue();
      }
    };
    timeField.addActionListener(delegate);
    timeField.setBorder(null);
  }
}

/**
 * is an internal class for rendering a TimeSpec.
 */
class FormattedCellRenderer
    extends DefaultTableCellRenderer {
  protected Format tFormat;

  /**
   * @param format is the Format object that formats a TimeSpec
   */
  public FormattedCellRenderer(Format format) {
    tFormat = format;
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus, int nRow,
                                                 int nCol) {
    return super.getTableCellRendererComponent(table, value == null ? null :
                                               tFormat.format(value),
                                               isSelected,
                                               hasFocus, nRow, nCol);
  }
}

/**
 * is a class that handles parsing the TimeSpec input.
 */
class TimeFormat
    extends Format {
  /**
   * converts an Object to a String.
   */
  public StringBuffer format(Object obj, StringBuffer toAppendTo,
                             FieldPosition fieldPosition) {
    String str = obj.toString().trim();
    fieldPosition.setBeginIndex(toAppendTo.length());
    toAppendTo.append(str);
    fieldPosition.setEndIndex(toAppendTo.length());
    return toAppendTo;
  }

  /**
   * parses the input for one or two digits.
   *
   * @param input is the input String being parsed.
   * @param index is the next character being examined.
   *
   * @return the number of characters that are digits: 0 for
   * none, 1 for one, and 2 for two.
   */
  private int parse_1_2(String input, int index) {
    int result = 0;
    if ( (input.length() > index) && Character.isDigit(input.charAt(index))) {
      result = 1;
      if ( (input.length() > (index + 1)) &&
          Character.isDigit(input.charAt(index + 1))) {
        result = 2;
      }
    }
    return result;
  }

  /**
   * parses Text to produce an Object.
   */
  public Object parseObject(String text, ParsePosition parsePos) {
    char ch;
    int pos = 0;
    int matched;
    StringBuffer result = new StringBuffer(text.length());
    String units;
    if (text.length() == 0) {
      return text;
    }
    if (text.length() > 2) {
      ch = text.charAt(pos);
      if ( (ch == '+') || (ch == '-')) {
        result.append(ch);
        ++pos;
      }
      if ( (text.length() > (pos + 2)) &&
          ( (matched = parse_1_2(text, pos)) != 0)) {
        units = text.substring(pos, pos + matched);
        if (Integer.parseInt(units) < 24) {
          result.append(units);
          pos += matched;
          ch = text.charAt(pos);
          if ( (ch == ':') && (text.length() > (pos + 1))) {
            result.append(":");
            ch = text.charAt(++pos);
            if ( (matched = parse_1_2(text, pos)) != 0) {
              units = text.substring(pos, pos + matched);
              if (Integer.parseInt(units) < 60) {
                result.append(units);
                pos += matched;
                parsePos.setIndex(parsePos.getIndex() + pos);
                return result.toString();
              }
            }
          }
        }
      }
    }
    parsePos.setIndex(parsePos.getIndex() + pos);
    return null;
  }
}

/**
 * is an inner class that handles verifying the input.
 */
class TimeVerifier
    extends InputVerifier {
  /**
   * is the constructor
   *
   * @param errMsg is the usage message.
   */
  public TimeVerifier(String errMsg) {
  }

  /**
   * does the actual work of verifying a JFormattedTextField.
   *
   * @param input is the thing being verified.
   */
  public boolean verify(JComponent input) {
    if (! (input instanceof JFormattedTextField)) {
      return true;
    }
    JFormattedTextField jtf = (JFormattedTextField) input;
    JFormattedTextField.AbstractFormatter formatter = jtf.getFormatter();
    if (formatter != null) {
      try {
        formatter.stringToValue(jtf.getText());
      }
      catch (ParseException pe) {
        return false;
      }
    }
    return true;
  }

  public boolean shouldYieldFocus(JComponent input) {
    return verify(input);
  }
}
/* @(#)TimeSpec.java */
