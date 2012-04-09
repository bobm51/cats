/*
 * Name: XMLEleFactory.java
 *
 * What:
 *   This file contains an interface for factories of XMLEleObjects.
 */
package cats.layout.xml;

/**
 * defines the interface that an XMLEleObject factory needs.  See
 * XMLReader for how this all works.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
 public interface XMLEleFactory {

   /**
    * tells the factory that an XMLEleObject is to be created.  Thus,
    * its contents can be set from the information in an XML Element
    * description.
    */
   public void newElement();

   /**
    * gives the factory an initialization value for the created XMLEleObject.
    *
    * @param tag is the name of the attribute.
    * @param value is it value.
    *
    * @return null if the tag:value are accepted; otherwise, an error
    * string.
    */
   public String addAttribute(String tag, String value);

   /**
    * tells the factory that the attributes have been seen; therefore,
    * return the XMLEleObject created.
    *
    * @return the newly created XMLEleObject or null (if there was a problem
    * in creating it).
    */
   public XMLEleObject getObject();
 }
