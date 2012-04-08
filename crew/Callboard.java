/*
 * Name: CallBoard.java
 *
 * What:
 *  This class provides a repository for holding all the Crew Descriptions.
 */
package cats.crew;

import cats.gui.store.AlignmentList;
import cats.gui.store.CatsTableModel;
import cats.gui.store.FontSpec;
import cats.gui.store.JobEditPane;
import cats.gui.store.TimeSpec;
import cats.gui.store.TrainList;
import cats.jobs.Job;
import cats.layout.FontList;
import cats.layout.Hours;
import cats.layout.store.AbstractStore;
import cats.layout.store.DefaultSelectionStrategy;
import cats.layout.store.FieldInfo;
import cats.layout.store.FieldPair;
import cats.layout.store.FieldVector;
import cats.layout.store.GenericRecord;
import cats.layout.store.RecordVector;
import cats.layout.xml.*;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JOptionPane;

/**
 * This class holds all the Crew descriptions.  It is a Singleton.
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2003, 2009, 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class Callboard
extends AbstractStore {

  /**
   * The Tag for identifying the CrewStore in the XML file.
   */
  private static final String XML_TAG = "CREWSTORE";

  /**
   * The Tag for identifying the CrewStore edit control FieldStore.
   */
  private static final String EDIT_TAG = "CREWEDIT";

  /**
   * The Tag for identifying the CrewStore Job Records.
   */
  private static final String DATA_TAG = "CREWDATA";

  /**
   * is the title to put on the editor JFrmae
   */
  private final String FRAME_TITLE = "Edit Callboard";
  
  /**
   * are the default FieldInfos - one FieldInfo for each field.  The order of
   * constructor parameters are:
   * 1. tag
   * 2. visible flag
   * 3. column label on JTable
   * 4. edit flag
   * 5. mandatory flag
   * 6. initial column width
   * 7. default value of Objects of this class
   * 8. class of objects of this class
   * The first field is critical - it references the Object being stored.
   */
  //  private static final FieldInfo DEFAULT_INFOS[] = {
  //    new FieldInfo(new String(FieldInfo.KEY_TAG), false, new String(FieldInfo.KEY_TAG), false, true, FieldInfo.NARROW_WIDTH,
  //        null, Crew.class),
  //    new FieldInfo(new String(Crew.CREW_NAME), true, new String(Crew.CREW_NAME), true, true, FieldInfo.MEDIUM_WIDTH,
  //        "", String.class),
  //    new FieldInfo(new String(Crew.TIME_ON_DUTY), true, new String(Crew.TIME_ON_DUTY), true, true, FieldInfo.NARROW_WIDTH,
  //        "", TimeSpec.class),
  //    new FieldInfo(new String(Crew.TIME_LEFT), true, new String(Crew.TIME_LEFT), true, true, FieldInfo.NARROW_WIDTH,
  //        "", TimeSpec.class),
  //    new FieldInfo(new String(Crew.EXPIRES), true, new String(Crew.EXPIRES), true, true, FieldInfo.NARROW_WIDTH,
  //        "", TimeSpec.class),
  //    new FieldInfo(new String(Crew.TRAIN_ID), true, new String(Crew.TRAIN_ID), true, true, FieldInfo.MEDIUM_WIDTH,
  //        "", TrainList.class),
  //    new FieldInfo(new String(Job.FONT), false, new String(Job.FONT), true, true, FieldInfo.MEDIUM_WIDTH,
  //        FontList.FONT_LABEL, FontSpec.class)
  //  };
  /**
   * are the default FieldInfos - one FieldInfo for each field.  The order of
   * constructor parameters are:
   * 1. tag
   * 2. visible flag
   * 3. column label on JTable
   * 4. edit flag
   * 5. mandatory flag
   * 6. initial column width
   * 7. alignment of the value in the editor column
   * 8. default value of Objects of this class
   * 9. class of objects of this class
   * The first field is critical - it references the Object being stored.
   * 
   * Several of these are mandatory:
   * <ol>
   * <li>the value of KEY_TAG is the tag on the field that contains the key.  It should be user visible.
   * <li>the value of STORED_OBJECT is the active CATS object.  It is linked to and links to other objects,
   * so when its value changes, other things may also have to change.
   * <li>the value of STATUS is used by the editor to propagate the results of editing.
   * </ol>
   */
  private static final FieldInfo DEFAULT_INFOS[] = {
    new FieldInfo(new String(FieldInfo.KEY_TAG), false, "", false, true, FieldInfo.MEDIUM_WIDTH,
        AlignmentList.DEFAULT, new String(Crew.CREW_NAME), String.class),
    new FieldInfo(new String(GenericRecord.STORED_OBJECT), false, "", true, true, FieldInfo.MEDIUM_WIDTH,
        AlignmentList.DEFAULT, null, Crew.class),
    new FieldInfo(new String(GenericRecord.STATUS), false, "", false, true, FieldInfo.MEDIUM_WIDTH,
        AlignmentList.DEFAULT, GenericRecord.UNCHANGED, String.class),
    new FieldInfo(new String(Crew.CREW_NAME), true, new String(Crew.CREW_NAME), false, true, FieldInfo.MEDIUM_WIDTH,
        AlignmentList.DEFAULT, "", String.class),
    new FieldInfo(new String(Crew.TIME_ON_DUTY), true, new String(Crew.TIME_ON_DUTY), false, true, FieldInfo.NARROW_WIDTH,
        AlignmentList.DEFAULT, "", TimeSpec.class),
    new FieldInfo(new String(Crew.TIME_LEFT), true, new String(Crew.TIME_LEFT), true, true, FieldInfo.NARROW_WIDTH,
        AlignmentList.DEFAULT, "", TimeSpec.class),
    new FieldInfo(new String(Crew.EXPIRES), true, new String(Crew.EXPIRES), true, true, FieldInfo.NARROW_WIDTH,
        AlignmentList.DEFAULT, "", TimeSpec.class),
    new FieldInfo(new String(Crew.TRAIN_ID), true, new String(Crew.TRAIN_ID), true, true, FieldInfo.MEDIUM_WIDTH,
        AlignmentList.DEFAULT, "", TrainList.class),
    new FieldInfo(new String(Job.FONT), false, new String(Job.FONT), true, true, FieldInfo.MEDIUM_WIDTH,
        AlignmentList.DEFAULT, FontList.FONT_LABEL, FontSpec.class)
  };


  /**
   * is the Callboard Singleton.
   */
  public static final Callboard Crews = new Callboard(XML_TAG, EDIT_TAG,
      DATA_TAG, DEFAULT_INFOS);

  /**
   * is the constructor.
   *
   * @param sTag is the XML tag of the AbstractStore.
   * @param fTag is the XML tag of the FieldStore.
   * @param rTag is the XML tag of the RecordStore.
   * @param prop is the initial Field keys.
   *
   */
  public Callboard(String sTag, String fTag, String rTag, FieldInfo[] prop) {
    super(sTag, fTag, rTag, prop);
    //    XML_TAG = sTag;
    //    FieldInfoStore = new FieldVector(prop, fTag);
    //    DataStore = new RecordVector<GenericRecord>(rTag);
    //    Fields.loadFields(prop);

    init();
  }

  /**
   * This method tailors the AbstractStore to the concrete instantiation
   * by adjusting the properties of the initial fields.
   */
  //  protected void fixup(FieldStore info) {
  //    GenericRecord keyRecord;
  //
  //    // The Crew name is mandatory.
  //    keyRecord = info.search(new FieldPair(FieldInfo.KEY_TAG, Crew.CREW_NAME), null);
  //    keyRecord.replacePair(new FieldPair(FieldInfo.WIDTH_TAG,
  //                                        new Integer(FieldInfo.MEDIUM_WIDTH)));
  //
  //    // TIME_ON is when the crew was assigned to the Train.
  //    keyRecord = info.search(new FieldPair(FieldInfo.KEY_TAG, Crew.TIME_ON_DUTY), null);
  //    keyRecord.replacePair(new FieldPair(FieldInfo.DEFAULT_TAG, new String("")));
  //    keyRecord.replacePair(new FieldPair(FieldInfo.CLASS_TAG,
  //                                        ClassSpec.normalizeClassName(TimeSpec.class.
  //        toString())));
  //    keyRecord.replacePair(new FieldPair(FieldInfo.WIDTH_TAG,
  //                                        new Integer(FieldInfo.NARROW_WIDTH)));
  //
  //    // TIME_LEFT is amount of time before going dead on hours.
  //    keyRecord = info.search(new FieldPair(FieldInfo.KEY_TAG, Crew.TIME_LEFT), null);
  //    keyRecord.replacePair(new FieldPair(FieldInfo.DEFAULT_TAG, new String("")));
  //    keyRecord.replacePair(new FieldPair(FieldInfo.CLASS_TAG,
  //                                        ClassSpec.normalizeClassName(TimeSpec.class.
  //        toString())));
  //    keyRecord.replacePair(new FieldPair(FieldInfo.WIDTH_TAG,
  //                                        new Integer(FieldInfo.NARROW_WIDTH)));
  //
  //    // EXPIRES is when the Crew goes dead on hours.
  //    keyRecord = info.search(new FieldPair(FieldInfo.KEY_TAG, Crew.TIME_LEFT), null);
  //    keyRecord.replacePair(new FieldPair(FieldInfo.DEFAULT_TAG, new String("")));
  //    keyRecord.replacePair(new FieldPair(FieldInfo.CLASS_TAG,
  //                                        ClassSpec.normalizeClassName(TimeSpec.class.
  //        toString())));
  //    keyRecord.replacePair(new FieldPair(FieldInfo.WIDTH_TAG,
  //                                        new Integer(FieldInfo.NARROW_WIDTH)));
  //
  //    // The train is a placeholder.
  //    keyRecord = info.search(new FieldPair(FieldInfo.KEY_TAG, Crew.TRAIN_ID), null);
  //    keyRecord.replacePair(new FieldPair(FieldInfo.DEFAULT_TAG, new String("")));
  //    keyRecord.replacePair(new FieldPair(FieldInfo.CLASS_TAG,
  //                                        ClassSpec.normalizeClassName(TrainList.class.
  //        toString())));
  //    keyRecord.replacePair(new FieldPair(FieldInfo.WIDTH_TAG,
  //                                        new Integer(FieldInfo.MEDIUM_WIDTH)));
  //    
  //    // The font for the crew
  //    keyRecord = info.search(new FieldPair(FieldInfo.KEY_TAG, Crew.FONT), null);
  //    keyRecord.replacePair(new FieldPair(FieldInfo.VISIBLE_TAG, new Boolean(false)));
  //    keyRecord.replacePair(new FieldPair(FieldInfo.DEFAULT_TAG, new FontSpec(FontList.FONT_LABEL)));
  //    keyRecord.replacePair(new FieldPair(FieldInfo.CLASS_TAG,
  //                                        ClassSpec.normalizeClassName(FontSpec.class.
  //        toString())));
  //    keyRecord.replacePair(new FieldPair(FieldInfo.WIDTH_TAG,
  //                                        new Integer(FieldInfo.MEDIUM_WIDTH)));
  //  }

  /**
   * is a method that tailors the AbstractStore to the concrete instantiation
   * by supplying the Java Class of an Object
   * described by a GenericRecord in the concrete store.
   *
   * @return a Class that can be used to create a StoredObject.  For a
   * TrainStore, it is a Train.
   */
  //  protected Class<?> getObjectClass() {
  //    return Crew.class;
  //  }

  /**
   * creates a new Crew record.
   *
   * @param crewName is the name of the crew member being created.
   * @return the new Crew record
   */
  public Crew addCrew(String crewName) {
    return (Crew) createRecord(crewName);
  }

  /**
   * searches the Callboard for the record for a crew member.
   *
   * @param crewName is the name of the crew member being searched for.
   *
   * @return the Crew record, if found, or null if not found.
   */
  public Crew findCrew(String crewName) {
    return (Crew) findStoredObject(Crew.CREW_NAME, crewName);
  }

  /**
   * creates a Vector of Crew names.
   *
   * @return the Vector of Crew names.
   */
  public Vector<String> getNames() {
    Vector<String> names = new Vector<String>(DataStore.size());
    for (Enumeration<GenericRecord> e = DataStore.elements(); e.hasMoreElements(); ) {
      names.add(new String( ( (Crew) e.nextElement().
          getActiveReference()).getCrewName()));

    }
    return names;
  }

  /**
   * is a filter on the Callboard.  It returns only those crew who are
   * on the extra board.
   *
   * @return the Crew who are working "extra" jobs.
   */
  public Vector<Crew> getExtras() {
    Vector<Crew> extras = new Vector<Crew>(DataStore.size());
    Crew c;
    for (Enumeration<GenericRecord> e = DataStore.elements(); e.hasMoreElements(); ) {
      c = (Crew) e.nextElement().getActiveReference();
      if (c.getExtraFlag()) {
        extras.add(c);
      }
    }
    return extras;
  }

  /**
   * is a method for reading in a file containing a list of crew members.  If
   * the file has a ".xml" or ".XML" suffix, the file will be read using
   * the XML parser, so that the format of the JTable can be picked up.
   * Otherwise, the file is assumed to be a flat file of crew member names.
   *
   * @param cb is the XML file to be read.
   */
  static public void readCrew(File cb) {
    String errReport = null;
    String crew;
    BufferedReader breader;
    if (cb.exists() && cb.canRead()) {
      if (cb.getName().endsWith(".xml") || cb.getName().endsWith(".XML")) {
        errReport = XMLReader.parseDocument(cb);
        if (errReport != null) {
          JOptionPane.showMessageDialog( (Component)null,
              errReport, "Open Error",
              JOptionPane.ERROR_MESSAGE);
        }
      }
      else {
        try {
          breader = new BufferedReader(new FileReader(cb));
          try {
            while ( (crew = breader.readLine()) != null) {
              if (!crew.trim().equals("")) {
                Crews.addCrew(crew);
              }
            }
          }
          catch (java.io.IOException ioe) {
            JOptionPane.showMessageDialog( (Component)null,
                "Error reading callboard",
                "Read Error",
                JOptionPane.ERROR_MESSAGE);
          }
        }
        catch (java.io.FileNotFoundException fnfe) {
          JOptionPane.showMessageDialog( (Component)null,
              cb + " does not exist",
              "Missing callboard",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  /**
   * is invoked to verify that the results of the JTable editing is complete
   * and the values are consistent.  If they are not, an error string is
   * created, which StoreEditPane puts in a pop-up.  This method is often
   * overridden.
   * <p>
   * For the Callboard, this checks that no train has been assigned to
   * multiple crew.
   *
   * @param model is the TableModel controlling the editing.  It is the
   * conduit for retrieving values from the JTable.
   *
   * @return null - nothing is being tested.
   */
  public String checkConsistency(CatsTableModel model) {
    //    int recordCount = model.getRowCount();
    //    int col;
    //    String key1;
    //    String key2;
    //
    //    // Check that no crew member has been assigned to more than one
    //    // train.
    //    col = model.getFieldColumn(Crew.TRAIN_ID);
    //    if (col >= 0) {
    //      for (int lower = 0; lower < recordCount; ++lower) {
    //        key1 = ( (String) model.getValueAt(lower, col)).trim();
    //        if (!key1.equals("")) {
    //          for (int test = lower + 1; test < recordCount; ++test) {
    //            key2 = ( (String) model.getValueAt(test, col)).trim();
    //            if (key1.equals(key2)) {
    //              return new String(key1 + " has been assigned to multiple crew.");
    //            }
    //          }
    //        }
    //      }
    //    }
    RecordVector<GenericRecord> data = model.getContents();
    int recordCount = data.size();
    GenericRecord record;
    String key1 = model.defaultVerifyResults();
    String key2;
    
    if (key1 != null) {
      return key1;
    }
    
    // Check that no crew member has been assigned to more than one
    // train.
    for (int lower = 0; lower < recordCount; ++lower) {
      record = data.get(lower);
      if (data.isVisible(record)) {
        key1 = ((String) record.findValue(Crew.TRAIN_ID)).trim();
        if (!key1.equals("")) {
          for (int test = lower + 1; test < recordCount; ++test) {
            record = data.get(test);
            if (data.isVisible(record)) {
              key2 = ((String) record.findValue(Crew.TRAIN_ID)).trim();
              if (key1.equals(key2)) {
                return new String(key1 + " has been assigned to multiple crew.");
              }
            }
          }
        }
      }
    }
    return null;
  }

  /**
   * is the method that starts off the editor on the DataStore.
   * <p>
   * The local variable visible is the cornerstone to making this all
   * work.  It is a subset of the FieldVector containing the fields
   * that have the visible property set.  The order of its elements
   * dictates the order that the record fields are placed in the JTable. 
   */
  public void editData() {
    //    RecordTableModel model;
    //    Visible = Fields.getVisibleInfos();
    //    ArrayList<Object> fieldKeys = Visible.getSlice(FieldInfo.KEY_TAG);
    //    Vector<TaggedVector> trial = extractVisibleFields(fieldKeys);
    //    String[] headings = new String[Visible.size()];
    //    FieldInfo info;
    //    FieldPair pair;
    //    GenericRecord rec;
    //    boolean enableOnDuty = false;
    //    boolean enableOffDuty = false;
    //    boolean enableLeft = false;
    //    int now = TimeSpec.currentTime();
    //    int law = Hours.getHours();
    //    int duty;
    //    int expires;
    //    int f = 0;
    //
    //    /*
    //     *  Determine if duty times need to be computed.
    //     */
    //    info = Fields.findField(Crew.TIME_ON_DUTY);
    //    if (info != null) {
    //      enableOnDuty = info.getVisible();
    //    }
    //    info = Fields.findField(Crew.EXPIRES);
    //    if (info != null) {
    //      enableOffDuty = info.getVisible();
    //    }
    //    info = Fields.findField(Crew.TIME_LEFT);
    //    if (info != null) {
    //      enableLeft = info.getVisible();
    //    }
    //
    //    if (enableOnDuty || enableOffDuty || enableLeft) {
    //      for (Enumeration<GenericRecord> e = Data.elements(); e.hasMoreElements(); ) {
    //        rec = e.nextElement();
    //        duty = ((Crew) (rec.getActiveReference())).getOnDutyTime();
    //        if (duty != TimeSpec.UNKNOWN_TIME) {
    //          pair = rec.findPair(Crew.TIME_ON_DUTY);
    //          pair.FieldValue = new String(TimeSpec.convertMinutes(duty));
    //          if (law == TimeSpec.UNKNOWN_TIME) {
    //            rec.findPair(Crew.EXPIRES).FieldValue = new String("");
    //            rec.findPair(Crew.TIME_LEFT).FieldValue = new String("");
    //          }
    //          else {
    //            pair = rec.findPair(Crew.EXPIRES);
    //            expires = duty + law;
    //            if (expires >= (24 * 60)) {
    //              expires -= (24 * 60);
    //            }
    //            pair.FieldValue = TimeSpec.convertMinutes(expires);
    //
    //            pair = rec.findPair(Crew.TIME_LEFT);
    //            expires = duty + law - now;
    //            if (expires < 0) {
    //              expires = 0;
    //            }
    //            else if (expires > law) {
    //              expires = law;
    //            }
    //            pair.FieldValue = TimeSpec.convertMinutes(expires);
    //          }
    //        }
    //        else {
    //          rec.findPair(Crew.TIME_ON_DUTY).FieldValue = new String("");
    //          rec.findPair(Crew.EXPIRES).FieldValue = new String("");
    //          rec.findPair(Crew.TIME_LEFT).FieldValue = new String("");
    //        }
    //      }
    //    }
    //    for (Enumeration<FieldInfo> e = Visible.elements(); e.hasMoreElements(); ) {
    //      info = e.nextElement();
    //      if (info.getVisible()) {
    //        headings[f++] = new String(info.getLabel());
    //      }
    //    }
    //    model = new RecordTableModel(trial, headings);
    //    model.setStore(this);
    //    runEditor(model, trial);
    FieldVector fields = FieldInfoStore.toFormatting();
    RecordVector<GenericRecord> trial = DataStore.makeCopy();
    trial.setStrategy(new CrewSelectionStrategy(trial));
    FieldPair pair;
    GenericRecord rec;
    boolean enableOnDuty = false;
    boolean enableOffDuty = false;
    boolean enableLeft = false;
    int now = TimeSpec.currentTime();
    int law = Hours.getHours();
    int duty;
    int expires;

    /*
     *  Determine if duty times need to be computed.
     */
    enableOnDuty = FieldInfoStore.getFieldInfo(Crew.TIME_ON_DUTY).getVisible();
    enableOffDuty = FieldInfoStore.getFieldInfo(Crew.EXPIRES).getVisible();
    enableLeft = FieldInfoStore.getFieldInfo(Crew.TIME_LEFT).getVisible();

    if (enableOnDuty || enableOffDuty || enableLeft) {
      for (Enumeration<GenericRecord> e = trial.elements(); e.hasMoreElements(); ) {
        rec = e.nextElement();
        duty = ((Crew) (rec.getActiveReference())).getOnDutyTime();
        if (duty != TimeSpec.UNKNOWN_TIME) {
          pair = rec.findPair(Crew.TIME_ON_DUTY);
          pair.FieldValue = new String(TimeSpec.convertMinutes(duty));
          if (law == TimeSpec.UNKNOWN_TIME) {
            rec.findPair(Crew.EXPIRES).FieldValue = new String("");
            rec.findPair(Crew.TIME_LEFT).FieldValue = new String("");
          }
          else {
            pair = rec.findPair(Crew.EXPIRES);
            expires = duty + law;
            if (expires >= (24 * 60)) {
              expires -= (24 * 60);
            }
            pair.FieldValue = TimeSpec.convertMinutes(expires);

            pair = rec.findPair(Crew.TIME_LEFT);
            expires = duty + law - now;
            if (expires < 0) {
              expires = 0;
            }
            else if (expires > law) {
              expires = law;
            }
            pair.FieldValue = TimeSpec.convertMinutes(expires);
          }
        }
        else {
          rec.findPair(Crew.TIME_ON_DUTY).FieldValue = new String("");
          rec.findPair(Crew.EXPIRES).FieldValue = new String("");
          rec.findPair(Crew.TIME_LEFT).FieldValue = new String("");
        }
      }
    }
    if (JobEditPane.editRecords(createModel(trial, fields), FRAME_TITLE)) {
      updateRecords(trial);
      updateFormat(fields);
    }
  }

  /**
   * removes any special FieldPairs from a GenericRecord before it is
   * pushed into the StoredObject.  The fields typically will be dependent
   * upon some other field value, so if they are not removed, the old
   * value will replace the dependent value.  In general, nothing needs to
   * be removed, but each AbstractStore has its own special needs.
   * <p>
   * For the Crew, Time On Duty is set by CATS, unless it is defined to be
   * Editable.  This allows the user to select how it is used.
   * 
   * @param rec is the GenericRecord containing the FieldPairs
   */
  protected void removeSpecialPairs(GenericRecord rec) {
    FieldPair onDutyPair = rec.findPair(Crew.TIME_ON_DUTY);
    if (onDutyPair != null) {
      FieldInfo onDutyInfo = FieldInfoStore.getFieldInfo(Crew.TIME_ON_DUTY);
      if ((onDutyInfo != null) && !onDutyInfo.getEdit()) {
        rec.remove(onDutyPair);
      }
    }
  }

//  /**
//   * is a method that should be defined by each sub-class for filtering
//   * out Records for editing.  The filter is specific to each sub-class.
//   *
//   * For the Callboard, it selects those Crew who are on an "extra" job.
//   *
//   * @param rec is a GenericRecord from the RecordStore.
//   *
//   * @return true if the record is selected.
//   */
//  protected boolean isSelected(GenericRecord rec) {
//    return ( (Crew) rec.getActiveReference()).getExtraFlag();
//  }

//  /**
//   * invokes the StoreEditPane specific editor for the Store.
//   * @param model is the model that drives the JTable
//   * @param trial is a copy of the data being edited
//   */
  //  protected void runEditor(RecordTableModel model, Vector<TaggedVector> trial) {
  //    if (JobEditPane.editRecords(model, "Edit Crew")) {
  //      updateRecords(trial);
  //    }
  //  }
  //  
//  /**
//   * constructs a StoreTableModel for the concrete class
//   * @param data is a copy of the DataStore
//   * @param format is a copy of the FieldInfoStore
//   * @return a concrete StoreTableModel for the concrete Store.
//   */
//  protected CatsTableModel createModel(RecordVector<GenericRecord> data,
//      FieldVector format) {
//    return new RecordTableModel(data, format, this);
//  }
  private class CrewSelectionStrategy extends DefaultSelectionStrategy {

    /**
     * the ctor
     * @param parent the Vector on which the Strategy is operating
     */
    public CrewSelectionStrategy(RecordVector<GenericRecord> parent) {
      super(parent);
    }
    /**
     * provides a finely grained way of preventing editing on a specific field
     * in an individual record.
     * @param rec is the record the edit request is for
     * @param field is the JTable column name of the field
     * @return true
     */
    public boolean isEditable(GenericRecord rec, String field) {
      Crew so;
      if (Crew.TRAIN_ID.equals(field)) {
        if ((so = (Crew) rec.getActiveReference()) != null) {
          return so.getExtraFlag();
        }
      }
      return true;
    }

  }
}
/* @(#)JobStore.java */
