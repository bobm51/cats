/**
 * Name: StartTrainStat
 * 
 * What:
 *   This file contains a boolean for controlling if the networking
 *   for a TrainStat server should be launched When CATS starts up or
 *   not.  "true" means CATS should start the network server and
 *   false means it should not.
 *   
 * Special Considerations:
 *   The checkbox is in the "Network" menu pulldown, unlike most of the
 *   other booleans.
 */
package cats.gui;

import java.awt.event.ActionEvent;


/**
 *   This file contains a boolean for controlling if the networking
 *   for a TrainStat server should be launched When CATS starts up or
 *   not.  "true" means CATS should start the network server and
 *   false means it should not.
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

public class StartTrainStat extends BooleanGui {
    /**
     * is the tag for identifying a StartTrainStat Object in the XMl file.
     */
    static final String XMLTag = "TRAINSTATLABEL";
    
    /**
     * is the label on the JCheckBoxMenuItem
     */
    static final String Label = "Start TrainStat Server";
    
    /**
     * is the singleton.
     */
    private static StartTrainStat TrainStatActive;

    /**
     * constructs the factory.
     */
    private StartTrainStat() {
      super(Label, XMLTag, false);
      TrainStatActive = this;
      NetworkManager.instance().enableNetworking(getFlagValue());
    }
    
    /**
     * is the only public method for retrieving the StartTrainStat
     * object
     * @return the TrainStat server Singleton
     */
    static public StartTrainStat instance() {
      if (TrainStatActive == null) {
        TrainStatActive = new StartTrainStat();
      }
      return TrainStatActive;
    }
    
    /**
     * is the ActionListener for setting or clearing the flag.
     */
    public void actionPerformed(ActionEvent arg0) {
      NetworkManager.instance().enableNetworking(getState());
    }
    
    /**
     * tells the factory that an XMLEleObject is to be created.  Thus,
     * its contents can be set from the information in an XML Element
     * description.
     */
    public void newElement() { 
      super.newElement();
      NetworkManager.instance().enableNetworking(getFlagValue());
    }
}
/* @(#)StartTrainStat.java */