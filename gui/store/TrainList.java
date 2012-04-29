/* What: TrainList.java
 *
 * What:
 *  This file creates a Selection list over the names of the Trains
 *  which have not been run.
 */
package cats.gui.store;

import cats.layout.store.FieldPair;
import cats.layout.store.GenericRecord;
import cats.layout.store.RecordVector;
import cats.trains.Train;
import cats.trains.TrainStore;
import java.util.Enumeration;
import java.util.Vector;

/**
 *  This file creates a Selection list over the names of the Trains
 *  which have not been run.
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2003, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class TrainList
    extends SelectionSpec {

  /**
   * is the no argument constructor.
   */
  public TrainList() {
    this("");
  }

  /**
   * is the single argument constructor.
   *
   * @param spec is a dummy String.
   */
  public TrainList(String spec) {
    super(spec);
  }

  /**
   * sets up the List from the specific store.
   *
   * @return the GenericRecords from the specific Store from which a
   * selection can be made.
   */
  protected RecordVector<?> initializeList() {
    Train train;
    Vector<Train> trainList = TrainStore.TrainKeeper.getUnrun();
    FilteredList = new RecordVector<GenericRecord>(trainList.size() + 1);
    GenericRecord rec = new GenericRecord("", 1);
    SelectionTag = "train";
    rec.add(new FieldPair(SelectionTag, ""));
    FilteredList.add(0, rec);
    for (Enumeration<Train> e = trainList.elements(); e.hasMoreElements(); ) {
      train = e.nextElement();
      rec = new GenericRecord("", 1);
      rec.add(new FieldPair(SelectionTag, new String(train.getSymbol())));
      FilteredList.add(rec);
    }
    return FilteredList;
  }
}
/* @(#)TrainList.java */