/* What: ClassSpec.java
 *
 * What:
 *  This is a class for creating a JComboBox in a JTable cell for selecting
 *  a Class for other Cells.
 */
package cats.gui.store;

import java.util.Enumeration;
import java.util.Vector;

import cats.crew.Crew;
import cats.jobs.Job;
import cats.layout.store.FieldPair;
import cats.layout.store.GenericRecord;
import cats.trains.Train;

/**
 *  This is an abstract class for formatting a JList as a JTableCell.  The
 *  concrete classes hold the objects that form the JList.  It is derived
 *  from the CATS SpecialType so that the StoreEditPane can locate the
 *  methods for acquiring a CellEditor and CellRenderer.
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2003, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

/**
 * is a filter for selecting certain file suffixes.
 */
public class ClassSpec
    extends ListSpec {

  /**
   * is the tag for recognizing the name of the Class.
   */
  private static final String CLASS_NAME = "CLASS_NAME";

  /**
   * is the tag of the field containing the Class.
   */
  private static final String CLASS_CLASS = "CLASS_CLASS";

  /**
   * are the classes that can appear in a JTable cell.
   */
  private static final Class<?>[] ClassList = {
      Boolean.class,
      ClassSpec.class,
      CrewList.class,
      ExtraList.class,
      Integer.class,
      String.class,
      TimeSpec.class,
      AlignmentList.class,
      TrainList.class,
      FontSpec.class,
      Train.class,
      Crew.class,
      Job.class
  };

  /**
   * is the list of Generic Records to select an item from.
   */
  private static Vector<GenericRecord> MasterList;

  /**
   * is the Tag of the field for retrieving the JList contents.
   */
  private static String IDTag = CLASS_NAME;

  /**
   * is the single argument constructor.
   *
   * @param c is the intial class.
   */
  public ClassSpec(Class<?> c) {
    super(c);
  }

  /**
   * is the sub-class specific method for building the Vector of labels
   * in the Selection dialog.
   */
  protected Vector<String> createSpecificList() {
    if (MasterList == null) {
      MasterList = new Vector<GenericRecord>();
      GenericRecord record;

      for (int c = 0; c < ClassList.length; ++c) {
        record = new GenericRecord("??", 1);
        record.add(new FieldPair(CLASS_CLASS, ClassList[c]));
        record.add(new FieldPair(CLASS_NAME,
                                 normalizeClassName(ClassList[c].toString())));
        MasterList.add(record);
      }
    }
    return createList(MasterList, IDTag);
  }

  /**
   * returns a JTableCellEditor for editing the time specification.
   *
   * @return the editor.
   */
//  public DefaultCellEditor getCellEditor() {
//    Vector myList = createList(MasterList, IDTag);
//    JComboBox combo = new JComboBox(myList);
//    return new DefaultCellEditor(combo);
//  }

  /**
   * returns a JTableCellRenderer for rendering the time specification.
   *
   * @return the renderer.
   */
//  public TableCellRenderer getCellRenderer() {
//    return new ComboRenderer(createList(MasterList, IDTag));
//  }

  /**
   * this method removes extraneous characters from a Class name.  Because
   * lastIndexOf returns -1 if '.' is not found, this method will return
   * the orginal String if it has already been normalized.
   *
   * @param className is the name being collapsed.
   *
   * @return className without "class" and initial qualifiers.
   */
  static public String normalizeClassName(String className) {
    int dot = className.lastIndexOf('.');
    return new String(className.substring(dot + 1));
  }

  /**
   * this method takes a Class name and finds the Class
   * that it names.  The Class name can be either shortened (as returned
   * from the normalizeClassName method), a fully qualifed class name,
   * or a fully qualified class name preceded by "class ".
   *
   * @param cName is the Class name.
   *
   * @return the Class it is the name of.
   */
  static public Class<?> toClass(String cName) {
    String normal = normalizeClassName(cName);
    GenericRecord classRecord;
    FieldPair pair = new FieldPair(CLASS_NAME, normal);
    for (Enumeration<GenericRecord> e = MasterList.elements(); e.hasMoreElements(); ) {
      classRecord = e.nextElement();
      if (classRecord.doesInclude(pair)) {
        return (Class<?>) classRecord.findPair(CLASS_CLASS).FieldValue;
      }
    }
    return null;
  }

  /**
   * is called when CATS starts up to initialize MasterList so that the toClass
   * method works.
   */
  static public void init() {
    ClassSpec cs = new ClassSpec(String.class);
    cs.createSpecificList();
  }
}
/* @(#)ClassSpec.java */