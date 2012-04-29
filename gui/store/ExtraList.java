/* What: ExtraList.java
 *
 * What:
 *  This file creates a Selection list over the names of the crew
 *  on the extra board.
 */
package cats.gui.store;

import cats.crew.Callboard;
import cats.crew.Crew;
import cats.layout.store.FieldPair;
import cats.layout.store.GenericRecord;
import cats.layout.store.RecordVector;
import java.util.Enumeration;
import java.util.Vector;

/**
 *  This file creates a Selection list over the names of the crew
 *  on the extra board.
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2003, 2009, 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class ExtraList
    extends SelectionSpec {

  /**
   * is the no argument constructor.
   */
  public ExtraList() {
    this("");
  }

  /**
   * is the single argument constructor.
   *
   * @param spec is a dummy String.
   */
  public ExtraList(String spec) {
    super(spec);
  }

  /**
   * sets up the List from the specific store.
   *
   * @return the GenericRecords from the specific Store from which a
   * selection can be made.
   */
  protected RecordVector<?> initializeList() {
    Crew crew;
    Vector<Crew> crewList = Callboard.Crews.getExtras();
    FilteredList = new RecordVector<GenericRecord>(crewList.size() + 1);
    GenericRecord rec = new GenericRecord("", 1);
    SelectionTag = "crew";
    rec.add(new FieldPair(SelectionTag, ""));
    FilteredList.add(0, rec);
    for (Enumeration<Crew> e = crewList.elements(); e.hasMoreElements(); ) {
      crew = e.nextElement();
      rec = new GenericRecord("", 1);
      rec.add(new FieldPair(SelectionTag, new String(crew.getCrewName())));
      FilteredList.add(rec);
    }
    return FilteredList;
  }
}
/* @(#)ExtraList.java */