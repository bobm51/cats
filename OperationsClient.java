/* Name: OperationsClient.java
 *
 * What:
 *   This file defines the information needed for establishing a network 
 *   connection between the computer running the CATS and
 *   the computer running JMRI Operations.  They can be the same computer,
 *   in which case, the local host IP address can be used.
 *  
 *  Special Considerations:
 */
package cats.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import cats.jmri.OperationsTrains;
import cats.layout.xml.XMLEleFactory;
import cats.layout.xml.XMLEleObject;
import cats.layout.xml.XMLReader;

/**
 *   This file defines the information needed for establishing a network 
 *   connection between the computer running the CATS and
 *   the computer running JMRI Operations.  They can be the same computer,
 *   in which case, the local host IP address can be used.
 * 
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
*/
public class OperationsClient implements XMLEleObject, Cloneable,
ConnectionListener {
	/**
	 * is the tag for identifying an OperationsClient in the XMl file.
	 */
	static final String XML_TAG = "OPERATIONS";

	/**
	 * is the XML attribute tag on the hostname
	 */
	static final String HOST_TAG = "HOSTNAME";

	/**
	 * is the XML attribute tag on the IP address
	 */
	static final String IP_TAG = "IPADDRESS";

	/**
	 * is the XML attribute tag on the Remote port
	 */
	static final String REMOTE_TAG = "REMOTE_PORT";

	/**
	 * is the XML attribute tag on the Local port
	 */
	static final String LOCAL_TAG = "LOCAL_PORT";

	/**
	 * is the XML attribute tag on connect at start up
	 */
	static final String CONNECT_TAG = "CONNECT";

	/**
	 * the default port to use on the Operations computer.  The default is defined
	 * in jmri.jmris.JmriServer.java as "protected int portNo".  However, this does
	 * not match.  This was found through netstat.
	 * 
	 * Try: jmri.jmrix.jmriclient.networkdriver:NetworkDriverAdapter()
	 */
	private static final int REMOTE_PORT = 2048;

	/**
	 * the default port to use on this computer
	 */
	private static final int LOCAL_PORT = 51431;

	/**
	 * flags an invalid TCP/IP port number
	 */
    public static final int INVALID_PORT = -1;

    /**
     * is the default value for the CATS IP address - the local host.
     * If no host name has been specified, this is the IP address of last resort.
     */
    private final String LOCALHOST = "127.0.0.1";
    
    /**
     * is the Singleton that contains the Operations IP information
     */
	private static OperationsClient OperationsNetwork;

    /**
     * is the message handler that receives updates from Operations.  It must be static
     * because it's value carries over from one incarnation of this class to
     * the next.
     */
    private static OperationsConnection MyConnection;
        
	/**
	 * The internal data being configured
	 */
	/**
	 * the IP address.  The default value is blank, meaning, use the localhost.
	 */
	private String IPAddress = "";

	/**
	 * the hostname.  The default value is blank, meaning, use the localhost.
	 */
	private String Hostname = "";

	/**
	 * the port to use on the Operations computer.  The default is defined above.
	 */
	private String RemotePort = "";

	/**
	 * the port to use on this computer
	 */
	private String LocalPort = "";

	/**
	 * a boolean indicating the user's desire to connect to Operations or not.  This
	 * does not reflect the status of the actual connection.  That is contained
	 * in the value of MyConnection.
	 */
	private boolean Connected = false;

	/**
	 * the ctor
	 */
	public OperationsClient() {
		Connected = false;
	}
	
    /**
     * is the single point for accessing the Network Address.
     * 
     * @return the Network Address.
     */
    public static OperationsClient instance() {
        if (OperationsNetwork == null) {
            MyConnection = null;
            OperationsFactory factory;
            OperationsNetwork = new OperationsClient();
            factory = OperationsNetwork.new OperationsFactory();
            XMLReader.registerFactory(XML_TAG, factory);
        }
        return OperationsNetwork;
    }

    /**
     * retrieves the network name of the computer running Operations
     * @return the network name of the computer that TrainStat
     * will attempt to connect to.  It may be null.
     */
    public String getHostName() {
        if (Hostname != null) {
            return new String(Hostname);
        }
        return "";
    }

    /**
     * retrieves the IP address of the computer running Operations
     * @return the IP address of the computer running JMRI Operations.
     * Null may be returned.
     */
    public String getIPAddress() {
        if (IPAddress != null) {
            return new String(IPAddress);
        }
        return "";
    }

    /**
     * retrieves the port number on the Operations computer
     * @return the port number on the Operations computer that will be
     *  contacted for setting up a session.  It may be null.
     */
    public String getOperationsPort() {
        if (RemotePort != null) {
            return new String(RemotePort);
        }
        return "";
    }

    /**
     * retrieves the numeric value of the string containing
     * the port on the Operations computer.  If null, then
     * the default port.
     * @return
     */
    private int OperationsPort() {
        if ((RemotePort == null) || RemotePort.equals("")) {
            return REMOTE_PORT;
        }
        return Integer.parseInt(RemotePort);
    }

    /**
     * retrieves the port number on this computer
     * @return the port number on this computer that will be
     *  used for setting up an session.  It may be null.
     */
    public String getLocalPort() {
        if (LocalPort != null) {
            return new String(LocalPort);
        }
        return "";
    }

	/**
	 * is called to retrieve the current value of the Connected flag.  The flag controls
	 * is the connection should be established when CATS starts or not.
	 * @return the Connected flag.
	 */
	public boolean getConnected() {
		return Connected;
	}

    /**
     * converts an IP address in String format to one in
     * InetAddress format.  If the String is null, blank,
     * or not dotted hex, the local address is returned.  
     * @param address is the String form of the address
     * @return the InetAddress form of an IP address.
     */
    private InetAddress convertAddress(String address) {
        InetAddress a = null;
        if ((address == null) || (address.trim().equals("")) ||
                ((a = toInetAddress(address)) == null)) {
            address = new String();
            try {
                a = InetAddress.getByName(LOCALHOST);
            } catch (UnknownHostException e) {
                log.fatal("Ill-formed IP address");
                e.printStackTrace();
            }
        }
        return a;
    }
    
    /**
     * converts a String to an InetAddress.  If the String
     * is not formatted for IPv4, null is returned.
     * @param addr is a String representation of an IP address
     * @return the IP address of the String or null if it is invalid
     */
    public static InetAddress toInetAddress(String addr) {
        InetAddress ia = null;
        byte [] result = new byte[4];
        int partial;
        int octet = 0;
        StringTokenizer tokens = new StringTokenizer(addr, ".");
        while (tokens.hasMoreTokens()) {
            try {
                partial = Integer.parseInt(tokens.nextToken());
            }
            catch (NumberFormatException nfe) {
                return null;
            }
            if ((partial < 0) || (partial > 255)) {
                return null;
            }
            if (octet > 3) {
                return null;
            }
            result[octet++] = (byte) partial;
        }
        if (octet != 4) {
            return null;
        }
        try {
            ia = InetAddress.getByAddress(result);
        } catch (UnknownHostException e) {
            return null;
        }
        return ia;
    }

    /**
     * checks a string for being a valid port, using
     * isValidPort().  If the String is null or empty.
     * the default port is returned; otherwise,
     * the results of isValidPort are returned.
     * @param port is the String representation of an IP port
     * @return numeric representation of the port if the String
     * is valid or the default port if not.
     */
    public static int isValidCATSPort(String port) {
        if ((port == null) || (port.trim().equals(""))) {
            return LOCAL_PORT;
        }
        return isValidPort(port);
    }

    /**
     * checks a string for being a valid port, using
     * isValidPort().  If the String is null or empty.
     * the default port is returned; otherwise,
     * the results of isValidPort are returned.
     * @param port is the String representation of an IP port
     * @return numeric representation of the port if the String
     * is valid or the default port if not.
     */
    public static int isValidOperationsPort(String port) {
        if ((port == null) || (port.trim().equals(""))) {
            return REMOTE_PORT;
        }
        return isValidPort(port);
    }
    
    /**
     * checks a String for being a valid IP port.  To be
     * valid, the String must be all numeric characters,
     * greater than or equal to 0, and less than 65536.
     * @param port is the String being verified.
     * @return If the String is valid, then the port
     * number; otherwise, -1.
     */
    private static int isValidPort(String port) {
        int result;
        try {
            result = Integer.parseInt(port);
        }
        catch (NumberFormatException nfe) {
            return INVALID_PORT;
        }
        if ((result >=0) && (result < 65536)) {
            return result;
        }
        return INVALID_PORT;
    }
    
    /**
	 * is called to set all the local data.
	 * @param hostname is the new name of the Operations host.
	 * @param ipaddress is the new IP address of the Operations host.
	 * @param operationsPort is the port to use on the Operations computer.
	 * @param localPort is the port to use on the CATS computer.
	 * @param enabled is true to set up the connection when CATS starts up.
	 * @param refresh is true to force CATS to refresh the status of trains from
	 * 	Operations
	 * 
	 * @return true if any field changed; false if no field changed
	 */
	public boolean setAll(String hostname, String ipaddress, String operationsPort,
			String localPort, boolean enabled, boolean refresh) {
		boolean changed = false;
		hostname = hostname.trim();
		if (!Hostname.equals(hostname)) {
			changed = true;
			Hostname = hostname;
		}
		if (!IPAddress.equals(ipaddress) && 
				(ipaddress.equals("") || (toInetAddress(ipaddress) != null))) {
			changed = true;
			IPAddress = ipaddress;
		}
		if (!RemotePort.equals(operationsPort) && 
				(operationsPort.equals("") ||(isValidPort(operationsPort) != INVALID_PORT))) {
			changed = true;
			RemotePort = operationsPort;
		}
		if (!LocalPort.equals(localPort) && 
				(localPort.equals("") || (isValidPort(operationsPort) != INVALID_PORT))) {
			changed = true;
			LocalPort = localPort;
		}
		if (Connected != enabled) {
			changed = true;
			Connected = enabled;
		}
		if (refresh) {
			OperationsTrains.instance().refreshTrains();
		}
		return changed;
	}

    /**
     * this method attempts to make the real connection the
     * same as the administered connection.
     */
    public void reconcileConnection() {
        if (Connected) {
            enableNetwork();
        }
        else {
            disableNetwork();
        }
    }
    
    /**
     * if the network is not running, the parameters (address
     * and port) are passed to the network handler and it is
     * enabled.
     */
    private void enableNetwork() {
    	if (isEnabled()) {
    		disableNetwork();
    	}
    	if (getIPAddress() == null) {
    		establishConnection(getHostName(), OperationsPort());
    	}
    	else {
    		establishConnection(convertAddress(IPAddress), OperationsPort());
    	}
    }

    /**
     * if the network is running, it is shutdown.
     */
    private void disableNetwork() {
        if (isEnabled()) {
            MyConnection.interrupt();
            MyConnection = null;
            OperationsTrains.instance().setConnection(null);
        }
    }
    
    /**
     * establishes a connection to a CATS server with a particular
     * IP address.  The caller should be sure that a connection is not
     * up before calling this method.
     * @param address is the IP address
     * @param port is the port on the CATS server to connect to.
     */
    private void establishConnection(InetAddress address, int port) {
        Socket sock;
        try {
//            sock = new Socket(address, port, convertAddress(LOCALHOST), 
//                    statPort());
            sock = new Socket(address, port);
            MyConnection = new OperationsConnection(sock);
            MyConnection.addListener(this);
            OperationsTrains.instance().setConnection(MyConnection);
            OperationsTrains.instance().refreshTrains();
        }
        catch (IOException e) {
            MyConnection = null;
            log.warn("Failed to establich network connection to " +
                    address + ": " + e.getMessage());
        }
    }
    
    /**
     * establishes a connection to a CATS server with a particular
     * Internet name.  The caller should be sure that a connection is not
     * up before calling this method.
     * @param address is the name of the CATS server
     * @param port is the port on the CATS server to connect to.
     */
    private void establishConnection(String address, int port) {
        Socket sock;
        try {
//          sock = new Socket(address, port, convertAddress(LOCALHOST), 
//          statPort());
        	sock = new Socket(address, port);
            MyConnection = new OperationsConnection(sock);
            MyConnection.addListener(this);
            OperationsTrains.instance().setConnection(MyConnection);
            OperationsTrains.instance().refreshTrains();
        }
        catch (IOException e) {
            MyConnection = null;
            log.warn("Failed to establich netwrok connection to " +
                    address + ": " + e.getMessage());
        }
    }
 
    /**
     * is invoked to inquire about the status of the network
     * connection
     * @return true if the connection is up and false if it is not.
     */
    public boolean isEnabled() {
        return MyConnection != null;
    }
    
    /**
     * handles the call back when the CATS server drops the connection.
     * @param connection
     */
    public void connectionDropped(AbstractConnection connection) {
        MyConnection = null;
    }

    /**
     * sends a message to CATS.  If there is no connection, the message is dropped.
     * @param msg is the fully constructed message to send
     * @return true if the connection has been established; false if there is no connection.
     */
    public boolean transmitMessage(String msg) {
        if (isEnabled()) {
            MyConnection.sendMessage(msg);
        }
        return false;
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
	 *
	 * @param objName is the name of the embedded object
	 * @param objValue is the value of the embedded object
	 *
	 * @return null if the Object is acceptable or an error String
	 * if it is not.
	 */
	public String setObject(String objName, Object objValue) {
		return new String("A " + XML_TAG + " cannot contain an Element ("
				+ objName + ").");
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
		return null;
	}

/**
 * is a Class known only to the OperationsClient class for creating an OperationsClient
 * from an XML document.
 */
class OperationsFactory
implements XMLEleFactory {

	/**
	 * the fields in OperationsClient
	 */
	private String Hostname;
	private String IPAddress;
	private String RemotePort;
	private String LocalPort;
	private boolean Connected;

	/*
	 * tells the factory that an XMLEleObject is to be created.  Thus,
	 * its contents can be set from the information in an XML Element
	 * description.
	 */
	public void newElement() {
		Hostname = OperationsNetwork.getHostName();
		IPAddress = OperationsNetwork.getIPAddress();
		RemotePort = OperationsNetwork.getOperationsPort();
		LocalPort = OperationsNetwork.getLocalPort();
		Connected = OperationsNetwork.getConnected();
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
		if (tag.equals(HOST_TAG)) {
			Hostname = value;
		}
		else if (tag.equals(IP_TAG)) {
			IPAddress = value;
		}
		else if (tag.equals(REMOTE_TAG)) {
			if (isValidOperationsPort(value) != INVALID_PORT) {
				RemotePort = value;
			}
		}
		else if (tag.equals(LOCAL_TAG)) {
			if (isValidCATSPort(value) != INVALID_PORT) {
				LocalPort = value;
			}
		}
		else if (tag.equals(CONNECT_TAG)) {
			Connected = true;
		}
		else {
			resultMsg = new String("A " + XML_TAG +
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
		if (OperationsNetwork.setAll(Hostname, IPAddress, RemotePort, LocalPort,
				Connected, true)) {
			OperationsNetwork.reconcileConnection();
		}
		return OperationsNetwork;
	}
}

static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(OperationsClient.class.getName());

}
/* @(#)Constants.java */