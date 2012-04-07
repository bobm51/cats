/* What: XmlFileFilter.java
 *
 * What:
 *  This class creates a file name filter for selecting files that
 *  could be XML files.  A file whose suffix is .XML or .xml is considered to
 *  be a possible XML file.
 */
package cats.gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *  This class creates a file name filter for selecting files that
 *  could be XML files.  A file whose suffix is .XML or .xml is considered to
 *  be a possible XML file.
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

/**
 * is a filter for selecting certain file suffixes.
 */
public class XmlFileFilter extends FileFilter {
  private String Desc = null;
  private String Ext = null;

  /**
   * is the constructor.
   *
   * @param extension is the file extension being matched.
   * @param description is a text description of files with that
   * extension.
   */
  public XmlFileFilter(String extension, String description) {
    Desc = description;
    Ext = extension;
  }

  /**
   * retrieves the description information.
   *
   * @return the description of files with teh extension.
   */
  public String getDescription() {
    return new String(Desc);
  }

  /**
   * is the actual filter.
   *
   * @param f is the file name being tested.
   *
   * @return true if it passes the filter.
   */
  public boolean accept(File f) {
    if (f == null) {
      return false;
    }
    if (f.isDirectory()) {
        return true;
    }
    return f.getName().toLowerCase().endsWith(Ext);
  }
}

