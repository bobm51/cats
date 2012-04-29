/* Name: NetworkStatus.java
 *
 * What:
 *   This class defines a Singleton JPanel that contains status information
 *   on the network.  It is designed this way so that the information can be
 *   updated whenever the menu is activated.
 *
 * Special Considerations:
 */
package cats.gui;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cats.network.TrainStatServer;

/**
 *   This class defines a Singleton JPanel that contains status information
 *   on the network.  It is designed this way so that the information can be
 *   updated whenever the menu is activated.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class NetworkStatus extends JPanel {
  
  /**
   * is the Singleton
   */
  private static NetworkStatus Singleton;

  /**
   * is the JLabel containing the host computer IP address
   */
  private JLabel IPAddress = new JLabel();
  
  /**
   * is the JLabel containing the host computer name
   */
  private JLabel HostName = new JLabel();
  
  /**
   * is the JLabel containing the status of the TrainStat server
   */
  private JLabel ConnectionStatus = new JLabel();
  
  /**
   * is a count of the number of known clients using the
   * TrainStat application
   */
  private JLabel ClientCount = new JLabel();
  
 
  /**
   * is the ctor
   *
   */
  public NetworkStatus() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    add(IPAddress);
    add(HostName);
    add(ConnectionStatus);
    add(ClientCount);
  }
  
  /**
   * is the accessor for the Singleton.  It is created,
   * if it does not exist.
   * @return the Singleton
   */
  public static NetworkStatus instance() {
    if (Singleton == null) {
      Singleton = new NetworkStatus();
    }
    return Singleton;
  }
  
  /**
   * updates the JLabels to the latest status
   */
  public void update() {
    InetAddress hostAddress;
    try {
      if ((hostAddress = InetAddress.getLocalHost())!= null) {
        IPAddress.setText("CATS IP address: " + hostAddress.getHostAddress());
        HostName.setText("CATS host name: " + hostAddress.getHostName());
      }
    } catch (UnknownHostException e1) {
      IPAddress.setText("CATS IP address: unknown");
      HostName.setText("CATS host name: unknown");
    }
    ClientCount.setText(TrainStatServer.instance().getClientCount() +
    " Status clients");
    if (TrainStatServer.instance().isConnected()) {
      ConnectionStatus.setText("Connection Up");
    }
    else {
      ConnectionStatus.setText("Connection Down");
    }
  }
}
/* @(#)NetworkStatus.java */