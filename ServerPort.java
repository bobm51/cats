/**
 * Name: ServerPort
 * 
 * What:
 *   This file contains a String that represents a TCP/IP port that the
 *   Train Status server will attach to.  If it is empty, the default port
 *   (1099) will be used.
 *   <p>
 *   The contents must be a valid port number (0-65536).
 *   
 * Special Considerations:
 */
package cats.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import cats.layout.xml.XMLEleFactory;
import cats.layout.xml.XMLEleObject;
import cats.layout.xml.XMLReader;
import cats.network.NetworkProtocol;

/**
 *   This file contains a String that represents a TCP/IP port that the
 *   Train Status server will attach to.  If it is empty, the default port
 *   (1234) will be used.
 *   <p>
 *   The contents must be a valid port number (0-65536).
 * 
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

public class ServerPort extends JTextField implements XMLEleObject {
    
    /**
     * is the tag for the port field in the XML file
     */
    private static final String XMLTag = "SERVERPORT";
    
    /**
     * is the return code for an invalid port String.
     */
    public static final int INVALID_PORT = -1;
    
    /**
     * is the initial value of the port String
     */
    private String InitialPort;
    
    /**
     * is the Singleton.  This is not needed for designer, but
     * is for CATS and I would like to keep the 2 source code
     * files as similar as I can.
     */
    private static ServerPort Singleton;
    
    /**
     * is the current value of the port, as an int
     */
    private int PortValue;
    
    /**
     * is the ctor
     */
    private ServerPort() {
        super("", 5);
        InitialPort = "";
        PortValue = NetworkProtocol.DEFAULT_PORT;
        setText(InitialPort);
        XMLReader.registerFactory(XMLTag, new ServerPortFactory());
        addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            int port = isValidPort(arg0.getActionCommand());
            if (port != INVALID_PORT) {
              NetworkManager.instance().newPortNumber(PortValue = port);
            }
          }});
        NetworkManager.instance().newPortNumber(PortValue);
    }

    /**
     * creates the Singleton ServerPort object.
     * 
     * @return the Singleton.  It is created if it does not exist.
     */
    public static ServerPort instance() {
        if (Singleton == null) {
            Singleton = new ServerPort();
        }
        return Singleton;
    }
    
    /**
     * overrides the default Document to provide control over formatting.
     */
    protected Document createDefaultModel() {
        return new PortDocument();
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
     * is called to get the value of the port number
     * @return the current port number, which may not be the
     * same as the String, because the user may not have completed
     * editing the String
     */
    public int getPortValue() {
      return PortValue;
    }
    
    /**
     * is the method through which the object receives the text field.
     *
     * @param eleValue is the Text for the Element's value.
     *
     * @return if the value is acceptable, then null; otherwise, an error
     * string.
     */
    public String setValue(String eleValue) {
      int port = isValidPort(eleValue);
        if (port != INVALID_PORT) {
            InitialPort = new String(eleValue);
            super.setText(new String(eleValue));
            NetworkManager.instance().newPortNumber(PortValue = port);
            return null;
        }
        return new String("invalid port string found in " + XMLTag);
    }

    /**
     * is the method through which the object receives embedded Objects.
     *
     * @param objName is the name of the embedded object
     * @param objValue is the value of the embedded object
     *
     * @return null if the Object is acceptible or an error String
     * if it is not.
     */
    public String setObject(String objName, Object objValue) {
        return new String("A " + XMLTag + " cannot have an embedded object.");
    }

    /**
     * returns the XML Element tag for the XMLEleObject.
     *
     * @return the name by which XMLReader knows the XMLEleObject (the
     * Element tag).
     */
    public String getTag() {
        return XMLTag;
    }

    /**
     * tells the XMLEleObject that no more setValue or setObject calls will
     * be made; thus, it can do any error chacking that it needs.
     *
     * @return null, if it has received everything it needs or an error
     * string if something isn't correct.
     */
    public String doneXML() {
        return null;
    }
    
    /**
     * is a Class known only to the ServerPort class for reading the value of the port from an XML
     * file
     */
    class ServerPortFactory
    implements XMLEleFactory {

        /*
         * tells the factory that an XMLEleObject is to be created.  Thus,
         * its contents can be set from the information in an XML Element
         * description.
         */
        public void newElement() {
        }

        /*
         * initializes a field in the created object.
         *
         * @param tag is the name of the attribute.
         * @param value is it value.
         *
         * @return null if the tag:value are accepted; otherwise, an error
         * string.
         */
        public String addAttribute(String tag, String value) {
            return new String("An " + XMLTag + " cannot have attributes.");
        }

        public XMLEleObject getObject() {
            return ServerPort.this;
        }
    }
    
    /**
     * Another inner class.  This ones extends the PlainDocument class to capture
     * input.  The string representing the port number must be all digits, and between
     * 0 and 65537.
     */
    static class PortDocument extends PlainDocument {
        public void insertString(int offs, String str, AttributeSet a) 
        throws BadLocationException {
            if (str == null) {
                return;
            }
            if ((getLength() + str.length()) > 5) {
                return;
            }
            char[] number = str.toCharArray();
            for (int i = 0; i < number.length; i++) {
                if (!Character.isDigit(number[i])) {
                    return;
                }
            }
            StringBuffer buf = new StringBuffer(getText(0, getLength()));
            
            buf.insert(offs, str);
            if (isValidPort(buf.toString()) == INVALID_PORT) {
                return;
            }
            super.insertString(offs, new String(number), a);
        }      
    }
}
/* @(#)ServerPort.java */