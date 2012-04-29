/*
 * Name: XMLEleObject.java
 *
 * What:
 *   This file contains an interface for accepting either text
 *   or embedded Elements from an XML document.
 */
package cats.layout.xml;

/**
 * is an interface for objects which receive the Text fields
 * or embedded Elements from the XML parser.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
 public interface XMLEleObject {

   /**
    * is the method through which the object receives the text field.
    *
    * @param eleValue is the Text for the Element's value.
    *
    * @return if the value is acceptable, then null; otherwise, an error
    * string.
    */
   public String setValue(String eleValue);

   /**
    * is the method through which the object receives embedded Objects.
    *
    * @param objName is the name of the embedded object
    * @param objValue is the value of the embedded object
    *
    * @return null if the Object is acceptible or an error String
    * if it is not.
    */
   public String setObject(String objName, Object objValue);

   /**
    * returns the XML Element tag for the XMLEleObject.
    *
    * @return the name by which XMLReader knows the XMLEleObject (the
    * Element tag).
    */
   public String getTag();

   /**
    * tells the XMLEleObject that no more setValue or setObject calls will
    * be made; thus, it can do any error chacking that it needs.
    *
    * @return null, if it has received everything it needs or an error
    * string if something isn't correct.
    */
   public String doneXML();
 }
