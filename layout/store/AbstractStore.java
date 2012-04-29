/* Name: AbstractStore.java
 *
 * What;
 * This object contains the structure for a mini-database.  The database
 * holds records of things needed by CATS.  The intent is that it is edited
 * by a Table Editor.  So, the internal structure is driven to be compatible
 * with the Table Editor in jCustom.
 * <p>
 * The heart of the store is two components.
 * <ol>
 * <li>The DataStore holds the contents of the mini-database.  It is  a Vector
 * of Vectors.  The major dimension has one element for each record in the
 * mini-database.  The minor dimension has a pair of values for each field
 * in the mini-database.  One member of the pair is a tag identifying the
 * field.  The other is the value of the field.
 * <li>
 * The FieldInfoStore holds the presentation of the DataStore in CATS.  It
 * is a Vector of Vectors.  The major dimension has one
 * element for describing each field in a DataStore record.  The minor
 * dimension has a pair of values for each Field property.  One member of
 * the pair is a tag identifying the property and the other is the value
 * of the property.
 * </ol>
 * <p>
 * The Store is converted to XML by first writing the presentation Vector of
 * Vectors.  There is one FieldInfo element for each Field in a record.
 * The properties of the FieldInfo are written as element attributes.  The
 * DataStore is written next, one record per XML element.  The fields
 * in the records are written as attributes with the field identifiers
 * being the attribute tags.
 */
package cats.layout.store;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import cats.common.Constants;
import cats.gui.store.CatsTableModel;
import cats.layout.Logger;
import cats.layout.xml.XMLEleFactory;
import cats.layout.xml.XMLEleObject;
import cats.layout.xml.XMLReader;

/**
 * This object contains the structure for a mini-database.  The database
 * holds records of things needed by CATS.  The intent is that it is edited
 * by a Table Editor.  So, the internal structure is driven to be compatible
 * with the Table Editor in jCustom.
 * <p>
 * The heart of the store is two components.
 * <ol>
 * <li>The DataStore holds the contents of the mini-database.  It is  a Vector
 * of Vectors.  The major dimension has one element for each record in the
 * mini-database.  The minor dimension has a pair of values for each field
 * in the mini-database.  One member of the pair is a tag identifying the
 * field.  The other is the value of the field.
 * <li>
 * The FieldInfoStore holds the presentation of the DataStore in CATS.  It
 * is a Vector of Vectors.  The major dimension has one
 * element for describing each field in a DataStore record.  The minor
 * dimension has a pair of values for each Field property.  One member of
 * the pair is a tag identifying the property and the other is the value
 * of the property.
 * </ol>
 * <p>
 * The Store is converted to XML by first writing the presentation Vector of
 * Vectors.  There is one FieldInfo element for each Field in a record.
 * The properties of the FieldInfo are written as element attributes.  The
 * DataStore is written next, one record per XML element.  The fields
 * in the records are written as attributes with the field identifiers
 * being the attribute tags.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2010, 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public abstract class AbstractStore
    implements XMLEleObject {

  /**
   * are the default attributes (columns)
   */
  private final FieldInfo[] DEFAULT_ATTRIBUTES;

  /**
   * is TAG in the XML file for identifying the RecordVector
   */
  private final String REC_TAG;
  
  /**
   * is the TAG in the XML file for identifying the FieldVector
   */
  private final String FIELD_TAG;
  
  /**
   * is the TAG in the XML file for identifying this instantiation of the
   * AbstractStore.
   */
  protected final String XML_TAG;

  /**
   * are the Field descriptions.
   */
  protected FieldVector FieldInfoStore;

  /**
   * are the contents.
   */
  protected RecordVector<GenericRecord> DataStore;

  /**
   * is the list of listeners
   */
  private LinkedList<AbstractStoreWatcher> Observers;
  
  /**
   * is the constructor.
   *
   * @param sTag is the XML tag of the AbstractStore.
   * @param fTag is the XML tag of the FieldStore.
   * @param rTag is the XML tag of the RecordStore.
   * @param prop is the initial Field keys.
   *
   */
  public AbstractStore(String sTag, String fTag, String rTag, FieldInfo[] prop) {
	  //    this();
	  //    XmlTag = sTag;
	  //    Fields = new FieldVector();
	  //    Data = new RecordVector<GenericRecord>();
	  //    Fields.loadFields(prop);
	  Observers = new  LinkedList<AbstractStoreWatcher>();
	  DEFAULT_ATTRIBUTES = prop;
	  XML_TAG = sTag;
	  REC_TAG = rTag;
	  FIELD_TAG = fTag;
	  reNew();
  }

  /**
   * is the no argument constructor.
   */
//  public AbstractStore() {
//    Observers = new  LinkedList<AbstractStoreWatcher>();
//  }

  /**
   * This method tailors the AbstractStore to the concrete instantiation
   * by returning the FieldTag String on the pair that is unique.
   * @return the FieldTag that identifies the unique pair in each data record.
   */
//  public abstract String getUniqueFieldTag();
    
  /**
   * is a method that tailors the AbstractStore to the concrete instantiation
   * by supplying the Java Class of an Object
   * described by a GenericRecord in the concrete store.
   *
   * @return a Class that can be used to create a StoredObject.
   */
//  protected abstract Class getObjectClass();

  /**
   * invokes the StoreEditPane specific editor for the Store.
   * @param model is the model that drives the JTable
   * @param trial is a copy of the data being edited
   */
//  protected abstract void runEditor(RecordTableModel model, Vector<TaggedVector> trial);
  
  /**
   * is a method for adding the FieldInfo that is a factory for creating the
   * StoredObject to Fields.  It will be unique for each concrete class.
   */
//  protected void setReferenceInfo() {
//    Class targetClass = getObjectClass();
//    GenericRecord oldKey = Fields.search(new FieldPair(FieldInfo.KEY_TAG,
//                                                FieldInfo.KEY_TAG), null);
//    FieldInfo info = new FieldInfo(FieldInfo.KEY_TAG,
//                                   false,
//                                   FieldInfo.KEY_TAG,
//                                   false,
//                                   true,
//                                   FieldInfo.NARROW_WIDTH,
//                                   null,
//                                   targetClass);
//    info.replacePair(new FieldPair(FieldInfo.CLASS_TAG, targetClass));
//    if (oldKey != null) {
//      Fields.remove(oldKey);
//    }
//    Fields.insertElementAt(info, 0);
//  }

  /**
   * retrieves the Vector of Field descriptions.
   *
   * @return the Vector of FieldInfo, the descriptions of the Fields.
   */
//  public FieldVector getFieldDescriptions() {
//    return Fields;
//  }

  /**
   * creates a new record, based on the data descriptions for each field.
   * This method should be called only from a Table pane.
   *
   * @return the newly created record.
   */
//  public GenericRecord createNewRecord() {
//    return Visible.createDefaultRecord();
//  }

  /**
   * creates a list of the values of the key fields
   * @return the list of key values
   */
  public ArrayList<String> listKeys() {
	  ArrayList<String> keyList = new ArrayList<String>();
	  for (GenericRecord gr : DataStore) {
		  keyList.add(gr.getRecordKey());
	  }
	  return keyList;
  }
  
  /**
   * creates a new record.
   *
   * @param keyName is the key value of the record being created.
   * 
   * @return the StoredObject that the record refers to.
   */
  public StoredObject createRecord(String keyName) {
    GenericRecord rec = FieldInfoStore.createDefaultRecord(getDataID(), keyName);
//    rec.replacePair(new FieldPair(keyLabel, keyName));
//    DataStore.replaceRecord(rec);
    addTrustedRecord(rec);
    return rec.getActiveReference();
  }

  /**
   * searches the AbstractStore for the record with
   * a field.
   *
   * @param label is the tag of the field.
   * @param value is the value of the field being searched for.
   *
   * @return the record, if found, or null if not found.
   */
  public GenericRecord findRecord(String label, String value) {
    GenericRecord rec = null;
    if ( (value != null) && (!value.trim().equals(""))) {
      rec = DataStore.search(new FieldPair(label, value), null);
    }
    return rec;
  }

  /**
   * searches the AbstractStore for the StoredObject with a field.
   * 
   * @param label is the tag of the field
   * @param value is the value of the field being searched for
   * 
   * @return the StoredObject, if found, or null, if not found.
   */
  public StoredObject findStoredObject(String label, String value) {
    GenericRecord rec = findRecord(label, value);
    if (rec != null) {
      return rec.getActiveReference();
    }
    return null;
  }
  
  /**
   * adds a new Object description.  This method is for compatibility with Versions prior
   * to 16.
   *
   * @param tag is the Tag on the Job Record.
   * @param record is the GenericRecord containing the Job data.
   */
//  public void addRecord(String tag, GenericRecord record) {
//    Data.setObject(tag, record);
//  }

  /**
   * deletes a GenericRecord from the RecordStore.
   *
   * @param record is the GenericRecord.
   */
  public void delRecord(GenericRecord record) {
    FieldPair pair;
    String removeEntry;
    if (record != null) {
      pair = record.getRecordKeyPair();
      if (pair != null) {
        removeEntry = AbstractStoreWatcher.buildRemoveString(DataStore.getTag() + Constants.FS + pair.toString());
        for (Iterator<AbstractStoreWatcher> iter = Observers.iterator(); iter.hasNext(); ) {
          iter.next().broadcast(removeEntry);
        }
      }
      DataStore.remove(record);
    }
  }

  /**
   * changes the contents of an existing GenericRecord.  If a record with the same
   * key exists, then the fields in the request replace those in the existing
   * record.  The requested changes do not have to be complete (i.e. the parameter
   * can be missing fields).  If a record with the same name does not exist, then
   * a new record is created and added to the Store.  Because the request may be
   * incomplete, it must be verified against the FieldInfoStore.
   * 
   * @param changes contains the new values.
   */
  public void changeRecord(GenericRecord changes) {
    String keyTag = FieldInfoStore.getKeyField();
    FieldPair keyValue;
    GenericRecord rec;
    StoredObject so;
    if ((null != keyTag) && (null != (keyValue = changes.findPair(keyTag)))) {
      if (null == (rec = DataStore.search(keyValue, null))) {
        addUntrustedRecord(changes);
        broadcastAdd(changes);
      }
      else {
    	  so = rec.getActiveReference();
    	  if (so != null) {
    		  FieldInfoStore.cleanseRecord(changes);
    		  so.updateDescription(changes);
    	  }
//        broadcastChange(changes);
      }
//      changes.checkValues(FieldInfoStore);
//      FieldInfoStore.recordFromXML(changes);
//      rec.replaceValues(changes);
//      so.updateDescription(changes);
    }
  }

  /**
   * changes a single field in a record
   * @param key is the value of the key field in the desired record
   * @param tag is the tag on the field to be changed
   * @param value is the new value of the field
   */
  public void changeField(String key, String tag, String value) {
		GenericRecord updates = new GenericRecord();
		updates.add(new FieldPair(getKeyTag(), key));
		updates.add(new FieldPair(tag, value));
		changeRecord(updates);
  }
  
  /**
   * is a method that should be defined by each sub-class for filtering
   * out Records for editing.  The filter is specific to each sub-class.
   * This should probably be an abstract class, but since a FieldStore
   * is a sub-class of RecordStore and FieldStores are not edited, this
   * method is never invoked.  So, a "do-nothing" method is provided
   * here.
   *
   * @param rec is a GenericRecord from the RecordStore.
   *
   * @return true if the record is selected.
   */
//  protected boolean isSelected(GenericRecord rec) {
//    return true;
//  }
  
  /**
   * this method runs through all fields in all records.  It selects those
   * fields that have the Visible property set and writes the associated
   * values to a Vector of Vectors.  This composite structure forms the
   * data structure that the editor manipulates.
   *
   *@param fieldKeys contains the field names of the fields to be edited.
   *
   * @return a Vector that has one element for each record.  Each element
   * is a Vector of values from the Record, of the Visible fields.
   */
//  protected Vector<TaggedVector> extractVisibleFields(ArrayList<Object> fieldKeys) {
//    Vector<TaggedVector> vec = new Vector<TaggedVector>(Data.size());
//    GenericRecord record;
//    TaggedVector values;
//    for (Enumeration<GenericRecord> rec = Data.elements(); rec.hasMoreElements(); ) {
//      record = rec.nextElement();
//      if (isSelected(record) ) {
//        values = record.extractValues(fieldKeys);
//        values.setTag(record);
//        vec.add(values);
//      }
//    }
//    return vec;
//  }

  /**
   * is the method that starts off the editor on the DataStore.
   * <p>
   * The local variable visible is the cornerstone to making this all
   * work.  It is a subset of the FieldVector containing the fields
   * that have the visible property set.  The order of its elements
   * dictates the order that the record fields are placed in the JTable. 
   */
//  public void editData() {
//    RecordTableModel model;
//    Visible = Fields.getVisibleInfos();
//    ArrayList<Object> fieldKeys = Visible.getSlice(FieldInfo.KEY_TAG);
//    Vector<TaggedVector> trial = extractVisibleFields(fieldKeys);
//    String[] headings = new String[Visible.size()];
//    FieldInfo info;
//    int f = 0;
//    for (Enumeration<FieldInfo> e = Visible.elements(); e.hasMoreElements(); ) {
//      info = e.nextElement();
//      if (info.getVisible()) {
//        headings[f++] = new String(info.getLabel());
//      }
//    }
//    model = new RecordTableModel(trial, headings);
//    model.setStore(this);
//    runEditor(model, trial);
////    if (StoreEditPane.editRecords(model)) {
////      updateRecords(trial, fieldKeys);
////    }
//  }

  /**
   * constructs a CatsTableModel for the concrete class
   * @param data is a copy of the DataStore
   * @param format is a copy of the FieldInfoStore
   * @return a concrete CatsTableModel for the concrete Store.
   */
  protected CatsTableModel createModel(RecordVector<GenericRecord> data,
      FieldVector format) {
    return new CatsTableModel(data, format, this);
  }

  /**
   * processes the results of editing the AbstractStore
   * @param trial is a copy of the AbstractStore that was edited.
   */
  protected void updateRecords(Vector<GenericRecord> trial) {
    GenericRecord edit;
    GenericRecord master;
    StoredObject so;
    FieldPair keyPair;
    String keyTag = FieldInfoStore.getKeyField();
    RecordVector<GenericRecord> tempStore = DataStore.duplicate();
    String status;
    
    // preserve the order of records from the edit
    DataStore.clear();
    for (Enumeration<GenericRecord> e = trial.elements(); e.hasMoreElements(); ) {
      edit = e.nextElement();
      status = edit.getStatus();
      keyPair = edit.findPair(keyTag);
      master = tempStore.search(keyPair, null);
      so = edit.getActiveReference();
      
      if (master == null) {
        // the record was just created, so if it was not also just deleted,
        // add it to the Store
        if (!status.equals(GenericRecord.DELETED)) {
          edit.setStatus(GenericRecord.UNCHANGED);
          addTrustedRecord(edit);
        }
      }
      else {
        // determine results of edit
        if (status.equals(GenericRecord.DELETED)) {
          so.destructor();
        }
        else if (status.equals(GenericRecord.CHANGED)) {
          edit.setStatus(GenericRecord.UNCHANGED);
          DataStore.add(master);
          removeSpecialPairs(edit);
          so.updateDescription(edit);
        }
        else if (status.equals(GenericRecord.UNCHANGED)) {
          DataStore.add(master);
        }
      }
    }
  }
  
  /**
   * is the method that starts off the editor on the DataStore.
   */
  public abstract void editData();
//  public void editData() {
//    FieldVector fields = FieldInfoStore.toFormatting();
//    RecordVector<GenericRecord> trial = DataStore.makeCopy();
//    trial.setStrategy(new DefaultSelectionStrategy(trial));
//    if (StoreEditPane.editRecords(createModel(trial, fields), FRAME_TITLE)) {
//      updateRecords(trial);
//    }
//  }

  /**
   * removes any special FieldPairs from a GenericRecord before it is
   * pushed into the StoredObject.  The fields typically will be dependent
   * upon some other field value, so if they are not removed, the old
   * value will replace the dependent value.  In general, nothing needs to
   * be removed, but each AbstractStore has its own special needs.
   * 
   * @param rec is the GenericRecord containing the FieldPairs
   */
  protected void removeSpecialPairs(GenericRecord rec) {
    
  }

  /**
   * saves the result of editing the Records.  It steps through the Vector,
   * looking at each TaggedVector.  The tag piece of the tagged Vector
   * references the original GenericRecord.  So, it updates each field in
   * the original GenericRecord with the values as a result of the edit.
   * Finally, it removes the GenericRecord and adds it back at the end
   * of the DataStore.  This last operation preserves the order as a result
   * of editing.
   *
   * @param updates is a Vector of Vectors of field Values from the JTable
   */
//  public void updateRecords(Vector<TaggedVector> updates) {
//    TaggedVector values;
//    GenericRecord record;
//    StoredObject target;
//    boolean changed;
//    ArrayList<Object> fieldKeys= Visible.getSlice(FieldInfo.KEY_TAG);
//    for (Enumeration<TaggedVector> rec = updates.elements(); rec.hasMoreElements(); ) {
//      values = rec.nextElement();
//
//      // See if the Record already exists.  Create it if it doesn't.
//      if ((record = values.getTag()) == null) {
//        record = createNewRecord();
//      }
//
//      changed = false;
//      // Replace all the Visible fields.
//      for (int iter = 0; iter < fieldKeys.size(); ++iter) {
//        changed |= record.replaceValue((String) (fieldKeys.get(iter)),
//                                         values.elementAt(iter));
//      }
//      target = record.getActiveReference();
//      if (target != null) {
//        target.updateDescription(record);
//      }
//      
//      if (Data.contains(record)) {
//        // to maintain the order from the edit, remove the record from its
//        // current location and add it at the end.
//        Data.remove(record);
//        Data.add(record);
//        if (changed) {
//          broadcastChange(record);
//        }
//      }
//      else {  // New record
//        Data.add(record);
//          broadcastAdd(record);
//      }
//    }
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
    return model.defaultVerifyResults();
  }

  /**
   * is invoked to persist format changes (for the duration of the program)
   * @param newFormat is the edited copy of the formatting
   */
  protected void updateFormat(FieldVector newFormat) {
    FieldInfo oldF;
    FieldInfo newF;
    for (Object element : newFormat) {
      newF = (FieldInfo) element;
      oldF = FieldInfoStore.getFieldInfo(newF.getKeyField());
      if (oldF != null) {
        oldF.findPair(FieldInfo.WIDTH_TAG).FieldValue = newF.findPair(FieldInfo.WIDTH_TAG).FieldValue;
      }
    }
  }

  /**
   * is invoked to add a GenericRecord whose fields have been verified against the FieldVector,
   * to the RecordVector.  GenericRecords created through FieldVector.createDefaultRecord (i.e.
   * the editor) meet this criteria, as do GenericRecords processed by FieldVector.recordFromXML.
   * Because it was verified by the FieldVector, it has all
   * the defined fields and only the defined fields, so some shortcuts can be taken.
   * @param rec is a GenericRecord which has been checked by the FieldVector.
   */
  public void addTrustedRecord(GenericRecord rec) {
    FieldPair recKey = rec.getRecordKeyPair();
    GenericRecord existing;
    if (recKey == null) {
      log.warn("A " + getDataID() + " record is missing its key field"); 
    }
    else {
      if (((String) recKey.FieldValue) == null) {
        log.warn("A " + getDataID() + " record has a blank key");
      }
      else {
        if ((existing = DataStore.search(recKey, null)) == null) {
          // no record with the same key exists, so add it
          rec.getActiveReference().linkDescription(rec);
          DataStore.add(rec);
          broadcastAdd(rec);
        }
        else {
          existing.getActiveReference().updateDescription(rec);
        }
      }
    }
  }

  /**
   * is invoked to add a GenricRecord whose fields have not been verified against the FieldVector
   * to the RecordVector.  It verifies them, then invokes addTrustedRecord.
   * @param rec is a GenricRecord which has not been verified.  Since it is being added to the
   * RecordVector, extraneous fields must be removed and missing fields added.  rec is modified
   * in the process.
   */
  public void addUntrustedRecord(GenericRecord rec) {
    FieldInfoStore.recordFromXML(rec);
    addTrustedRecord(rec);
  }
  
  /**
   * is called to get the tag on the key field for the enclosed GenericRecords.
   * This is a plug-in, supplied by the concrete instantiation.
   * 
   * @return the tag on the key field of the concrete instantiation.
   */
  public String getKeyTag() {
       return FieldInfoStore.getKeyField();
  }

  /**
   * restores a Store to a clean (empty) state.
   *
   */
  public void reNew() {
      FieldInfoStore = new FieldVector(DEFAULT_ATTRIBUTES, FIELD_TAG);
      DataStore = new RecordVector<GenericRecord>(REC_TAG);
  }

  /**
   * constructs an ArrayList containing the tag:value pairs of
   * each element in a store
   * @param is the Vector of records (store)
   * @return the Vector of records as tag:value pairs
   */
  private ArrayList<String> dumpStore(RecordVector<GenericRecord> store) {
    ArrayList<String> contents = new ArrayList<String>(store.size());
    for (int i = 0; i < store.size(); ++i) {
      contents.add(store.get(i).toString());
    }
    return contents;
  }

  /**
   * constructs a list of the format store.
   * @return Fields as an Array list of string, with the format
   * records encoded as tag:value pairs in Strings
   */
  public ArrayList<String> dumpFieldContents() {
//    ArrayList<String> contents = new ArrayList<String>(FieldInfoStore.size());
//    for (int i = 0; i < FieldInfoStore.size(); ++i) {
//      contents.add(FieldInfoStore.get(i).toString());
//    }
//    return contents;
    return FieldInfoStore.dumpFieldContents();
  }
  
  /**
   * constructs a list containing the contents of the AbstractStore,
   * one record per line.  The fields in each record are separated by
   * tabs.
   * @return the contents in an ArrayList, one record per element.
   */
  public ArrayList<String> dumpStoreContents() {
    return dumpStore(DataStore);
  }

  /**
   * returns the XML id of the DataStore
   * @return the XML id of the data
   */
  public String getDataID() {
    return new String(DataStore.getTag());
  }
  
  /**
   * returns the XML id of the FieldStore
   * @return the XML id of the data
   */
  public String getFieldID() {
    return new String(FieldInfoStore.getTag());
  }
  
  /**
   * registers an observer for changes
   * @param observer is the object interested in changes
   */
  public void registerObserver(AbstractStoreWatcher observer) {
    if (!Observers.contains(observer)) {
      Observers.add(observer);
    }
  }
  
  /**
   * deregisters an observer
   * @param observer is the object that is no longer interested
   * in changes.
   */
  public void deregisterObserver(AbstractStoreWatcher observer) {
    Observers.remove(observer);
  }

  /**
   * broadcasts to all observers the changes in a record.  The record need not be
   * complete.
   * @param record is the GenericRecord that was (possibly) changed
   */
  public void broadcastChange(GenericRecord record) {
    String change = AbstractStoreWatcher.buildChangeString(DataStore.getTag() + Constants.FS + record.valueString());
    for (Iterator<AbstractStoreWatcher> iter = Observers.iterator(); iter.hasNext(); ) {
      iter.next().broadcast(change);
    }    
  }
  
  /**
   * broadcasts to all observers an added record
   * @param record is the GenericRecord that was added
   */
  public void broadcastAdd(GenericRecord record) {
    String addEntry = AbstractStoreWatcher.buildAddString(getDataID() + Constants.FS + record.valueString());
    for (Iterator<AbstractStoreWatcher> iter = Observers.iterator(); iter.hasNext(); ) {
      iter.next().broadcast(addEntry);
    }    
  }

  /**
   * constructs a timestamped message and sends it to all listeners.
   * @param tag is the identity on the message
   * @param message is the contents of the message
   */
  public void broadcastTimestamp(String tag, String message) {
    String update = Logger.timeStamp(tag, message);
    for (Iterator<AbstractStoreWatcher> iter = Observers.iterator(); iter.hasNext(); ) {
      iter.next().broadcast(update);
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
   * <p>
   * Only two kinds of embedded objects can be received - FieldVectors
   * (containing the format information) and RecordVectors (containing
   * the actual data).  FieldInfos are validated and duplicates removed
   * when they are added to the FieldInfo.  GenericRecords are validated
   * and duplicates removed here.
   * <p>
   * Another difference in how the two Vectors are handled is that the
   * FieldVector replaces any existing one (one is created as a default,
   * so any FieldVector read in should replace the existing one).  RecordVectors,
   * on the otherhand, merge into any existing one.
   *
   * @param objName is the name of the embedded object
   * @param objValue is the value of the embedded object
   *
   * @return null if the Object is acceptable or an error String
   * if it is not.
   */
  @SuppressWarnings("unchecked")
public String setObject(String objName, Object objValue) {
    String resultMsg = null;
    GenericRecord rec;
    if (FieldInfoStore.getTag().equals(objName)) {
      FieldInfoStore = (FieldVector) objValue;
      // need to bring any DataStore to conform
      if (DataStore != null) {
        for (Enumeration<GenericRecord> element = DataStore.elements(); element.hasMoreElements(); ) {
          FieldInfoStore.recordFromXML(element.nextElement());
        }
      }
    }
    else if ( (DataStore != null) && (DataStore.getTag() != null) &&
        (DataStore.getTag().equals(objName))) {
//      DataStore = (RecordVector<GenericRecord>) objValue;
//      if (FieldInfoStore != null) {
//        FieldInfoStore.vectorFromXML(DataStore);
//      }
      for (Enumeration<GenericRecord> element = ((RecordVector<GenericRecord>) objValue).elements();
          element.hasMoreElements(); ) {
        rec = element.nextElement();
        addUntrustedRecord(rec);
      }
    }
    return resultMsg;
  }
//  public String setObject(String objName, Object objValue) {
//    String resultMsg = null;
//    if (Fields.getTag().equals(objName)) {
//      Fields = (FieldVector) objValue;
//    }
//    else if (Data.getTag().equals(objName)) {
//      Data = (RecordVector<GenericRecord>) objValue;
//      Data.castVector(Fields);
//    }
//    else {
//      resultMsg = new String("A " + XmlTag + " cannot contain an " + objName +").");
//    }
//    return resultMsg;
//  }

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
   * be made; thus, it can do any error checking that it needs.
   *
   * @return null, if it has received everything it needs or an error
   * string if something isn't correct.
   */
  public String doneXML() {
    return null;
  }

  /**
   * registers a Factory for accepting a Store
   * with the XMLReader.
   */
  public void init() {
//    XMLReader.registerFactory(XmlTag, new Factory(this, XmlTag));
//    Fields.init();
//    Data.init();
    XMLReader.registerFactory(XML_TAG, new Factory(this, XML_TAG));
    FieldInfoStore.init(FIELD_TAG, DEFAULT_ATTRIBUTES);
    DataStore.init(REC_TAG);
  }

  /**
   * is a Class known only to the AbstractStore class for creating a
   * Database from an XML document.
   */
  class Factory
      implements XMLEleFactory {

    /**
     * is the product produced by the Factory.
     */
    private AbstractStore Product;

    /**
     * is the XML Tag of the product.
     */
    private String ProductTag;

    /**
     * is the constructor.
     *
     * @param product is the product produced by the Factory.
     *
     *@param tag is the XML tag of the product.
     */
    Factory(AbstractStore product, String tag) {
      Product = product;
      ProductTag = tag;
    }

    /*
     * tells the factory that an XMLEleObject is to be created.  Thus,
     * its contents can be set from the information in an XML Element
     * description.
     */
    public void newElement() {
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

      resultMsg = new String("A " + ProductTag + " XML Element cannot have a " +
                             tag +
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
      return Product;
    }
  }
  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AbstractStore.class.getName());
}
/* @(#)AbstractStore.java */
