/*
 * Name: AspectMap.java
 *
 * What:
 *  This file contains a data structure for associating Indications (the
 *  meaning of signals) with aspects (the presentation of signals).
 */

package cats.layout;

import cats.layout.items.Track;
import cats.layout.xml.*;
import java.util.Enumeration;
import java.util.StringTokenizer;

/**
 *  A class for associating Indications (the
 *  meaning of signals) with aspects (the presentation of signals).
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class AspectMap
    implements XMLEleObject {

  /**
   * is the tag for identifying an AspectMap in the XML file.
   */
  static final String XML_TAG = "ASPECTMAP";

  /**
   * is the XML tag for the approach attribute.
   */
  static final String XML_APPROACH = "APPROACH";
  
  /**
   * is the value of the Approach attribute, when approach is enabled.
   */
  static final String ISAPPROACH = "true";
  
  /**
   * is the tag for the advance signal attribute
   */
  static final String XML_ADVANCE = "ADVANCE";
  
  /**
   * is the separator between heads states for each indication.  It is a
   * String, even though it is a single character, so that it can be
   * passed to the Streamtokenzier without alteration.
   */
  static final String SEP = "|";

  /**
   * The following describes all the possible indications supported
   * by the dispatcher panel.  The list represents 4 speeds (normal,
   * limited, medium, slow), with 2 block (the protected block and
   * the following block).
   */
  public static final String[][] IndicationNames = {
      {
      "ARA 281 - Clear", "R281"}
      , {
      "ARA 281B - Normal Approach Limited", "R281B"}
      , {
      "ARA 282 - Normal Approach Medium", "R282"}
      , {
      "ARA 284 - Normal Approach Slow", "R284"}
      , {
      "ARA 285 - Approach", "R285"}
      , {
      "ARA 281C - Limited Clear", "R281C"}
      , {
      "CROR 412 - Limited Approach Limited", "C412"}
      , {
      "CROR 413 - Limited Approach Medium", "C413"}
      , {
      "CROR 414 - Limited Approach Slow", "C414"}
      , {
      "ARA 281D - Limited Approach", "R281D"}
      , {
      "ARA 283 - Medium Clear", "R283"}
      , {
      "CROR 417 - Medium Approach Limited", "C417"}
      , {
      "ARA 283A - Medium Approach Medium", "R283A"}
      , {
      "ARA 283B - Medium Approach Slow", "R283B"}
      , {
      "ARA 286 - Medium Approach", "R286"}
      , {
      "ARA 287 - Slow Clear", "R287"}
      , {
      "CROR 422 - Slow Approach Limited", "C422"}
      , {
      "CROR 423 - Slow Approach Medium", "C423"}
      , {
      "CROR 424 - Slow Approach Slow", "C424"}
      , {
      "ARA 288 - Slow Approach", "R288"}
      , {
      "Advance Approach", "ADV_NORM"}
      , {
      "Advance Limited Approach", "ADV_LIM"}
      , {
      "Advance Medium Approach", "ADV_MED"}
      , {
      "Advance Slow Approach", "ADV_SLO"}
      , {
      "ARA 290 - Restricting", "R290"}
      , {
      "ARA 291 - Stop and Proceed", "R291"}
      , {
      "ARA 292 - Stop", "R292"}
  };

  /**
   * the lookup table to translate speeds into Rules.
   */
  private static int[][] SpeedTable;

  /**
   * The index into IndicationName of the dialog label.
   */
  public static final int LABEL = 0;

  /**
   * the index into IndicationName of the XML tag.
   */
  public static final int XML = 1;

  /**
   * the mapping of indication to lights/semaphore position.
   * There is one rule (indication) per row and one signal
   * head per column.
   */
  private String[][] Map;

  /**
   * the number of heads.  This should be 0 (for an object which has
   * been instantiated, but not initialized) or a positive.  Once set to
   * the latter, then all indications should have the same number of
   * heads (columns).
   */
  private int NumHeads;

  /**
   * is true if the signal uses approach lighting; false if it doesn't.
   * The default is false.  Approach lighting means the signal is lit
   * only when the block facing the signal is occupied.
   */
  private boolean HasApproach;

  /**
   * is true if the signal can display an Advanced indication; false
   * if it does not.  To be conmpatible with older versions, the default
   * is false.
   */
  private boolean HasAdvance;
  
  /**
   * constructs the table of signal aspects for each indication.
   */
  public AspectMap() {
    Map = new String[IndicationNames.length][];
    NumHeads = 0;
    HasApproach = false;
    HasAdvance = false;
  }

  /**
   * constructs the table of signal aspects for each indication from
   * an array of strings.
   *
   * @param settings is an array of Strings.  Each entry is one indication.
   * An entry could be null, meaning there is no indication for it.  Each
   * string should have the same number of SEP separated substrings,
   * for each substring describes the head's state for that indication.
   *
   * @param app is true if the signal has approach lighting and false if
   * it doesn't.
   * 
   * @param adv is true if the signal can display advanced indications and
   * false if it cannot.
   */
  public AspectMap(String[] settings, boolean app, boolean adv) {
    this();
    if (settings.length == IndicationNames.length) {
      StringTokenizer st;
      String[] indication;
      int heads;
      int position;

      HasApproach = app;
      HasAdvance = adv;
      // Extract the composite Strings from each indication.
      for (int ind = 0; ind < IndicationNames.length; ++ind) {
        if (settings[ind] != null) {
          // Find out how many heads are represented by counting separators.
          heads = 1;
          position = 0;

          while (position < settings[ind].length()) {
            if (settings[ind].charAt(position) == SEP.charAt(0)) {
              ++heads;
            }
            ++position;
          }
          st = new StringTokenizer(settings[ind], SEP);
          indication = new String[heads];
          heads = 0;
          while (st.hasMoreElements()) {
            indication[heads] = st.nextToken();
            // Flush the separator.
            if (!indication[heads].equals(SEP)) {
              ++heads;
            }
          }
          addAspect(ind, indication);
        }
      }
    }
    else {
      System.out.println(
          "Error in creating an AspectMap - wrong number of indications:"
          + settings.length);
    }
  }

  /**
   * retrieves the number of heads in the Aspect.
   *
   * @return the number of heads contributing to the aspect.
   */
  public int getHeadCount() {
    return NumHeads;
  }

  /**
   * retrieves the signal presentation for a particular head for a particular
   * indication.
   *
   * @param ind is the index of the indication.
   * @param head is the head.
   *
   * @return a String describing the Light/Semaphore.  Null is legal if the
   * light/semaphore does not exist or has no value.
   */
  public String getPresentation(int ind, int head) {
    if ( (ind >= 0) && (ind < IndicationNames.length) && (head >= 0) &&
        (head < NumHeads)) {
      if ( (Map[ind] != null) && (Map[ind][head] != null)) {
        return new String(Map[ind][head]);
      }
    }
    return null;
  }

  /**
   * retrieves the set of all presentations defined for a head.
   *
   * @param head is the number of the head.
   *
       * @return an Enumeration over the Strings describing the expected presentation
   * of the head or null, if there are none.
   */
//  public Enumeration getState(int head) {
//    if ( (head >= 0) && (head < NumHeads)) {
//      return new HeadEnumeration(head);
//    }
//    return null;
//  }

  /**
   * fills out a row in the Map, based on index.
   *
   * @param index is where in the array to place the Signal information.
   * @param aspects is the signal information
   */
  public void addAspect(int index, String[] aspects) {
    int heads;
    if (aspects == null) {
      Map[index] = null;
    }
    else {
      heads = aspects.length;
      Map[index] = new String[heads];
      if (NumHeads == 0) {
        NumHeads = heads;
      }
      else if (NumHeads != heads) {
        System.out.println("Inconsistent number of heads for an AspectMap:"
                           + " expecting " + NumHeads + " received " +
                           heads);
        heads = 0;
      }
      for (int a = 0; a < heads; ++a) {
        Map[index][a] = new String(aspects[a]);
      }
    }
  }

  /**
   * returns the approach flag.
   *
   * @return true if the signal should be dark unless the facing block
   * is occupied; false if it shoul dbe lit at all times.
   */
  public boolean getApproachLight() {
    return HasApproach;
  }

  /**
   * returns the Advanced flag.
   * @return true if the signal has aspects for advanced approach
   * indicaitons.
   */
  public boolean getAdvanced() {
    return HasAdvance;
  }
  
  /**************************************************************************
   * An inner class.
   ************************************************************************/

  /**
   * is an inner class for providing an Enumeration over the Strings
   * describing the presentation of a head.  This is not thread safe.
   */
  class HeadEnumeration
      implements Enumeration<String> {

    /**
     * is the number of the head being traversed.
     */
    int EnumHead;

    /**
     * is the Indication that will be given out next.
     */
    int EnumInd;

    /**
     * constructs the Enumeration.
     * @param head is the number of the SignalHead
     */
    HeadEnumeration(int head) {
      EnumHead = head;
      EnumInd = search(0);
    }

    /**
     * indicates if all the presentations have been given out.
     *
     * @return true if nextElement() has been called for all the Indications or
     * false if there are some remaining.
     */

    public boolean hasMoreElements() {

      return (EnumInd < IndicationNames.length);
    }

    /**
     * returns the next String that has not been given out.
     *
     * @return the next String, starting with the current, that has not been
     * given out.
     */
    public String nextElement() {
      int result = EnumInd;
      EnumInd = search(EnumInd + 1);
      if (result < IndicationNames.length) {
        return Map[result][EnumHead];
      }
      return null;
    }

    /**
     * finds the next String that has not been given out.
     *
     * @param start is the index of the place to start looking from.
     *
     * @return a the index of a String if one is found which is not at a
     * lower index or IndicationNames.length, if none is found.
     */
    private int search(int start) {
      int ind;
      while (start < IndicationNames.length) {
        if ( (Map[start] != null) && (Map[start][EnumHead] != null)) {
          // Let's see if the String is at a lower indication.
          for (ind = 0; ind < start; ++ind) {
            if ( (Map[ind] != null) && (Map[ind][EnumHead] != null) &&
                Map[ind][EnumHead].equals(Map[start][EnumHead])) {
              break;
            }
          }
          if (start == ind) {
            break;
          }
        }
        ++start;
      }
      return start;
    }
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
    return new String("A " + XML_TAG + " cannot contain an Element ("
                      + objName + ").");
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
   * does a table lookup for a rule, based on the block being protected
   * and the following block.
   *
   * @param near is the speed through the block being protected
   * @param next is the speed through the next block.  It may be -1.
   *
   * @return an index into IndicationNames of the rule governing the
   * indication.  Rule 292 (STOP) is returned if either index is
   * out of bounds.
   */
  public static int getRule(int near, int next) {

    if ((near < 0) || (near > Track.TrackSpeed.length)) {
      near = findRule("R292");
    }
    if ((next < 0) || (next > Track.TrackSpeed.length)) {
      next = Track.STOP;
    }
    return SpeedTable[near][next];
  }

  /**
   * searches IndicationNames for the index of a Rule.
   *
   * @param rule is the rule being searched for
   *
   * @return the index in IndicationNames where it is found.  If
   * not found, -1 is returned.
   */
  public static int findRule(String rule) {
    for (int r = 0; r < IndicationNames.length; ++r) {
      if (IndicationNames[r][XML].equals( rule)) {
        return r;
      }
    }
    System.out.println("Could not find the rule named " + rule);
    return -1;
  }

  /**
   * registers an AspectMapFactory with the XMLReader.
   */
  static public void init() {
    XMLReader.registerFactory(XML_TAG, new AspectMapFactory());

    // This creates the speed lookup table
    SpeedTable = new int[Track.TrackSpeed.length + 1][Track.TrackSpeed.length +
        1];
    SpeedTable[Track.DEFAULT][Track.DEFAULT] =
        SpeedTable[Track.DEFAULT][Track.NORMAL] =
        SpeedTable[Track.NORMAL][Track.DEFAULT] =
        SpeedTable[Track.NORMAL][Track.NORMAL] = findRule("R281");
    SpeedTable[Track.DEFAULT][Track.LIMITED] =
        SpeedTable[Track.NORMAL][Track.LIMITED] = findRule("R281B");
    SpeedTable[Track.DEFAULT][Track.MEDIUM] =
        SpeedTable[Track.NORMAL][Track.MEDIUM] = findRule("R282");
    SpeedTable[Track.DEFAULT][Track.SLOW] =
        SpeedTable[Track.NORMAL][Track.SLOW] = findRule("R284");
    SpeedTable[Track.DEFAULT][Track.STOP] =
        SpeedTable[Track.NORMAL][Track.STOP] = findRule("R285");
    SpeedTable[Track.DEFAULT][Track.APPROACH] =
        SpeedTable[Track.NORMAL][Track.APPROACH] = findRule("ADV_NORM");

    SpeedTable[Track.LIMITED][Track.DEFAULT] =
        SpeedTable[Track.LIMITED][Track.NORMAL] = findRule("R281C");
    SpeedTable[Track.LIMITED][Track.LIMITED] = findRule("C412");
    SpeedTable[Track.LIMITED][Track.MEDIUM] = findRule("C413");
    SpeedTable[Track.LIMITED][Track.SLOW] = findRule("C414");
    SpeedTable[Track.LIMITED][Track.STOP] = findRule("R281D");
    SpeedTable[Track.LIMITED][Track.APPROACH] = findRule("ADV_LIM");

    SpeedTable[Track.MEDIUM][Track.DEFAULT] =
        SpeedTable[Track.MEDIUM][Track.NORMAL] = findRule("R283");
    SpeedTable[Track.MEDIUM][Track.LIMITED] = findRule("C417");
    SpeedTable[Track.MEDIUM][Track.MEDIUM] = findRule("R283A");
    SpeedTable[Track.MEDIUM][Track.SLOW] = findRule("R283B");
    SpeedTable[Track.MEDIUM][Track.STOP] = findRule("R286");
    SpeedTable[Track.MEDIUM][Track.APPROACH] = findRule("ADV_MED");

    SpeedTable[Track.SLOW][Track.DEFAULT] =
        SpeedTable[Track.SLOW][Track.NORMAL] = findRule("R287");
    SpeedTable[Track.SLOW][Track.LIMITED] = findRule("C422");
    SpeedTable[Track.SLOW][Track.MEDIUM] = findRule("C423");
    SpeedTable[Track.SLOW][Track.SLOW] = findRule("C424");
    SpeedTable[Track.SLOW][Track.STOP] = findRule("R288");
    SpeedTable[Track.SLOW][Track.APPROACH] = findRule("ADV_SLO");

    SpeedTable[Track.STOP][Track.DEFAULT] =
        SpeedTable[Track.STOP][Track.NORMAL] = findRule("R292");
    SpeedTable[Track.STOP][Track.LIMITED] = findRule("R292");
    SpeedTable[Track.STOP][Track.MEDIUM] = findRule("R292");
    SpeedTable[Track.STOP][Track.SLOW] = findRule("R292");
    SpeedTable[Track.STOP][Track.STOP] = findRule("R292");
    SpeedTable[Track.STOP][Track.APPROACH] = findRule("R292");
  }
}

/**
 * is a Class known only to the AspectMap class for creating AspectMaps from
 * an XML document.
 */
class AspectMapFactory
    implements XMLEleFactory {

  /**
   * is the array of strings representing the head values for each indication.
   */
  private String[] HeadStr;

  /**
   * is the approach lighting flag.
   */
  private boolean ApproachFlag = false;

  /**
   * is the Advance indication flag.
   */
  private boolean AdvanceFlag = false;
  
  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
    HeadStr = new String[AspectMap.IndicationNames.length];
    ApproachFlag = false;
    AdvanceFlag = false;
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
    String resultMsg = null;

    // locate the indication that the tag corresponds to.
    for (int ind = 0; ind < AspectMap.IndicationNames.length; ++ind) {
      if (AspectMap.IndicationNames[ind][AspectMap.XML].equals(tag)) {
        HeadStr[ind] = new String(value);
        return null;
      }
    }
    // looks for the approach flag
    if (AspectMap.XML_APPROACH.equals(tag)) {
      if (AspectMap.ISAPPROACH.equals(value)) {
        ApproachFlag = true;
      }
      return null;
    }

    // looks for the advanced flag
    if (AspectMap.XML_ADVANCE.equals(tag)) {
      if (AspectMap.ISAPPROACH.equals(value)) {
        AdvanceFlag = true;
      }
      return null;
    }

    resultMsg = new String("A " + AspectMap.XML_TAG +
                           " XML Element cannot have a " + tag +
                           " attribute.");
    return resultMsg;
  }

  /*
   * tells the factory that the attributes have been seen; therefore,
   * return the XMLEleObject created.
   *
   * @return the newly created XMLEleObject or null (if there was a problem
   * in creating it).
   */
  public XMLEleObject getObject() {
    return new AspectMap(HeadStr, ApproachFlag, AdvanceFlag);
  }
}