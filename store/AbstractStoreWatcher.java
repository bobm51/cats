/* Name: AbstractStoreWatcher.java
 *
 * What:
 *  This file is an abstract class that watches for changes in a Store.
 *  It watches for additions, deletions, and changes to the contents
 *  of an item.  The concrete implementations pass the changes to
 *  a logger or through a network connection.
 */
package cats.layout.store;

import java.util.Iterator;

import cats.common.Constants;
import cats.crew.Callboard;
import cats.jobs.JobStore;
import cats.layout.Logger;
import cats.trains.TrainStore;

/**
 *  This file is an abstract class that watches for changes in a Store.
 *  It watches for additions, deletions, and changes to the contents
 *  of an item.  The concrete implementations pass the changes to
 *  a logger or through a network connection.
 * <p>Title: CATS - Computer Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2010, 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public abstract class AbstractStoreWatcher {
  
  /**
   * is the method that forwards the changes to
   * a logger or across the network.  This is the method
   * that is tailored to the concrete implementations.
   * 
   * @param msg is the status update in String form.
   */
  protected abstract void forward(String msg);
  
  /**
   * registers the watcher with all the Stores
   */
  public void register() {
    TrainStore.TrainKeeper.registerObserver(this);
    JobStore.JobsKeeper.registerObserver(this);
    Callboard.Crews.registerObserver(this);
  }
  
  /**
   * unregisters the watcher with all Stores
   */
  public void unregister() {
    TrainStore.TrainKeeper.deregisterObserver(this);
    JobStore.JobsKeeper.deregisterObserver(this);
    Callboard.Crews.deregisterObserver(this);    
  }

  /**
   * receives a string from the observed object and forwards it.
   * 
   * @param message is the String being broadcast
   */
  public void broadcast(String message) {
    forward(message);
  }
  
  /**
   * dumps the format and contents of a Store
   * 
   * @param store is the data store being sent
   */
  private void dumpStore(AbstractStore store) {
    String storeId = store.getDataID() + Constants.FS_STRING;
    for(Iterator<String> iter = store.dumpStoreContents().iterator(); iter.hasNext(); ) {
      forward(buildAddString(storeId + iter.next()));
    }
  }
  
  /**
   * sends the state of all stores to the far end(s)
   */
  public void dumpAllStores() {
    dumpStore(TrainStore.TrainKeeper);
    dumpStore(JobStore.JobsKeeper);
    dumpStore(Callboard.Crews);
  }
  

  /**
   * the following are used to construct consistent Strings
   */
  /**
   * attaches the prefix to a deletion String
   * @param del is the body of the deletion string
   * @return a String that is the concatenation of a time stamp, "remove"
   * and the body of the deletion string
   */
  public static String buildRemoveString(String del) {
    return (Logger.timeStamp(Constants.REMOVE_FROM_STORE, del));
  }
  
  /**
   * attaches the prefix to an addition String
   * @param add is the body of the add string
   * @return a String that is the concatenation of a time stamp, "add"
   * and the body of the addition string
   */
  public static String buildAddString(String add) {
    return (Logger.timeStamp(Constants.ADD_TO_STORE, add));
  }
  
  /**
   * attaches the prefix to a change String
   * @param change is the body of the add string
   * @return a String that is the concatenation of a time stamp, "change"
   * and the body of the change string
   */
  public static String buildChangeString(String change) {
    return (Logger.timeStamp(Constants.CHANGE_STORE, change));
  }
}
/* @(#)StoredObject.java */
