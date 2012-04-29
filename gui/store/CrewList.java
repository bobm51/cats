/* What: CrewList.java
 *
 * What:
 *  This file creates a Selection list over the names of the crew
 *  members on the CallBoard.  It differs from ExtraList
 *  that it includes all crew members, while the ExtraList is those
 *  CrewMembers that work multiple trains.
 */
package cats.gui.store;

import cats.crew.Callboard;
import cats.layout.store.FieldPair;
import cats.layout.store.GenericRecord;
import cats.layout.store.RecordVector;
import java.util.Enumeration;
import java.util.Vector;

/**
 *  This file creates a Selection list over the names of the crew
 *  members on the CallBoard.  It differs from ExtraList
 *  that it includes all crew members, while the ExtraList is those
 *  CrewMembers that work multiple trains.
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2003, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class CrewList
    extends SelectionSpec {

  /**
   * is the no argument constructor.
   */
  public CrewList() {
    this("");
  }

  /**
   * is the single argument constructor.
   *
   * @param spec is a dummy String.
   */
  public CrewList(String spec) {
    super(spec);
  }

  /**
   * sets up the List from the specific store.
   *
   * @return the GenericRecords from the specific Store from which a
   * selection can be made.
   */
  protected RecordVector<?> initializeList() {
    String crew;
    Vector<String> crewList = Callboard.Crews.getNames();
    FilteredList = new RecordVector<GenericRecord>(crewList.size() + 1);
    GenericRecord rec = new GenericRecord("", 1);
    SelectionTag = "crew";
    rec.add(new FieldPair(SelectionTag, ""));
    FilteredList.add(0, rec);
    for (Enumeration<String> e = crewList.elements(); e.hasMoreElements(); ) {
      crew = e.nextElement();
      rec = new GenericRecord("", 1);
      rec.add(new FieldPair(SelectionTag, new String(crew)));
      FilteredList.add(rec);
    }
    return FilteredList;
  }
}
/* @(#)CrewList.java */