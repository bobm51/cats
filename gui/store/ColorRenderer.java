/* Name: ColorRenderer.java
 *
 * What:
 *  This file contains the class definition for a TableCellRenderer object
 *  for rendering Colors.  It is taken mostly intact from the Sun Swing
 *  trail for TableDialogEditDemo.
 */
package cats.gui.store;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

/**
 *  This file contains the class definition for a TableCellRenderer object
 *  for rendering Colors.  It is taken mostly intact from the Sun Swing
 *  trail for TableDialogEditDemo.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A model railroad dispatching program</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * @author Rodney Black
 * @version $Revision$
 */
public class ColorRenderer extends JLabel
implements TableCellRenderer {
    Border unselectedBorder = null;
    Border selectedBorder = null;
    boolean isBordered = true;

    /**
     * the ctor
     * @param isBordered is true if the cell has a border
     */
    public ColorRenderer(boolean isBordered) {
        this.isBordered = isBordered;
        setOpaque(true); //MUST do this for background to show up.
    }
    
    public Component getTableCellRendererComponent(
            JTable table, Object color,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
        Color newColor = (Color)color;
        setBackground(newColor);
        if (isBordered) {
            if (isSelected) {
                if (selectedBorder == null) {
                    selectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
                            table.getSelectionBackground());
                }
                setBorder(selectedBorder);
            } else {
                if (unselectedBorder == null) {
                    unselectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
                            table.getBackground());
                }
                setBorder(unselectedBorder);
            }
        }
        
        setToolTipText("RGB value: " + newColor.getRed() + ", "
                + newColor.getGreen() + ", "
                + newColor.getBlue());
        return this;
    }
}
/* @(#)ColorRenderer.java */