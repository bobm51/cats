/*
 * Name: Compression.java
 * 
 * What:
 *  Compression is a Singleton object with a boolean value.  It is set
 *  to true if CATS should try to squeeze the layout horizontally
 *  by shrinking columns containing only horizontal tracks.
 *   
 * Special Considerations:
 */
package cats.gui;

/**
 *  Compression is a Singleton object with a boolean value.  It is set
 *  to true if CATS should try to squeeze the layout horizontally
 *  by shrinking columns containing only horizontal tracks.
 * <p>Title: cats</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class Compression
extends BooleanGui {
  /**
   * is the tag for identifying a Compression Object in the XMl file.
   */
  static final String XMLTag = "COMPRESSIONTAG";
  
  /**
   * is the label on the JCheckBoxMenuItem
   */
  static final String Label = "Compress Screen";

  /**
   * is the singleton.
   */
  public static Compression TheCompressionType;

  /**
   * constructs the factory.
   */
  public Compression() {
    super(Label, XMLTag, true);
    TheCompressionType = this;
  }
}
/* @(#)Compression.java */