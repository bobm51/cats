/*
 * Name: Itemizable.java
 *
 * What:
 *   This file contains an interface required by all sub-components of
 *   a Section.
 */
package cats.layout.items;

import cats.gui.GridTile;
import cats.layout.xml.XMLEleObject;

/**
 * defines the interface for Objects that are sub-components of a Section.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
 public interface Itemizable extends XMLEleObject {

   /**
    * tells the sub-component where its Section is, so that the sub-component
    * can register itself and retrieve anything else it needs from the Section.
    *
    * @param sec is the Section containing the sub-component.
    *
    * @see Section
    */
   public void addSelf(Section sec);

   /**
    * asks the sub-component to install itself on the painting surface.
    *
    * @param tile is the GridTile to install a frill on.
    *
    * @see cats.gui.GridTile
    */
   public void install(GridTile tile);

   /**
    * asks the sub-component if it has anything to paint on the Screen.
    *
    * @return true if it does and false if it doen't.
    */
   public boolean isVisible();
 }
