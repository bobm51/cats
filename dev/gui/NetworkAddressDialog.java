/**
 * Name: NetworkAddressDialog
 * 
 * What:
 * This class creates a JPanel for entering information on the network connection from
 * CATS to a JMRI instance running operations.
 *   
 * Special Considerations:
 */
package cats.gui;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cats.gui.jCustom.AcceptDialog;
import cats.network.OperationsClient;

/**
 * This class creates a JPanel for entering information on the network connection from
 * CATS to a JMRI instance running operations.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class NetworkAddressDialog extends JPanel{

	/**
	 * constants for this class
	 */
	/**
	 * the text tag for the name of the computer running Operations.
	 */
	private static String IP_HOSTNAME = "Operations Host Name:";
	
	/**
	 * the text tag for the IP address of the computer running Operations.
	 */
	private static String IP_ADDRESS = "Operations IP Address:";

	/**
	 * the text tag for the IP port address on the Operations computer.
	 */
	private static String OPERATIONS_PORT = "Operations port:";

	/**
	 * the text tag for the local IP port.
	 */
	private static String CATS_PORT = "Local Port:";

	/**
	 * the text tag for the connection box
	 */
	private static String CONNECTION = "Connect to Operations:";

	/**
	 * the text tag for the refresh box
	 */
	private static String REFRESH = "Refresh from Operations";
	
    /**
     * is the JTextField for the host name of the computer running
     * CATS.
     */
    JTextField HostName;
    
    /**
     * is the JTextField for IP address of the computer running CATS.
     */
    JTextField IPAddress;
    
    /**
     * is the JTextField for the telnet port on the computer running CATS.
     */
    JTextField OperationsPort;
    
    /**
     * is the JTextField for the telnet port on this computer.
     */
    JTextField LocalPort;
    
    /**
     * is the JCheckBox for the connection state
     */
    JCheckBox ConnectBox;

    /**
     * is the JCheckBox for refreshing trains from Operations
     */
    JCheckBox RefreshBox;
    
    /**
     * is the constructor.
     * 
     * @param addr contains the network information
     */
    private NetworkAddressDialog(OperationsClient addr) {
        JPanel name = new JPanel(new FlowLayout());
        JPanel address = new JPanel(new FlowLayout());
        JPanel rPort = new JPanel(new FlowLayout());
        JPanel lPort = new JPanel(new FlowLayout());
        String str;
        
        name.add(new JLabel(IP_HOSTNAME));
        str = addr.getHostName();
        if (str == null) {
            HostName = new JTextField(15);
        }
        else {
            HostName = new JTextField(str,
                    (str.length() < 15) ? 15 : str.length());
        }
        name.add(HostName);
        
        address.add(new JLabel(IP_ADDRESS));
        str = addr.getIPAddress();
        if (str == null) {
            IPAddress = new JTextField(15);
        }
        else {
            IPAddress = new JTextField(str, 15);
        }
        address.add(IPAddress);
        
        rPort.add(new JLabel(OPERATIONS_PORT));
        str = addr.getOperationsPort();
        if (str == null) {
            OperationsPort = new JTextField(5);
        }
        else
        {
            OperationsPort = new JTextField(str, 5);
        }
        rPort.add(OperationsPort);
    
        lPort.add(new JLabel(CATS_PORT));
        str = addr.getLocalPort();
        if (str == null) {
            LocalPort = new JTextField(5);
        }
        else {
            LocalPort = new JTextField(str, 5);
        }
        lPort.add(LocalPort);
 
        ConnectBox = new JCheckBox(CONNECTION);
        ConnectBox.setSelected(addr.getConnected());
        
        RefreshBox = new JCheckBox(REFRESH);
        RefreshBox.setSelected(false);
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(name);
        add(address);
        add(rPort);
        add(lPort);
        add(ConnectBox);
        if (addr.getConnected()) {
        	add(RefreshBox);
        }
    }
    
    /**
     * creates the JDialog, displays it, and tells the user what closing
     * button was pushed.
     *
     * @param addr contains the network address information.  It must not be null.
     *
     * @return true if the user pushed the Accept button or false if the
     * user pushed the Cancel button.
     */
    static public boolean select(OperationsClient addr, String title) {
        NetworkAddressDialog nad = new NetworkAddressDialog(addr);
        while (AcceptDialog.select(nad, title)) {
//            if (((nad.HostName.getText() == null) || nad.HostName.getText().trim().equals(""))
//                    && ((nad.IPAddress.getText() == null) || nad.IPAddress.getText().trim().equals(""))) {
//                JOptionPane.showMessageDialog(nad, I18N.getString(Constants.DEST_ERROR),
//                        "", JOptionPane.ERROR_MESSAGE);                
//            }
//            else if ((nad.IPAddress.getText() != null) && !nad.IPAddress.getText().trim().equals("") &&
             if ((nad.IPAddress.getText() != null) && !nad.IPAddress.getText().trim().equals("") &&
                    (OperationsClient.toInetAddress(nad.IPAddress.getText()) == null)) {
                JOptionPane.showMessageDialog(nad, "Improperly formatted IP address",
                        "", JOptionPane.ERROR_MESSAGE);
            }
            else if (OperationsClient.isValidOperationsPort(nad.OperationsPort.getText()) == OperationsClient.INVALID_PORT) {
                JOptionPane.showMessageDialog(nad, "Illegal IP port number on Operations computer",
                        "", JOptionPane.ERROR_MESSAGE);
            }
            else if (OperationsClient.isValidCATSPort(nad.LocalPort.getText()) == OperationsClient.INVALID_PORT) {
                JOptionPane.showMessageDialog(nad, "Illegal IP port number on CATS computer",
                        "", JOptionPane.ERROR_MESSAGE);                
            }
            else {
            	return addr.setAll(nad.HostName.getText(),
            			nad.IPAddress.getText(),
            			nad.OperationsPort.getText(),
            			nad.LocalPort.getText(),
            			nad.ConnectBox.isSelected(),
            			nad.RefreshBox.isSelected());
            }
        }
        return false;
    }    

}
/* @(#)NetworkAddressDialog.java */