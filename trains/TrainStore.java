/*
 * Name: TrainStore
 *
 * What:
 *  This class provides a repository for holding all the Trains.
 */
package cats.trains;

import cats.gui.ArrowUser;
import cats.gui.GridTile;
import cats.gui.KeyHandler;
//import cats.gui.KeyHandler;
import cats.gui.store.AlignmentList;
import cats.gui.store.CatsTableModel;
import cats.gui.store.ExtraList;
import cats.gui.store.FontSpec;
import cats.gui.store.SingleTrainEditPane;
//import cats.gui.store.StoreEditPane;
//import cats.gui.store.SingleTrainEditPane;
import cats.gui.store.TimeSpec;
import cats.gui.store.TrainEditPane;
import cats.jmri.OperationsTrains;
import cats.layout.FontList;
import cats.layout.store.AbstractStore;
import cats.layout.store.FieldInfo;
import cats.layout.store.FieldPair;
import cats.layout.store.FieldVector;
import cats.layout.store.GenericRecord;
import cats.layout.store.RecordVector;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JOptionPane;

/**
 * This class holds all the Trains.  It is a Singleton.  Implicit in
 * how this operates is that all searching and presentation is done in
 * the order in which Trains are added.  Thus, if they are added in the
 * order that they are scheduled, then the dispatcher can see which Trains
 * are run before others.
 * <p>
 * Another assumption is that once a Train has been added to the list,
 * it will not be removed.  This assumption is reenforced by the absence
 * of a delete operation.
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class TrainStore
    extends AbstractStore
    implements ArrowUser {

  /**
   * The Tag for identifying the TrainStore in the XML file.
   */
  private static final String XML_TAG = "TRAINSTORE";

  /**
   * The Tag for identifying the TrainStore edit control FieldStore.
   */
  private static final String EDIT_TAG = "TRAINEDIT";

  /**
   * The Tag for identifying the TrainStore Train Records.
   */
  private static final String DATA_TAG = "TRAINDATA";

  /**
   * is the title to put on the editor JFrmae
   */
  private final String FRAME_TITLE = "Edit Lineup";
  
  /**
   * is the title to put on the single train editor JFrame
   */
  private final String TRAIN_TITLE = "Edit Train";

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
//        null, Train.class),
//    new FieldInfo(new String(Train.TRAIN_SYMBOL), true, new String(Train.TRAIN_SYMBOL), true, true, FieldInfo.MEDIUM_WIDTH,
//        "", String.class),
//    new FieldInfo(new String(Train.ENGINE), true, new String(Train.ENGINE), true, true, FieldInfo.NARROW_WIDTH,
//        "", String.class),
//    new FieldInfo(new String(Train.TRANSPONDING), false, new String(Train.TRANSPONDING), true, true, FieldInfo.NARROW_WIDTH,
//        new Boolean(false), Boolean.class),
//    new FieldInfo(new String(Train.CABOOSE), true, new String(Train.CABOOSE), true, true, FieldInfo.NARROW_WIDTH,
//        "", String.class),
//    new FieldInfo(new String(Train.CREW), true, new String(Train.CREW), true, true, FieldInfo.MEDIUM_WIDTH,
//        "", ExtraList.class),
//    new FieldInfo(new String(Train.ONDUTY), false, new String(Train.ONDUTY), true, true, FieldInfo.NARROW_WIDTH,
//        "", TimeSpec.class),
//    new FieldInfo(new String(Train.DEPARTURE), false, new String(Train.DEPARTURE), true, true, FieldInfo.NARROW_WIDTH,
//        "", TimeSpec.class),
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
   * 7. alignment in JTable cell
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
        AlignmentList.DEFAULT, new String(Train.TRAIN_SYMBOL), String.class),
      new FieldInfo(new String(GenericRecord.STORED_OBJECT), false, "", false, true, FieldInfo.MEDIUM_WIDTH,
              AlignmentList.DEFAULT, null, Train.class),
      new FieldInfo(new String(GenericRecord.STATUS), false, "", false, true, FieldInfo.MEDIUM_WIDTH,
              AlignmentList.DEFAULT, GenericRecord.UNCHANGED, String.class),
      new FieldInfo(new String(Train.TRAIN_NAME), true, new String(Train.TRAIN_NAME), true, true, FieldInfo.WIDE_WIDTH,
              AlignmentList.DEFAULT, "", String.class),
      new FieldInfo(new String(Train.TRAIN_SYMBOL), true, new String(Train.TRAIN_SYMBOL), false, true, FieldInfo.MEDIUM_WIDTH,
              AlignmentList.DEFAULT, "", String.class),
      new FieldInfo(new String(Train.ENGINE), true, new String(Train.ENGINE), true, true, FieldInfo.NARROW_WIDTH,
              AlignmentList.DEFAULT, "", String.class),
      new FieldInfo(new String(Train.TRANSPONDING), false, new String(Train.TRANSPONDING), true, true, FieldInfo.NARROW_WIDTH,
              AlignmentList.DEFAULT, new Boolean(false), Boolean.class),
      new FieldInfo(new String(Train.CABOOSE), true, new String(Train.CABOOSE), true, true, FieldInfo.NARROW_WIDTH,
              AlignmentList.DEFAULT, "", String.class),
      new FieldInfo(new String(Train.CREW), true, new String(Train.CREW), true, true, FieldInfo.MEDIUM_WIDTH,
              AlignmentList.DEFAULT, "", ExtraList.class),
      new FieldInfo(new String(Train.ONDUTY), false, new String(Train.ONDUTY), true, true, FieldInfo.NARROW_WIDTH,
              AlignmentList.DEFAULT, "", TimeSpec.class),
      new FieldInfo(new String(Train.DEPARTURE), false, new String(Train.DEPARTURE), true, true, FieldInfo.NARROW_WIDTH,
              AlignmentList.DEFAULT, "", TimeSpec.class),
      new FieldInfo(new String(Train.LENGTH), false, new String(Train.LENGTH), true, true, FieldInfo.MEDIUM_WIDTH,
              AlignmentList.DEFAULT, "", Integer.class),
      new FieldInfo(new String(Train.WEIGHT), false, new String(Train.WEIGHT), true, true, FieldInfo.MEDIUM_WIDTH,
              AlignmentList.DEFAULT, "", Integer.class),
      new FieldInfo(new String(Train.CARS), false, new String(Train.CARS), true, true, FieldInfo.MEDIUM_WIDTH,
              AlignmentList.DEFAULT, "", Integer.class),
      new FieldInfo(new String(Train.FONT), false, new String(Train.FONT), true, true, FieldInfo.MEDIUM_WIDTH,
              AlignmentList.DEFAULT, FontList.FONT_LABEL, FontSpec.class)
  };

  /**
   * is used to locate the fields that describe the Train.  The
   * common.prop class has some useful utilities for pulling out
   * entries.
   */
//  protected static String[] TRAIN_PROPS = {
//      Train.TRAIN_SYMBOL,
//      Train.TRAIN_NAME,
//      Train.ENGINE,
//      Train.TRANSPONDING,
//      Train.CABOOSE,
//      Train.CREW,
//      Train.ONDUTY,
//      Train.DEPARTURE,
//      Train.FONT
//  };

  /**
   * the singleton, which is known by all clients.
   */
//  public static TrainStore TrainKeeper = new TrainStore(XML_TAG, EDIT_TAG,
//      DATA_TAG, DEFAULT_INFOS);
  /**
   * the singleton, which is known by all clients.
   */
  public static TrainStore TrainKeeper = new TrainStore();

  /**
   * is the Train receiving keystrokes.
   */
  private Train Focus;
  
//  /**
//   * is the strategy to use when selecting which Trains are to appear
//   * in the TrainEdit table.
//   */
//  private TrainSelectionStrategy Strategy;
//  
//  /**
//   * is the singleton (since it doesn't change) that looks for all Trains.
//   */
//  private TrainSelectionStrategy AllTrains = new allActiveStrategy();
//  
//  /**
//   * is the singleton that looks for a specific Train.
//   */
//  private namedTrainStrategy NamedTrain = new namedTrainStrategy();

//  /**
//   * is the strategy to use for the Train Editor
//   */
// @SuppressWarnings("unused")
//private TrainEditorStrategy MyEditor;
// 
// /**
//  * is the singleton (since it doesn't change) that edits all Trains.
//  */
// private TrainEditorStrategy AllTrainsEditor = new allEditStrategy();
// 
// /**
//  * is the singleton that edits a specific Train.
//  */
// private TrainEditorStrategy NamedTrainEditor = new namedEditStrategy();

  /**
   * is the constructor.
   *
   * @param sTag is the XML tag of the AbstractStore.
   * @param fTag is the XML tag of the FieldStore.
   * @param rTag is the XML tag of the RecordStore.
   * @param prop is the initial Field keys.
   *
   */

 /**
  * the constructor.
  */
 public TrainStore() {
   super(XML_TAG, EDIT_TAG, DATA_TAG, DEFAULT_INFOS);
   init();
   Train.init();
   KeyHandler.wantsArrow(this);
 }

 /**
   * This method tailors the AbstractStore to the concrete instantiation
   * by adjusting the properties of the initial fields.
   */
//  protected void fixup(FieldStore info) {
//    GenericRecord keyRecord;
//
//    // The Train Symbol is the key, so is mandatory.
//    keyRecord = info.search(new FieldPair(FieldInfo.KEY_TAG, Train.TRAIN_SYMBOL), null);
//    keyRecord.replacePair(new FieldPair(FieldInfo.MANDATORY_TAG, new Boolean(true)));
//    keyRecord.replacePair(new FieldPair(FieldInfo.WIDTH_TAG,
//                                        new Integer(FieldInfo.MEDIUM_WIDTH)));
//
//    // shrink the width of the Engine.
//    keyRecord = info.search(new FieldPair(FieldInfo.KEY_TAG, Train.ENGINE), null);
//    keyRecord.replacePair(new FieldPair(FieldInfo.WIDTH_TAG,
//                                        new Integer(FieldInfo.NARROW_WIDTH)));
//
//    // Transponding is a boolean.
//    keyRecord = info.search(new FieldPair(FieldInfo.KEY_TAG, Train.TRANSPONDING), null);
//    keyRecord.replacePair(new FieldPair(FieldInfo.DEFAULT_TAG, new Boolean(true)));
//    keyRecord.replacePair(new FieldPair(FieldInfo.CLASS_TAG,
//                                        ClassSpec.normalizeClassName(Boolean.class.
//        toString())));
//    keyRecord.replacePair(new FieldPair(FieldInfo.WIDTH_TAG,
//                                        new Integer(FieldInfo.NARROW_WIDTH)));
//    // shrink the width of the Caboose.
//    keyRecord = info.search(new FieldPair(FieldInfo.KEY_TAG, Train.CABOOSE), null);
//    keyRecord.replacePair(new FieldPair(FieldInfo.WIDTH_TAG,
//                                        new Integer(FieldInfo.NARROW_WIDTH)));
//
//    // The crew is a placeholder.
//    keyRecord = info.search(new FieldPair(FieldInfo.KEY_TAG, Train.CREW), null);
//    keyRecord.replacePair(new FieldPair(FieldInfo.VISIBLE_TAG, new Boolean(true)));
//    keyRecord.replacePair(new FieldPair(FieldInfo.DEFAULT_TAG, new String("")));
//    keyRecord.replacePair(new FieldPair(FieldInfo.CLASS_TAG,
//                                        ClassSpec.normalizeClassName(ExtraList.class.
//        toString())));
//    keyRecord.replacePair(new FieldPair(FieldInfo.WIDTH_TAG,
//                                        new Integer(FieldInfo.MEDIUM_WIDTH)));
//
//    // The ONDUTY value is a TimeSpec.
//    keyRecord = info.search(new FieldPair(FieldInfo.KEY_TAG, Train.ONDUTY), null);
//    keyRecord.replacePair(new FieldPair(FieldInfo.DEFAULT_TAG, new String("")));
//    keyRecord.replacePair(new FieldPair(FieldInfo.CLASS_TAG,
//                                        ClassSpec.normalizeClassName(TimeSpec.class.
//        toString())));
//    keyRecord.replacePair(new FieldPair(FieldInfo.WIDTH_TAG,
//                                        new Integer(FieldInfo.NARROW_WIDTH)));
//
//    // The DEPARTURE value is a TimeSpec.
//    keyRecord = info.search(new FieldPair(FieldInfo.KEY_TAG, Train.DEPARTURE), null);
//    keyRecord.replacePair(new FieldPair(FieldInfo.DEFAULT_TAG, new String("")));
//    keyRecord.replacePair(new FieldPair(FieldInfo.CLASS_TAG,
//                                        ClassSpec.normalizeClassName(TimeSpec.class.
//        toString())));
//    keyRecord.replacePair(new FieldPair(FieldInfo.WIDTH_TAG,
//                                        new Integer(FieldInfo.NARROW_WIDTH)));
//    // The FONT value is a FontFinder.
//    keyRecord = info.search(new FieldPair(FieldInfo.KEY_TAG, Train.FONT), null);
//    keyRecord.replacePair(new FieldPair(FieldInfo.VISIBLE_TAG, new Boolean(false)));
//    keyRecord.replacePair(new FieldPair(FieldInfo.DEFAULT_TAG, new FontSpec(FontList.FONT_TRAIN)));
//    keyRecord.replacePair(new FieldPair(FieldInfo.CLASS_TAG,
//                                        ClassSpec.normalizeClassName(FontSpec.class.
//        toString())));
//    keyRecord.replacePair(new FieldPair(FieldInfo.WIDTH_TAG,
//                                        new Integer(FieldInfo.NARROW_WIDTH)));
//  }

  /**
   * is a method that tailors the AbstractStore to the concrete instantiation
   * by supplying the Java Class of an Object
   * described by a GenericRecord in the concrete store.
   *
   * @return a Class that can be used to create a StoredObject.  For a
   * TrainStore, it is a Train.
   */
//  protected Class<Train> getObjectClass() {
//    return Train.class;
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
//    int col= model.getFieldColumn(Train.TRAIN_SYMBOL);;
//    String key1;
//    String key2;
//
//    // The first check is to be sure all Train Symbols are not blank.
//    for (int rec = 0; rec < recordCount; ++rec) {
//      key1 = (String) model.getValueAt(rec, col);
//      if ( (key1 == null) || (key1.trim().equals(""))) {
//        return new String("Row " + rec + " needs a Train Symbol");
//      }
//    }
//
//    // The next check is to be sure there are no duplicates.
//    for (int lower = 0; lower < recordCount; ++lower) {
//      key1 = ((String) model.getValueAt(lower, col)).trim();
//      for (int test = lower + 1; test < recordCount; ++test) {
//        key2 = ((String) model.getValueAt(test, col)).trim();
//        if (key1.equals(key2)) {
//          return new String("Multiple Trains have the Symbol " + key1);
//        }
//      }
//    }
//
//    // Finally, check that no crew member has been assigned to more than one
//    // train.
//    col = model.getFieldColumn(Train.CREW);
//    if (col >= 0) {
//      for (int lower = 0; lower < recordCount; ++lower) {
//        key1 = ((String) model.getValueAt(lower, col)).trim();
//        if (!key1.equals("")) {
//          for (int test = lower + 1; test < recordCount; ++test) {
//            key2 = ((String) model.getValueAt(test, col)).trim();
//            if (key1.equals(key2)) {
//              return new String(key1 + " has been assigned to multiple trains.");
//            }
//          }
//        }
//      }
//    }
    RecordVector<GenericRecord> data = model.getContents();
    int recordCount = data.size();
    String key1;
    String key2;
    GenericRecord record1;
    GenericRecord record2;

    // The first check is to be sure all Train Symbols are not blank.
    for (int rec = 0; rec < recordCount; ++rec) {
      record1 = data.get(rec);
      if (data.isVisible(record1)) {
        key1 = (String) record1.findValue(Train.TRAIN_SYMBOL);
        if ( (key1 == null) || (key1.trim().equals(""))) {
          return new String("Row " + (rec + 1)  + " needs a Train Symbol");
        }
      }
    }

    // The next check is to be sure there are no duplicates.  This cannot ignore
    // tied down and terminated trains, because they can be rerun
    for (int lower = 0; lower < recordCount; ++lower) {
      record1 = data.get(lower);
      key1 = (String) record1.findValue(Train.TRAIN_SYMBOL);
      if (key1 != null) {
        key1 = key1.trim();
        if (!key1.equals("")) {
          for (int test = lower + 1; test < recordCount; ++test) {
            record2 = data.get(test);
            key2 = ((String) record2.findValue(Train.TRAIN_SYMBOL));
            if (key2 != null) {
              key2 = key2.trim();
              if (key1.equals(key2)) {
                return new String("Multiple Trains have the Symbol " + key1);
              }
            }
          }
        }
      }
    }

    // Finally, check that no crew member has been assigned to more than one
    // train.
    if (FieldInfoStore.getFieldInfo(Train.CREW).getVisible()) {
      for (int lower = 0; lower < recordCount; ++lower) {
        record1 = data.get(lower);
        if (data.isVisible(record1)) {
          key1 = ((String) record1.findValue(Train.CREW)).trim();
          if (!key1.equals("")) {
            for (int test = lower + 1; test < recordCount; ++test) {
              record2 = data.get(test);
              if (data.isVisible(record2)) {
                key2 = ((String) record2.findValue(Train.TRAIN_SYMBOL)).trim();
                if (key1.equals(key2)) {
                  return new String(key1 + " has been assigned to multiple trains.");
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
   * is a method for deciding if a Train should be included in the
   * table, based on the active selection criteria.
   *
   * @param rec is a GenericRecord from the RecordStore.
   *
   * @return true if the record is selected.
   */
//  protected boolean isSelected(GenericRecord rec) {
//    return Strategy.meetsSelectionCriteria(rec);
//  }

  /**
   * pulls the Train field out of a Train Record.
   *
   * @param record is the GenericRecord for a Train.
   * @return the Train described.
   */
  private Train extractTrain(GenericRecord record) {
    return (Train) record.findValue(GenericRecord.STORED_OBJECT);
  }

  /**
   * extracts the Train Field from all Train Records that match the selection
   * criteria.
   * @param selector is the Train state being searched for.  If any one
   * of the bits in the BitSet are set, then it matches.
   *
   * @param filter is the inverse of selector.  If any one of the bits
   * in the BitSet are set, then it is rejected.
   *
   * @return a Vector containing all Trains whose state is selector.
   */
  private Vector<Train> selectTrain(BitSet selector, BitSet filter) {
    Vector<Train> trains = new Vector<Train>();
    Train train;
    GenericRecord t;
    for (Enumeration<GenericRecord> e = DataStore.elements();
         e.hasMoreElements(); ) {
      t = (e.nextElement());
      train = (Train) t.getActiveReference();
      if ( (train != null) && (train.selectTrain(selector, filter))) {
        trains.add(train);
      }
    }
    return trains;
  }

  /**
   * constructs a Vector of Trains that have not been positioned
   * on the layout.
   *
   * @return the Vector.
   */
  public Vector<Train> getCreated() {
    return selectTrain(Train.Created, null);
  }

  /**
   * constructs a Vector of Trains that have not been assigned crews
   * and have not been run.
   *
   * @return the Vector.
   */
  public Vector<Train> getUnrun() {
    return selectTrain(Train.Unassigned, Train.Done);
  }

//  /**
//   * constructs a Vector of Trains that are being run.
//   *
//   * @return the Vector.
//   */
//  public Vector getActive() {
//    return selectTrain(Train.Active, null);
//  }
//
  /**
   * constructs a Vector of Trains that have not been removed.
   *
   * @return the Vector.
   */
  private Vector<Train> getNotRemoved() {
    return selectTrain(null, Train.Removed);
  }

  /**
   * constructs a Vector of Trains that have been run.
   * @return a Vector of all Trains that have been tied down
   * or terminated.
   */
  public Vector<Train> getRun() {
    return selectTrain(Train.Done, null);
  }

  /**
   * creates a new Train record.
   *
   * @param trainName is the name of the train being created.
   * @return the newly created train
   */
  public Train createTrain(String trainName) {
    GenericRecord rec = FieldInfoStore.createDefaultRecord(DATA_TAG, trainName);
//    rec.replacePair(new FieldPair(Train.TRAIN_SYMBOL, trainName));
//    DataStore.replaceRecord(rec);
    addTrustedRecord(rec);
    return getTrain(trainName);
  }

  /**
   * searches the list of Trains for one whose symbol matches symbol.
   *
   * @param symbol is a String containing the Symbol for a train.  If
   * symbol is null or empty or cannot be found, null is returned.
   *
   * @return the Train whose symbol matches the parameter.  null is a valid
   * return value if no Train cab be found.
   */
  public Train getTrain(String symbol) {
    Train t = null;
    GenericRecord rec;
    if ((symbol != null) && (!symbol.trim().equals(""))) {
        rec = DataStore.search(new FieldPair(Train.TRAIN_SYMBOL, symbol), null);
        if (rec != null) {
          return (Train) rec.getActiveReference();
        }
    }
    return t;
  }
  
  /**
   * removes focus from the train that has it.
   */
  public void clrFocus() {
    Focus = null;
  }

  /**
   * receives a key code from the KeyHandler.
   *
   * @param KeyCode is the key code.
   *
   * @see java.awt.event.KeyEvent
   */
  public void takeArrow(int KeyCode) {
    Vector<Train> working = new Vector<Train>();
    Train train;
    GenericRecord t;
    int where = -1;
    if ( (KeyCode == KeyEvent.VK_PAGE_DOWN) ||
        (KeyCode == KeyEvent.VK_PAGE_UP) || (KeyCode == KeyEvent.VK_TAB)) {
      // Select the Trains that are running and see if Focus is one of them.
      for (Enumeration<GenericRecord> e = DataStore.elements();
           e.hasMoreElements(); ) {
        t = (e.nextElement());
        train = extractTrain(t);
        if (train.isActive()) {
          working.add(train);
          if (train == Focus) {
            where = working.size() - 1;
          }
        }
      }
      train = null;

      if (where < 0) {
        // The old Focus (if there was one) is not running.
        if (working.size() > 0) {
          train = working.firstElement();
        }
      }
      else {
        if (KeyCode == KeyEvent.VK_PAGE_DOWN) { // find previous Train
          if (where == 0) {
            train = working.lastElement();
          }
          else {
            train = working.elementAt(where - 1);
          }
        }
        else {
          if (Focus == working.lastElement()) {
            train = working.firstElement();
          }
          else {
            train = working.elementAt(where + 1);
          }
        }
      }
      if (Focus != null) {
        Focus.setFocus(false);
        Focus = null;
      }
      if (train != null) {
        train.setFocus(true);
        Focus = train;
      }
    }
    else if (Focus != null) {
      Focus.moveTrain(KeyCode);
    }
    GridTile.doUpdates();
  }
  
  /**
   * sets the label on the panel.
   * 
   * @param useSymbol is true if the label is
   * the train's symbol and false if it is the
   * train's engine number.
   */
  public void setTrainLabel(boolean useSymbol) {
    Vector<Train> trains = getNotRemoved();
    Train train;
    for (Enumeration<Train> itor = trains.elements(); itor.hasMoreElements();) {
      train = itor.nextElement();
      train.changeLabel(useSymbol);
    }
  }

  /**
   * invokes the Train Editor on a single Train.
   * @param t is the Train to edit.
   */
  public void singleTrainEdit(Train t) {
//    if (t != null) {
//      editTrain(t);
//    }
    FieldVector fields = FieldInfoStore.toFormatting();
    RecordVector<GenericRecord> trial = new RecordVector<GenericRecord>(1);
    GenericRecord tRec = TrainStore.TrainKeeper.findRecord(Train.TRAIN_SYMBOL, t.getSymbol());
    GenericRecord eRec;
    if (tRec != null) {
      trial.add(eRec = tRec.copyRecord());
      trial.setStrategy(new OneTrainSelectionStrategy());
      if (TrainEditPane.editRecords(new SingleTrainEditPane(createModel(trial, fields), 
          (Train) tRec.getActiveReference()), TRAIN_TITLE)) {
        updateTrain(eRec, tRec);
      }
    }
    else {
      JOptionPane.showMessageDialog( (Component)null,
          "Internal error.  The Train data is inconsistent.",
          "Internal Train Error",
          JOptionPane.ERROR_MESSAGE
          );     
    }
  }
  
  /**
   * invokes the StoreEditPane specific editor for the Store.
   * @param model is the model that drives the JTable
   * @param trial is a copy of the data being edited
   */
//  protected void runEditor(RecordTableModel model, Vector<GenericRecord> trial) {
//    if (TrainEditPane.editRecords(model)) {
//      updateRecords(trial);
//    }
//  }
  
  /**
   * runs the Train Editor on a single Train.  It first sets the
   * Train Selection Strategy to select only the Train.  It runs
   * the editor.  It sets the Selection Strategy back to all active
   * Trains because the Menu class does not know that there are
   * different strategies.
   * @param t is the Train
   */
//  public void editTrain(Train t) {
//    NamedTrain.setTrain(t);
//    Strategy = NamedTrain;
//    MyEditor = NamedTrainEditor;
//    editData();
//    Strategy = AllTrains;
//    MyEditor = AllTrainsEditor;
//  }
  
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

  /**
   * applies the result of the edit to the StoredObject (the Train), which
   * in turn synchronizes the GenericRecord in the DataStore
   * 
   * @param edit is the GenericRecord that was edited (the copy)
   * @param master is the original GenericRecord
   */
  private void updateTrain(GenericRecord edit, GenericRecord master) {
    Train so = (Train) edit.getActiveReference();
    String status = edit.getStatus();

    // Trains can never be deleted; however, their status may change
    if (status == GenericRecord.CHANGED) {
      so.updateDescription(edit);
      master.setStatus(GenericRecord.UNCHANGED);
    }
    else if (status == Train.TRAIN_RERUN) {
      so.rerun();
    }
    else if (status == Train.TRAIN_TERMINATED) {
      so.remove();
    }
    else if (status == Train.TRAIN_TIED_DOWN) {
      so.tieDown();
    }
  }    
  
  /**
   * processes the results of editing the AbstractStore
   * @param trial is a copy of the AbstractStore that was edited.
   */
  protected void updateRecords(Vector<GenericRecord> trial) {
    GenericRecord edit;
    GenericRecord master;
    FieldPair keyPair;
    String keyTag = FieldInfoStore.getKeyField();
    RecordVector<GenericRecord> tempStore = DataStore.duplicate();
    
    // preserve the order of records from the edit
    DataStore.clear();
    for (Enumeration<GenericRecord> e = trial.elements(); e.hasMoreElements(); ) {
      edit = e.nextElement();
      keyPair = edit.findPair(keyTag);
      master = tempStore.search(keyPair, null);
      
      if (master == null) {
        // the record was just created.  A Train can not be deleted.  So, after the
        // accept button is pushed, it remains until CATS ends.
        edit.setStatus(GenericRecord.UNCHANGED);
        addTrustedRecord(edit);
      }
      else {
        updateTrain(edit, master);
        DataStore.add(master);
      }
    }
  }

  /**
   * is the method that starts off the editor on the DataStore.
   */
  public void editData() {
    FieldVector fields = FieldInfoStore.toFormatting();
    RecordVector<GenericRecord> trial = DataStore.makeCopy();
    trial.setStrategy(new LineUpSelectionStrategy());
    if (TrainEditPane.editRecords(new TrainEditPane(createModel(trial, fields), true), FRAME_TITLE)) {
      updateRecords(trial);
      updateFormat(fields);
    }
  }
  
  /*
   * tells the XMLEleObject that no more setValue or setObject calls will
   * be made; thus, it can do any error checking that it needs.
   *
   * @return null, if it has received everything it needs or an error
   * string if something isn't correct.
   */
  public String doneXML() {
	  OperationsTrains.instance().refreshTrains();
    return null;
  }

  /**
   *** strategies to use for selecting a train.  ********
   */
  
//  /**
//   * the interface that all selection strategies must adhere to.
//   */
//  private interface TrainSelectionStrategy {
//    
//    /**
//     * examines the fields of a Record to determine if it meets
//     * the selection criteria or not.  This means the client will
//     * apply the single method to all Records.
//     * 
//     * @param r is a Record being examined.
//     * @return true if the Record can is selected or false if not.
//     */
//    public boolean meetsSelectionCriteria(GenericRecord r);
//  }
//
//  /**
//   * a strategy in which all active Trains that have not been run
//   * are selected.
//   */
//  private class allActiveStrategy implements TrainSelectionStrategy {
//    
//    public boolean meetsSelectionCriteria(GenericRecord rec) {
//      Train train;
//      if ( (train = (Train) rec.getActiveReference()) != null) {
//        return !train.hasRun();
//      }
//      return false;
//    }    
//  }
//
//  /**
//   * a strategy in which only a single train is selected
//   */
//  private class namedTrainStrategy implements TrainSelectionStrategy {
//    
//    /**
//     * is the the Train being selected.
//     */
//    private Train thisTrain = null;
// 
//    public boolean meetsSelectionCriteria(GenericRecord rec) {
//      return TrainStore.TrainKeeper.extractTrain(rec) == thisTrain;
//    }
// 
//    /**
//     * sets the Train to search for.
//     * @param t is the train.
//     */
//    public void setTrain(Train t) {
//      thisTrain = t;
//    }
//  }
//  
//  /**
//   *** strategies to use for selecting a Train Editor.  ********
//   */
//  
//  /**
//   * the interface that all editor strategies must adhere to.
//   */
//  private interface TrainEditorStrategy {
//    
//    /**
//     * executes the desired editor.
//     * @param m is the model to pass to the editor.
//     * @param v is the underlying vector of records
//   * @param fieldKeys are the tags on the fields being edited
//     */
//    public void edit(RecordTableModel m, Vector<TaggedVector> v, ArrayList<Object> fieldKeys);
//  }

  /**
   * a strategy to edit a list of Trains.
   */
//  private class allEditStrategy implements TrainEditorStrategy {
//    
//    public void edit(RecordTableModel model, Vector<TaggedVector> trial, ArrayList<Object> fieldKeys) {
//      if (TrainEditPane.editRecords(model)) {
//        updateRecords(trial);
//      } 
//    }
//  }

  /**
   * a strategy to edit a single train
   */
//  private class namedEditStrategy implements TrainEditorStrategy {
//    public void edit(RecordTableModel model, Vector<TaggedVector> v, ArrayList<Object> fieldKeys) {
//      if (SingleTrainEditPane.editRecords(model)) {
//        updateRecords(v, fieldKeys);
//      } 
//    }
//
//    /**
//     * saves the result of editing the Record.  This overrides the method
//     * in the RecordStore super class to preserve the order of the TrainRecord
//     * in the lineup.
//     *
//     * @param updates is a Vector of Vectors of field Values.
//     * @param fieldKeys are the tags on the fields edited, in the same order
//     * as they appear in updates
//     */
//    public void updateRecords(Vector<TaggedVector> updates, ArrayList<Object> fieldKeys) {
//      TaggedVector values;
//      GenericRecord record;
//      StoredObject target;
//      if (updates.size() == 1) {
//        values = updates.elementAt(0);
//
//        // See if the Record already exists.  Create it if it doesn't.
//        if ((record = values.getTag()) == null) {
//          record = createNewRecord();
//        }
//
//        // Replace all the Visible fields.
//        for (int iter = 0; iter < fieldKeys.size(); ++iter) {
//          record.replaceValue((String) fieldKeys.get(iter),
//                                           values.elementAt(iter));
//        }
//        target = record.getActiveReference();
//        if (target != null) {
//          target.updateDescription(record);
//          broadcastChange(record);
//        }
//      }
//    }    
//  }
}
/* @(#)TrainStore.java */
