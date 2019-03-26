/**
 * Name: IOSpecChainManager.java
 * 
 * What:
 *   Manages the decoder chains.  The Chains are in a separate
 *   System and Name space.  It would be great to merge it into
 *   the JMRI name space, but I would have to add a new type
 *   or spoof an existing type.
 */
package cats.jmri;

import java.util.ArrayList;
import java.util.Iterator;

import cats.layout.items.IOSpecChain;


/**
 *   Manages the decoder chains.  The Chains are in a separate
 *   System and Name space.  It would be great to merge it into
 *   the JMRI name space, but I would have to add a new type
 *   or spoof an existing type.  TODO: make IOSPecChain
 *   a NamedBean so they can be added to the JMRI name spce.
 *
 *   System names are "ICnnn", where nnn is the sensor number without padding.
 *   This code is derived from ther JMRI LnSensorManager.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2006, 2010, 2011, 2016</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
@SuppressWarnings("rawtypes")
public class IOSpecChainManager extends jmri.managers.AbstractManager {
  
  ArrayList<IOSpecChain> Chains;
  
  /**
   * JMRI system type is internal
   */
  private static final String MY_SPECIFIER = "I";
  
  /**
   * JMRI system device is Chain
   */
  private static final char MY_DEVICE = 'C';
  
  /**
   * JMRI system specifier
   */
//  private static final String MY_IDENTIFIER = MY_SPECIFIER + MY_DEVICE;
  
  public String getSystemPrefix() {
    return MY_SPECIFIER;
  }
  
  /**
   * is the device type identifier.
   */
  public char typeLetter() {
    return MY_DEVICE;
  }

  /*
   * return the system prefix
   */
//  @SuppressWarnings("deprecation")
//public String getSystemPrefix(){
//     return ""+systemLetter(); // for now.  if more than one, this needs
//                               // to be different
//  }
  
  /**
   * check for the existance of a Manager.
   *
   * @return the manager.  If one does not exist, create it.
   */
  static public IOSpecChainManager instance() {
    if (mInstance == null) {
      mInstance = new IOSpecChainManager();
    }
    return mInstance;
  }
  
  /**
   * is the singleton manager.
   */
  static private IOSpecChainManager mInstance = null;
  
  // to free resources when no longer used
  public void dispose() {
  }
  
  /**
   * constructor.
   */
  public IOSpecChainManager() {
    mInstance = this;
    Chains = new ArrayList<IOSpecChain>();
  }
 
  /**
   * finds a DecoderChain.  If it does not exist, one is created.
   * @param name the name of the Chain being requested
   * @return the DecoderChain
   */
  public IOSpecChain provideChain(String name) {
	    IOSpecChain c = getChain(name);
	    if (c!=null) return c;
	    String cName = name.toUpperCase();
	    if (cName.startsWith(""+getSystemPrefix()+typeLetter()))
	      return newChain(cName, null);
	    return newChain(makeSystemName(cName), null);
  }
  
  private IOSpecChain getChain(String name) {
    IOSpecChain c = getByUserName(name);
    if (c!=null) return c;
    
    return getBySystemName(name);
  }

  /**
   * searches the list of created DecoderChains for one with a matching
   * JMRI system name.
   * @param key the JMRI system name of the desired Chain
   * @return the DecoderChain, if found; or null, if not found.
   */
  public IOSpecChain getBySystemName(String key) {
    String name = key.toUpperCase();
    IOSpecChain c;
    for (Iterator<IOSpecChain> iter = Chains.iterator(); iter.hasNext();) {
      c = iter.next();
      if (c.getName().equals(name)) {
        return c;
      }
    }
    return null;
  }
  
  /**
   * searches the list of created DecoderChains for one with a matching
   * JMRI user name.
   * @param key the JMRI user name of the desired Chain
   * @return the DecoderChain, if found; or null, if not found.
   */
  public IOSpecChain getByUserName(String key) {
    String name = key.toUpperCase();
    IOSpecChain c;
    for (Iterator<IOSpecChain> iter = Chains.iterator(); iter.hasNext();) {
      c = iter.next();
      if (c.getName().equals(name)) {
        return c;
      }
    }
    return null;
  }
  
  private IOSpecChain newChain(String sysName, String userName) {
	  String systemName = sysName.toUpperCase();
	  if (log.isDebugEnabled()) {
		  log.debug("newChain:"
				  +( (systemName==null) ? "null" : systemName)
				  +";"+( (userName==null) ? "null" : userName));
	  }
	  if (systemName == null) {
		  log.error("SystemName cannot be null. UserName was "
				  +( (userName==null) ? "null" : userName));
		  return null;
	  }
	  // is system name in correct format?
	  if (!systemName.startsWith(""+getSystemPrefix()+typeLetter())) {
		  log.error("Invalid system name for chain: "+systemName
				  +" needed "+getSystemPrefix()+typeLetter());
		  return null;
	  }

	  // return existing if there is one
	  IOSpecChain c;
	  if ( (userName!=null) && ((c = getByUserName(userName)) != null)) {
		  if (getBySystemName(systemName)!=c)
			  log.error("inconsistent user ("+userName+") and system name ("+systemName+") results; userName related to ("+c.getName()+")");
		  return c;
	  }
	  if ( (c = getBySystemName(systemName)) != null) {
		  if ((c.getUserName() == null) && (userName != null))
			  c.setUserName(userName);
		  else if (userName != null) log.warn("Found chain via system name ("+systemName
				  +") with non-null user name ("+userName+")");
		  return c;
	  }

	  // doesn't exist, make a new one
	  c = createNewChain(systemName.substring(2), userName);

	  // save in the maps
	  //  register(c);

	  return c;
  }
  
  /**
   * Internal method to invoke the factory, after all the
   * logic for returning an existing method has been invoked.
   * @return new null
   */
  private IOSpecChain createNewChain(String systemName, String userName) {
    int addr = 0;
    boolean parseError = false;
    try {
      addr = Integer.parseInt(systemName);
    }
    catch (NumberFormatException nfe) {
      System.out.println("Illegal number for IOSpecChain " + systemName);
      log.warn("Illegal number for IOSpecChain " + systemName);
      parseError = true;
    }
    if (!parseError) {
      IOSpecChain i = new IOSpecChain(addr);
      i.setUserName(userName);
      Chains.add(i);
      return i;
    }
    return null;
  }
 
   public int getXMLOrder(){
 	return jmri.Manager.LOGIXS;
   }
  
  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(IOSpecChainManager.class.getName());

@Override
public String getBeanTypeHandled() {
	// TODO Auto-generated method stub
	return "IOSpecChain";
}
}
