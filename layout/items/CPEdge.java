/* Name: CPEdge.java
 *
 * What:
 *   This class is the container for all the information about one
 *   of a Section's Edges which "is a" Control Point boundary.
 */

package cats.layout.items;

import cats.gui.GridTile;
import cats.gui.CTCcanvas;
import cats.gui.Screen;
import cats.gui.jCustom.JListDialog;
import cats.rr_events.RREventManager;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JOptionPane;

/**
 * is a Control Point boundary. A Control Point boundary is a Block boundary
 * with a visible signal. <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's Crandic model
 * railroad. <p>Copyright: Copyright (c) 2004, 2009, 2010</p> <p>Company: </p>
 *
 * @author Rodney Black
 * @version $Revision$
 */
public class CPEdge
        extends BlkEdge {

    /**
   * the Labels for the Menu Items.  There are two for each item.  The
   * first is to add the item to the Signal's state and the second is
   * to remove it from the Signal's state.
     */
    private static final String[][] MenuItems = {
        {
            "Allow Traffic", "Block Traffic"}, {
            "Turn on Fleeting", "Turn off Fleeting"}, {
            "Entrance Signal", null}, {
            "Add Stack Command", null}
    };

    /**
     * a placeholder for the Signal associated with a Block boundary.
     */
    private SecSignal MySignal;

    /**
     * is true if a train has been lined into the Block through the Edge.
     */
    private boolean Traffic;

    /**
     * is true if the Signal has been set to allow fleeting.
     */
    private boolean Fleet;
    /**
     * is true if the Signal has been cleared.
     */
    private boolean NX;
    MouseEvent me1 = null;
    MouseEvent me2 = null;
    MouseEvent me3 = null;

    /**
     * constructs a CPEdge with only its Edge identifier.
     *
     * @param edge identifies the side of the GridTile the SecEdge is on.
   * @param shared describes the shared edge, if it is not adjacent.
   *    A null value means it is adjacent.
   * @param block is the Block definition.  If it is null, then the
   *   a default block is created, which may be overwritten during the
   *   bind process.
   * @param blkSignal is a Signal.  It may not be null.
     */
    public CPEdge(int edge, Edge shared, Block block, SecSignal blkSignal) {
        super(edge, shared, block, blkSignal);
        MySignal = blkSignal;
        if (MySignal.getSigIcon() != null) {
            MySignal.getSigIcon().protectEdge(this);
        }
        strategyFactory();
    }

    /**
     * is used to determine if a SecEdge is a Home Signal.
     *
     * @return true if it is and false if it is not.
     */
    public boolean isHomeSignal() {
    return super.isHomeSignal() || MySignal.isHS() ||
        (getBlock().getDiscipline() == Block.DTC);
    }

    /**
     * registers the SecEdge's icon on the painting surface.
     *
     * @param tile is where the Frills are painted.
     *
     * @see cats.gui.GridTile
     */
    public void install(GridTile tile) {
        EdgeTile = tile;
        if (EdgeTile != null) {
            MySignal.install(EdgeTile);
            EdgeTile.setSide(MyEdge, true);
        }
    }

    /**
   * tells the CPEdge that it does not appear on the painting surface.
   * For a CPEdge, this means that the Signal Icon can be removed.
     */
    public void hidden() {
        if (MySignal.getSigIcon() != null) {
            MySignal.setSigIcon(null);
        }
    }

    /**
     * handles a mouse button push when positioned over a Signal Icon.
     *
     * If the left mouse button is pushed, then it is interpreted as a request
   * to set traffic through the Block, beginning at this CPEdge.  If the
   * right button is pushed, then a menu is presented.
     *
     * @param me is the MouseEvent recording the button push/release.
     *
     * @see java.awt.event.MouseEvent
     */
    public void edgeMouse(MouseEvent me) {
        int mods = me.getModifiers();
    if ( (mods & MouseEvent.BUTTON3_MASK) != 0) {
            edgeMenu(me.getPoint());
    }
    else if ( (mods & MouseEvent.BUTTON1_MASK) != 0) {
            if (Reservation == ENTER_RESERVATION) {
                handleFleet(false);
      }
      else if (Reservation == NO_RESERVATION) {
                if (!setupReservation()) {
          JOptionPane.showMessageDialog( (Component)null,
                            "The reservation could not be made because of conflicts.",
                            "Route Reservation Error",
              JOptionPane.ERROR_MESSAGE
              );
                }
            }
        }
    }

    /**
     * presents the Menu of operations on the Signal.
     *
     * @param location is the location of the Mouse on the screen.
     *
     * @see cats.gui.jCustom.JListDialog
     */
    private void edgeMenu(Point location) {
        String[] menu = new String[4];
        if (Traffic) {
            menu[0] = new String(MenuItems[0][1]);
        } else {
            menu[0] = new String(MenuItems[0][0]);
        }
        if (Fleet) {
            menu[1] = new String(MenuItems[1][1]);
    }
    else {
            menu[1] = new String(MenuItems[1][0]);
        }

        //menu[2] = new String(MenuItems[2][0]);

        menu[3] = new String(MenuItems[3][0]);

        switch (JListDialog.select(menu, "Traffic Operation", location)) {
            case 0: // allow/disallow Traffic
                Traffic = !Traffic;
                break;

            case 1:
                handleFleet(!Fleet);
                break;

            case 2:
                System.out.println("Setting up NX");
                break;

            case 3:
                addStack();
                break;

            default:
        }
    }

    /*
     * enables or disables Fleeting through the Block from this BlkEdge.
     *
   * @param fleet is true if Fleeting should be enabled and false if
   * it should be disabled.
     */
    protected void setFleet(boolean fleet) {
        super.setFleet(fleet);
        Fleet = fleet;
    }

    /**
   * sets the Fleet flag and forwards it through all Blocks, up to
   * the next Control Point.
     *
   * @param fleet is true if fleeting is enabled and false if it is
   * to be disabled.
     */
    private void handleFleet(boolean fleet) {
        BlkEdge nextBlk;
        SecEdge egress = null;

        if (fleet) {
            setupReservation();
        } else {
            removeReservation();
        }
        for (nextBlk = this; nextBlk != null;) {
            // This will iterate through all blocks in which requestRoute
            // set (or cleared) a reservation.
            if (!fleet || (nextBlk.Reservation == ENTER_RESERVATION)) {
                nextBlk.setFleet(fleet);
                egress = nextBlk.findEgress();
                if ((egress != null) && egress.isBlock()) {
                    nextBlk = (BlkEdge) egress.getNeighbor();
                    if ((nextBlk == null) || nextBlk.isControlPoint()) {
                        nextBlk = null;
                    }
                } else {
                    nextBlk = null;
                }
            } else {
                nextBlk = null;
            }
        }
    }

    /**
     * addStack()
     *
     */
    private void addStack() {
        int mods = 16;
        OSEdge os;
        Component lcanvas = new CTCcanvas();
        Rectangle lRect;
        //lcanvas.addMouseListener(new lListener());
        
        //private class lListener {
            
        //}

        if (!hasClearRoute()) {
            SecEdge egress = findEgress(true); // get the section where the switchpoints are for the mouse event
            os = (OSEdge) egress;
            lRect = os.MySection.getTile().getSize();
            me1 = new MouseEvent(lcanvas, 501, System.currentTimeMillis(), mods, lRect.x + lRect.width / 2,
                    lRect.y + lRect.height / 2, 1, false, 1);

            me2 = new MouseEvent(lcanvas, 502, System.currentTimeMillis(), mods, lRect.x + lRect.width / 2,
                    lRect.y + lRect.height / 2, 1, false, 1);
        }
        
        Thread t = new Thread(new StackThread());
        t.setName("Stack Thread");
        t.start();
    }
    
    private class StackThread implements Runnable {

        public void run() {
;
            try {
                while (!setupStack(me1, me2)) {                   
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                return;
            }
            GridTile.doUpdates();
        }
     }
           

    /**
     * setupStack If the points were not aligned then the mouse events exist, in
     * which case the points are thrown. The route is then reserved. If the
     * route cannot be reserved then the points are returned to their original
     * position.
     *
     * @param me1 Created mouse down event
     * @param me2 Created mouse release event
     * @return
     */
           private boolean setupStack(MouseEvent me1, MouseEvent me2) {
            if ((me1 != null) && (me2 != null)) {
                Screen.DispatcherPanel.mousePressedAction(me1);
                Screen.DispatcherPanel.mouseReleasedAction(me2);
            }


            if (!setupReservation()) {
                if ((me1 != null) && (me2 != null)) {

                    Screen.DispatcherPanel.mousePressedAction(me1);
                    Screen.DispatcherPanel.mouseReleasedAction(me2);
                }
                return false;
            }
            return true;           
        }
           
    static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
            CPEdge.class.getName());
}
/* @(#)CPEdge.java */