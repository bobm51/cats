/*
 * Name: XMLReader.java
 *
 * What:
 *   This is a class for reading in the XML file that describes the layout.
 */

package cats.layout.xml;

//import cats.gui.Ctc;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.util.*;
import java.io.*;

/**
 * reads an XML description from a file, parses it, and generates the Nodes
 * in the Layout from the description.  It uses SAX as the parser skeleton.
 * The way the SAX parser works on a well-formed XML document is it calls
 * <ol>
 *    <li> startDocument (the <?xml ... ?> tag)
 *    <li> startElement (the <element> tag, including any attributes)
 *    <li> possibly one character (the text of the element body)
 *    <li> possibly one or more startElement (embedded Objects)
 *    <li> endElement </element>
 *    <li> endDocument
 * </ol>
 *
 * Each element kind is associated with a factory object so that
 * XMLReader calls the factory when a startElement is encountered, so
 * that it can create an object of the appropriate type.  It then calls
 * a method in the factory for each attribute it finds and calls another
 * method when it runs out of attributes.  This last method returns
 * the Object created.  Furthermore, the current Object is pushed on
 * an internal stack and the newly created Object becomes the current
 * Object.
 *
 * The character call passes the text field to the current object.  When
 * endElement is found, a method is called on the current object to let
 * it know that no more text fields or embedded objects will be added.
 * Then, the stack is popped and the current Object is passed to the
 * popped Object.
 *
 * Ctc handles 1 and 6.  Factories implement methods for 2 (and 4).
 * Objects implement 3 and 5.  5 is handled in two ways:
 * <ul>
 *    <li> the current Object is informed that no more calls will be made
 *    <li> the stacked Object is given the current Object
 * </ul>
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class XMLReader
    extends DefaultHandler {

  /**
   * spaces to indent each line by
   */
  static String ErrorMsg;
  static private Locator MyLocator; // A helper for locating where in a document
                                    // callbacks are happening.
  static private int LastLine = 0;  // last line located in document.
  private Stack<XMLEleObject> EleStack = new Stack<XMLEleObject>(); // Stack of Elements being constructed
  private XMLEleObject Rcvr; // The current Element being constructed
  private static Hashtable<String, XMLEleFactory> Factories; // The collection of Element factories
  private String TextVal = new String(); // The text value being constructed.

  /**
   * creates the parser and gets it rolling.
   *
   * @param file is the file containing the XML description.
   *
   * @return null if the file was parsed successfully; otherwise, return
   *         a String describing the error.
   */
  public static String parseDocument(File file) {
    XMLReader SAXEventHandler = new XMLReader();
    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
    org.xml.sax.XMLReader xmlReader = null;

    try {
      SAXParser saxParser = parserFactory.newSAXParser();
      xmlReader = saxParser.getXMLReader();
    }
    catch (Exception e) {
      return e.getMessage();
    }

    xmlReader.setContentHandler(SAXEventHandler);
    xmlReader.setErrorHandler(SAXEventHandler);

    try {
      String path = file.getAbsolutePath();
      xmlReader.parse("file:" + path);
    }
    catch (SAXException saxE) {
      ErrorMsg = new String("At line " + LastLine + ": " + saxE.getMessage());
    }
    catch (IOException ioE) {
      ErrorMsg = new String("At line " + LastLine + ": " + ioE.getMessage());
    }

    return ErrorMsg;
  }

  /**
   * Provides a reference to <code>Locator</code>, which provides
   * information about where in a document callbacks occur.
   * <p>
   * @param locator <code>Locator</code> object is tied to callback
   * process.
   */
  public void setDocumentLocator(Locator locator) {
    MyLocator = locator;
  }

  public void startDocument() {

  }

  public void startElement(String filename, String localName,
                           String qualifiedName, Attributes attributes) {
    String resultCode = null;
    String str;
    if (MyLocator != null) {
      LastLine = MyLocator.getLineNumber();
    }
    if (Factories.containsKey(qualifiedName)) {
      XMLEleFactory fac = Factories.get(qualifiedName);

      // Tell the XMLEleFactory that a new XMLEleObject is being parsed.
      fac.newElement();
      resultCode = sendValue();

      // Hand the XMLEleFactory the attributes of the new XMLEleObject
      if (attributes != null) {
        int numberAttributes = attributes.getLength();
        for (int loopindex = 0; loopindex < numberAttributes; ++loopindex) {
          str = fac.addAttribute(attributes.getQName(loopindex),
                                 attributes.getValue(loopindex));
          if (str != null) {
            resultCode = str;
          }
        }
      }
      EleStack.push(Rcvr);
      Rcvr = fac.getObject();
    }
    else {
      resultCode = new String("Unknown XML Element name : " + qualifiedName);
    }
    if (resultCode != null) {
      System.out.println("Line " + LastLine + ": " + resultCode);
    }
  }

  public void characters(char textChars[], int textStart, int textLength) {
    if (MyLocator != null) {
      LastLine = MyLocator.getLineNumber();
    }
    TextVal = TextVal.concat(new String(textChars, textStart, textLength));
  }

  public void endElement(String filename, String localName,
                         String qualifiedName) {
    String str;
    String resultMsg = sendValue();
    XMLEleObject oldRcvr = EleStack.pop();
    str = Rcvr.doneXML();
    if (str != null) {
    	resultMsg = str;
    }
    if (MyLocator != null) {
      LastLine = MyLocator.getLineNumber();
    }
    if ((oldRcvr != null) && (Rcvr != null)){
      str = oldRcvr.setObject(Rcvr.getTag(), Rcvr);
      if (str != null) {
        resultMsg = str;
      }
    }
    Rcvr = oldRcvr;
    if (resultMsg != null) {
      System.out.println("Line " + LastLine + ": " + resultMsg);
    }
  }

  public void endDocument() {
    if (ErrorMsg == null) {
//      Ctc.RootCTC.getLayout().showMe();
    }
  }

  private String sendValue() {
	  String result = null;
	  TextVal = TextVal.trim();
	  if (TextVal.length() > 0) {
		  result = Rcvr.setValue(TextVal);
		  TextVal = new String();
	  }	
	  return result;
  }
  
  public void warning(SAXParseException e) {
    ErrorMsg = new String("SAX error at line " + e.getLineNumber() + ": "
                          + e.getMessage());
  }

  public void error(SAXParseException e) {
    ErrorMsg = new String("SAX error at line " + e.getLineNumber() + ": "
                          + e.getMessage());
  }

  public void fatalError(SAXParseException e) {
    ErrorMsg = new String("SAX error at line " + e.getLineNumber() + ": "
                          + e.getMessage());
  }

  /**
   * adds a factory and its XML Element tag to the factory collection.
   *
   * @param tag is the Element's tag (name) in the XML file
   * @param factory is an object that knows how to construct Objects for
   *        the tag.
   *
   * @see XMLEleFactory
   */
  public static void registerFactory(String tag, XMLEleFactory factory) {
    if (Factories == null) {
      Factories = new Hashtable<String, XMLEleFactory>();
    }
    if (Factories.containsKey(tag)) {
      System.out.println("Line " + LastLine +": XML Element name " + tag
                         + " is already defined.");
    }
    else {
      Factories.put(tag, factory);
    }
  }
}
