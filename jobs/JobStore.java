/*
 * Name: JobStore
 *
 * What:
 *  This class provides a repository for holding all the Job Descriptions.
 */
package cats.jobs;

//import java.util.Vector;

import cats.gui.store.AlignmentList;
import cats.gui.store.CatsTableModel;
import cats.gui.store.CrewList;
import cats.gui.store.FontSpec;
import cats.gui.store.JobEditPane;
//import cats.gui.store.StoreEditPane;
import cats.layout.FontList;
import cats.layout.store.AbstractStore;
import cats.layout.store.DefaultSelectionStrategy;
import cats.layout.store.FieldInfo;
import cats.layout.store.FieldVector;
import cats.layout.store.GenericRecord;
import cats.layout.store.RecordVector;


/**
 * This class holds all the Job descriptions.  It is a Singleton.
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2003, 2010, 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class JobStore
    extends AbstractStore {

  /**
   * The Tag for identifying the JobStore in the XML file.
   */
  private static final String XML_TAG = "JOBSTORE";

  /**
   * The Tag for identifying the JobStore edit control FieldStore.
   */
  private static final String EDIT_TAG = "JOBEDIT";

  /**
   * The Tag for identifying the JobStore Job Records.
   */
  private static final String DATA_TAG = "JOBDATA";

  /**
   * is the error String when a crew has been assigned multiple times.
   */
  private static final String MULTIPLE = " has been assigned to multiple jobs.";
  
  /**
   * is the title to put on the editor JFrmae
   */
  private final String FRAME_TITLE = "Edit Jobs";

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
   */
//  private static final FieldInfo DEFAULT_INFOS[] = {
//    new FieldInfo(new String(FieldInfo.KEY_TAG), false, new String(FieldInfo.KEY_TAG), false, true, FieldInfo.NARROW_WIDTH,
//        null, Job.class),
//    new FieldInfo(new String(Job.JOB_NAME), true, new String(Job.JOB_NAME), true, true, FieldInfo.MEDIUM_WIDTH,
//        "", String.class),
//    new FieldInfo(new String(Job.RUNS_TRAIN), true, new String(Job.RUNS_TRAIN), true, true, FieldInfo.NARROW_WIDTH,
//        new Boolean(true), Boolean.class),
//    new FieldInfo(new String(Job.CREW_NAME), true, new String(Job.CREW_NAME), true, true, FieldInfo.MEDIUM_WIDTH,
//        "", CrewList.class),
//    new FieldInfo(new String(Job.ASSISTANT), true, new String(Job.ASSISTANT), true, true, FieldInfo.MEDIUM_WIDTH,
//        "", CrewList.class),
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
   * 7. default value of Objects of this class
   * 8. class of objects of this class
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
        AlignmentList.DEFAULT, new String(Job.JOB_NAME), String.class),
    new FieldInfo(new String(GenericRecord.STORED_OBJECT), false, "", false, true, FieldInfo.MEDIUM_WIDTH,
        AlignmentList.DEFAULT, null, Job.class),
    new FieldInfo(new String(GenericRecord.STATUS), false, "", false, true, FieldInfo.MEDIUM_WIDTH,
            AlignmentList.DEFAULT, GenericRecord.UNCHANGED, String.class),
    new FieldInfo(new String(Job.JOB_NAME), true, new String(Job.JOB_NAME), false, true, FieldInfo.MEDIUM_WIDTH,
            AlignmentList.DEFAULT, "", String.class),
    new FieldInfo(new String(Job.RUNS_TRAIN), true, new String(Job.RUNS_TRAIN), true, true, FieldInfo.NARROW_WIDTH,
            AlignmentList.DEFAULT, new Boolean(true), Boolean.class),
    new FieldInfo(new String(Job.CREW_NAME), true, new String(Job.CREW_NAME), true, true, FieldInfo.MEDIUM_WIDTH,
            AlignmentList.DEFAULT, "", CrewList.class),
    new FieldInfo(new String(Job.ASSISTANT), true, new String(Job.ASSISTANT), true, true, FieldInfo.MEDIUM_WIDTH,
            AlignmentList.DEFAULT, "", CrewList.class),
    new FieldInfo(new String(Job.FONT), false, new String(Job.FONT), true, true, FieldInfo.MEDIUM_WIDTH,
            AlignmentList.DEFAULT, FontList.FONT_LABEL, FontSpec.class)
  };
  
  /**
   * the singleton, which is known by all clients.
   */
//  public static JobStore JobsKeeper = new JobStore(XML_TAG, EDIT_TAG,
//      DATA_TAG, DEFAULT_INFOS);
  public static JobStore JobsKeeper = new JobStore();

  /**
   * is the constructor.
   */
//  public JobStore(String sTag, String fTag, String rTag, FieldInfo[] prop) {
  public JobStore() {
    //    XmlTag = sTag;
    //    Fields = new FieldVector(fTag, FieldInfo.XML_TAG);
    //    Data = new RecordVector<GenericRecord>(rTag, GenericRecord.DATARECORD);
    //    Fields.loadFields(prop);
    super(XML_TAG, EDIT_TAG, DATA_TAG, DEFAULT_INFOS);

    init();
//    Job.init();
  }

  /**
   * This method tailors the AbstractStore to the concrete instantiation
   * by adjusting the properties of the initial fields.
   */
//  protected void fixup(FieldStore info) {
//    GenericRecord keyRecord;
//
//    // The Job name is mandatory.
//    keyRecord = info.search(new FieldPair(FieldInfo.KEY_TAG, Job.JOB_NAME), null);
//    keyRecord.replacePair(new FieldPair(FieldInfo.WIDTH_TAG,
//                                        new Integer(FieldInfo.MEDIUM_WIDTH)));
//
//    // RUNS_TRAIN is a boolean.
//    keyRecord = info.search(new FieldPair(FieldInfo.KEY_TAG, Job.RUNS_TRAIN), null);
//    keyRecord.replacePair(new FieldPair(FieldInfo.DEFAULT_TAG, new Boolean(true)));
//    keyRecord.replacePair(new FieldPair(FieldInfo.CLASS_TAG,
//                                        ClassSpec.normalizeClassName(Boolean.class.
//        toString())));
//    keyRecord.replacePair(new FieldPair(FieldInfo.WIDTH_TAG,
//                                        new Integer(FieldInfo.NARROW_WIDTH)));
//
//    // The crew is a placeholder.
//    keyRecord = info.search(new FieldPair(FieldInfo.KEY_TAG, Job.CREW_NAME), null);
//    keyRecord.replacePair(new FieldPair(FieldInfo.DEFAULT_TAG, new String("")));
//    keyRecord.replacePair(new FieldPair(FieldInfo.CLASS_TAG,
//                                        ClassSpec.normalizeClassName(CrewList.class.
//        toString())));
//    keyRecord.replacePair(new FieldPair(FieldInfo.WIDTH_TAG,
//                                        new Integer(FieldInfo.MEDIUM_WIDTH)));
//
//    keyRecord = info.search(new FieldPair(FieldInfo.KEY_TAG, Job.ASSISTANT), null);
//    keyRecord.replacePair(new FieldPair(FieldInfo.DEFAULT_TAG, new String("")));
//    keyRecord.replacePair(new FieldPair(FieldInfo.CLASS_TAG,
//                                        ClassSpec.normalizeClassName(CrewList.class.
//        toString())));
//
//    keyRecord = info.search(new FieldPair(FieldInfo.KEY_TAG, Job.FONT), null);
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
   * JobStore, it is a Job.
   */
//  protected Class<Job> getObjectClass() {
//    return Job.class;
//  }

  /**
   * is invoked to verify that the results of the JTable editing is complete
   * and the values are consistent.  If they are not, an error string is
   * created, which StoreEditPane puts in a pop-up.  This method is often
   * overridden.
   *
   * @param model is the TableModel controlling the editing.  It is the
   * conduit for retrieving values from the JTable.
   *
   * @return null - nothing is being tested.
   */
  public String checkConsistency(CatsTableModel model) {
    //    int recordCount = model.getRowCount();
    RecordVector<GenericRecord> data = model.getContents();
    int recordCount = data.size();
    //    int col;
    boolean crew = FieldInfoStore.getFieldInfo(Job.CREW_NAME).getVisible();
    boolean assistant = FieldInfoStore.getFieldInfo(Job.ASSISTANT).getVisible();
    GenericRecord rec;
    String key1 = model.defaultVerifyResults();
    String key2;
    String assist;

    // Check that no crew member has been assigned to more than one
    // train.
    //    col = model.getFieldColumn(Job.CREW_NAME);
    //    assistant = model.getFieldColumn(Job.ASSISTANT);
    //    if ((col >= 0) || (assistant >= 0)) {
    //      for (int lower = 0; lower < recordCount; ++lower) {
    //        if (col >= 0) {
    //          key1 = ( (String) model.getValueAt(lower, col)).trim();
    //          if (!key1.equals("")) {
    //            for (int test = lower + 1; test < recordCount; ++test) {
    //              key2 = ( (String) model.getValueAt(test, col)).trim();
    //              if (key1.equals(key2)) {
    //                return new String(key1 + MULTIPLE);
    //              }
    //              if (assistant >= 0) {
    //                key2 = ( (String) model.getValueAt(test, assistant)).trim();
    //                if (key1.equals(key2)) {
    //                  return new String(key1 + MULTIPLE);
    //                }
    //              }
    //            }
    //          }
    //        }
    //        if (assistant >= 0) {
    //          key1 = ( (String) model.getValueAt(lower, assistant)).trim();
    //          if (!key1.equals("")) {
    //            for (int test = lower + 1; test < recordCount; ++test) {
    //              if (col >= 0) {
    //                key2 = ( (String) model.getValueAt(test, col)).trim();
    //                if (key1.equals(key2)) {
    //                  return new String(key1 + MULTIPLE);
    //                }
    //              }
    //              key2 = ( (String) model.getValueAt(test, assistant)).trim();
    //              if (key1.equals(key2)) {
    //                return new String(key1 + MULTIPLE);
    //              }
    //            }
    //          }
    //        }
    //      }
    //    }

    if (key1 != null) {
      return key1;
    }

    if (crew || assistant) {
      for (int lower = 0; lower < recordCount; ++lower) {
        rec = data.get(lower);
        if (data.isVisible(rec)) {
          assist = ((String) rec.findValue(Job.ASSISTANT)).trim();
          if (crew) {
            key1 = ((String) rec.findValue(Job.CREW_NAME)).trim();
            if (!key1.equals("")) {
              for (int test = lower + 1; test < recordCount; ++test) {
                rec = data.get(test);
                if (data.isVisible(rec)) {
                  key2 = ((String) rec.findValue(Job.CREW_NAME)).trim();
                  if (key1.equals(key2)) {
                    return new String(key1 + MULTIPLE);
                  }
                  if (assistant) {
                    key2 = ((String) data.get(test).findValue(Job.ASSISTANT)).trim();
                    if (key1.equals(key2)) {
                      return new String(key1 + MULTIPLE);
                    }
                  }
                }
              }
            }
          }
          if (assistant  && !assist.equals("")) {
            for (int test = lower + 1; test < recordCount; ++test) {
              rec = data.get(test);
              if (data.isVisible(rec)) {
                if (crew) {
                  key2 = ( (String) rec.findValue(Job.CREW_NAME)).trim();
                  if ((key1 != null) && key1.equals(key2)) {
                    return new String(key1 + MULTIPLE);
                  }
                }
                key2 = ((String) rec.findValue(Job.ASSISTANT)).trim();
                if ((key1 != null) && key1.equals(key2)) {
                  return new String(key1 + MULTIPLE);
                }
              }
            }
          }
        }
      }
    }
    return null;
  }

  /**
   * creates a new Job record.
   *
   * @param jobName is the name of the job being created.
   */
  private Job createJob(String jobName) {
    return (Job) createRecord(jobName);
  }

  /**
   * searches the JobStore for the record for a job.
   *
   * @param jobName is the name of the job being searched for.
   *
   * @return the job record, if found, or null if not found.
   */
  public Job findJob(String jobName) {
    Job j = (Job) findStoredObject(Job.JOB_NAME, jobName);
    if (j == null) {
      j = createJob(jobName);
    }
    return j;
  }

  /**
   * invokes the StoreEditPane specific editor for the Store.
   * @param model is the model that drives the JTable
   * @param trial is a copy of the data being edited
   */
//  protected void runEditor(RecordTableModel model, Vector<GenericRecord> trial) {
//    if (JobEditPane.editRecords(model, "Edit Jobs")) {
//      updateRecords(trial);
//    }
//  }

  /**
   * is the method that starts off the editor on the DataStore.
   */
  public void editData() {
    FieldVector fields = FieldInfoStore.toFormatting();
    RecordVector<GenericRecord> trial = DataStore.makeCopy();
    trial.setStrategy(new DefaultSelectionStrategy(trial));
    if (JobEditPane.editRecords(createModel(trial, fields), FRAME_TITLE)) {
      updateRecords(trial);
      updateFormat(fields);
    }
  }

  /**
   * constructs a StoreTableModel for the concrete class
   * @param data is a copy of the DataStore
   * @param format is a copy of the FieldInfoStore
   * @return a concrete StoreTableModel for the concrete Store.
   */
//  protected CatsTableModel createModel(RecordVector<GenericRecord> data,
//          FieldVector format) {
//      return new RecordTableModel(data, format, this);
//  }
}
/* @(#)JobStore.java */