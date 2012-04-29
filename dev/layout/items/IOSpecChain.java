/* Name: IOSpecChain.java
 *
 * What:
 *  This is a class for holding multiple IOSpecs.
 */
package cats.layout.items;

import cats.jmri.IOSpecChainManager;
import cats.layout.items.IOSpec;
import cats.jmri.JmriPrefixManager;
import cats.layout.xml.*;

import java.util.Enumeration;
import java.util.Vector;

/**
 * is a class for holding multiple IOSpecs.  It is assumed to be non-changing.
 * It is created and IOSpecs are added to it, but they are not changed or
 * removed.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A model railroad dispatching program</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class IOSpecChain implements IOInterface, XMLEleObject {
  /**
   *   The XML tag for recognizing the Block description.
   */
  public static final String XML_TAG = "IOSPECCHAIN";
  
  /**
   * The JMRI prefix for an IOSpecChain
   */
  public static final String CHAIN_PREFIX = "IC";
  
  /**
   * is the IOSpecChain instantiation counter for converting
   * older XML file to the latest.  0 is reserved to be the
   * temporary address during a copy operation.
   */
  static private int Counter = 1;
  
  /**
   * is the address to send the trigger to.
   */
  private int DecAddr;

  /**
   * is the number of milliseconds to wait after starting the
   * chain before starting the next command in the parent chain.
   */
  private int Delay;
  
  /**
   * is the JMRI user name
   */
  private String UserName;
  
  /**
   * is the JMRI prefix name used for finding the specific decoder.
   */
  private String JmriPrefix;
  
  /**
   * is the Vector of composite IOSpecs.
   */
  private Vector<IOInterface> Specs;

  /**
   * is the delay timer
   */
  private javax.swing.Timer DelayTimer;
  
  /**
   * are the Objects that send the commands.
   */
  private CommandBot NormalCommand;  // sends the default command
  private CommandBot UndoCommand;    // sends the Undo command
  private CommandBot ForceCommand;   // sends the Force state command 

  /**
   * remembers which request is being made.
   */
  private CommandBot ExecutingCommand;
  
  /**
   * is the null parameter ctor.  It is called when an
   * anonymous IOSpecChain is encountered.  This happens
   * only when migrating from a version of CATS prior to
   * the designer 21 version.  This means if one is seen,
   * no non-anonymous IOSpecLists will be seen, so we can
   * sequentially number them, as found.
   */
  public IOSpecChain() {
    this(Counter++);
  }
  
  /**
   * is the ctor, when the "system address" (number) is
   * known.
   * 
   * @param addr is the system identifier
   */
  public IOSpecChain(int addr) {
    JmriPrefix = new String(CHAIN_PREFIX);
    Specs = new Vector<IOInterface>();
    if (addr == 0) {
      DecAddr = Counter++;
    }
    else {
      DecAddr = addr;
    }
    
    NormalCommand = new CommandBot() {
      public void runCommand(IOInterface decoder) {
       decoder.sendCommand(); 
      }
    };
    
    UndoCommand = new CommandBot() {
      public void runCommand(IOInterface decoder) {
        decoder.sendUndoCommand();
      }
    };
    
    ForceCommand = new CommandBot() {
      public void runCommand(IOInterface decoder) {
        decoder.forceState(state);
      }
    };
  }
  
  /**
   * sends a command to the decoders to set them to their normal
   * state.
   */
  public void sendCommand() {
    runChain(NormalCommand);
  }
  
  /**
   * sends the deactivate command (if it exists).  This sets the
   * decoder to its other state.
   */
  public void sendUndoCommand() {
    runChain(UndoCommand);
  }
  
  /**
   * forces the decoder into a particular state.
   * 
   * @param state is the state (Throw or Close) to send the decoder to.
   */
  public void forceState(boolean state){
    ForceCommand.state = state;
    runChain(ForceCommand);
  }

  /**
   * initiates running a sequence of commands.  If there is a delay
   * between any, it starts the timer.
   * 
   * @param bot is the object holding the command,
   */
  private void runChain(CommandBot bot) {
    ExecutingCommand = bot;
    ExecutingCommand.nextDecoder = 0;
    ExecutingCommand.runNext();
  }

  /**
   * retrieves the decoder's name, which is used to identify it to the
   * control structure.
   *
   * @return the name of the decoder.
   */
  public String getName() {
    return new String(JmriPrefix + String.valueOf(DecAddr));
  }
  
  /**
   * retrieves the JMRI User Name.
   * 
   * @return the JMRI User Name fo rthe Chain.
   */
  public String getUserName() {
    return new String(UserName);
  }
  
  /**
   * saves the JMRI User Name.
   * @param name is the User defined name.
   */
  public void setUserName(String name) {
    UserName = name;
  }
  /**
   * adds an additional command to the Chain.
   * 
   * @param spec is the command being added
   */
  public void addSpec(IOInterface spec) {
    Specs.add(spec);
  }

  /**
   * Locks the decoder command while the route is locked through a turnout so that
   * no other decoder can move the points.
   */
  public void lockOutCommand() {
    for (Enumeration<IOInterface> iter = Specs.elements(); iter.hasMoreElements(); ) {
      iter.nextElement().lockOutCommand();      
    }
  }
  
  /**
   * Unlocks the decoder command when a route through a turnout has cleared.  The points
   * can again be moved.
   */
  public void unlockOutCommand() {
    for (Enumeration<IOInterface> iter = Specs.elements(); iter.hasMoreElements(); ) {
      iter.nextElement().unlockOutCommand();      
    }
  }
  
  /**
   * tests if the decoder command has been locked out.
   * @return true if the command should not be sent. False if it is safe to send it.
   */
  public boolean isLockedOut() {
    for (Enumeration<IOInterface> iter = Specs.elements(); iter.hasMoreElements(); ) {
      if (iter.nextElement().isLockedOut()) {
        return true;
      }
    }
    return false;
  }

  /**
   * registers the decoder with the LockedDecoder as a candidate for having the lock
   * treatment.
   */
  public void registerLock() {
    for (Enumeration<IOInterface> iter = Specs.elements(); iter.hasMoreElements(); ) {
      iter.nextElement().registerLock();      
    }
  }

  /**
   * replaces the Delay value.
   * 
   * @param d is the new delay in milliseconds.
   */
  public void setDelay(int d) {
      Delay = d;
  }
  
  /**
   * retrieves the Delay value.
   * 
   * @return the delay in milliseconds after the previous command
   *  in a chain.
   */
  public int getDelay() {
      return Delay;
  }
  
  /**
   * Start the timer that delays before sending the next command
   * 
   * @param delay is the amount of time to delay in milliseconds
   */
  protected void startDelay(int delay) {
    if (DelayTimer==null) {
      DelayTimer = new javax.swing.Timer(delay, new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          timeout();
        }
      });
      DelayTimer.setInitialDelay(delay);
      DelayTimer.setRepeats(false);
    }
    DelayTimer.start();
  }
 
  /**
   * is invoked when the timeout delay from the previous command
   * in the chain has expired.  This is the trigger to send the
   * next command in the chain.
   */
  private void timeout() {
    ExecutingCommand.runNext();
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
    return new String("An " + XML_TAG + " cannot have a text value (" +
        eleValue + ").");
  }
  
  /*
   * is the method through which the object receives embedded Objects.
   *
   * @param objName is the name of the embedded object
   * @param objValue is the value of the embedded object
   *
   * @return null if the Object is acceptible or an error String
   * if it is not.
   */
  public String setObject(String objName, Object objValue) {
    String result = null;
    if (IOSpec.XML_TAG.equals(objName)) {
      Specs.add((IOInterface) objValue);
    }
    else {
      result = new String("An " + XML_TAG + " cannot contain a " +
          objName + ".");
    }
    return result;
  }
  
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
    String resultMsg = null;
    IOSpecChainManager.instance().provideChain(JmriPrefix + String.valueOf(DecAddr));
    return resultMsg;
  }
  
  /**
   * registers an IOSpecChainFactory with the XMLReader.
   */
  static public void init() {
    XMLReader.registerFactory(XML_TAG, new IOSpecChainFactory());
  }


/**
 * is a Class known only to the IOSpecChain class that implements
 * the command design pattern.  This makes an object out of the request being
 * sent to the links in the chain.
 */
  private abstract class CommandBot {
    /**
     * is the parameter for the forceState command.
     */
    public boolean state;
    
    /**
     * is the index of the decoder that will be triggerd next.
     */
    public int nextDecoder;
    
    /**
     * is the specialization for each request.
     * 
     * @param decoder is the IOSpec of the decoder being triggered
     */
    public abstract void runCommand(IOInterface decoder);

    /**
     * walks through the Chain, inserting delays where needed.
     */
    public void runNext() {
      IOInterface decoder;
      while (nextDecoder < (Specs.size() - 1)) {
        decoder = Specs.elementAt(nextDecoder++);
        runCommand(decoder);
        if (decoder.getDelay() > 0) {
          startDelay(decoder.getDelay());
          return;
        }
      }
      if (nextDecoder >= 0) {
        runCommand(Specs.elementAt(nextDecoder));
      }
    }
  }
}
/**
 * is a Class known only to the IOSpecChain class for creating IOSpecChains from
 * an XML document.
 */
class IOSpecChainFactory
implements XMLEleFactory {
  
  /**
   * is the address to send the command to.
   */
  String Recipient;
  
  /**
   * is the User Name.
   */
  String UserName;

  /**
   * is the Delay field.
   */
  int Delay;

  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
    Recipient = null;
    UserName = null;
    Delay = 0;
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
    if (IOSpec.DECADDR.equals(tag)) {
      Recipient = value;
    }
    else if (IOSpec.DELAY.equals(tag)) {
      try {
          int addr = Integer.parseInt(value);
          Delay = addr;
        }
        catch (NumberFormatException nfe) {
          System.out.println("Illegal delay for " + IOSpec.XML_TAG
                             + "XML Element:" + value);
        }        
  }
    else if (IOSpec.USER_NAME.equals(tag)) {
      UserName = value;
    }
    else {
      resultMsg = new String("A " + IOSpecChain.XML_TAG +
          " XML Element cannot have a " + tag +
      " attribute.");
    }
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
    IOSpecChain spec = JmriPrefixManager.findChain(IOSpecChain.CHAIN_PREFIX, Recipient);
    spec.setUserName(UserName);
    spec.setDelay(Delay);
    return spec;
  }
}
/* @(#)IOSpecChain.java */