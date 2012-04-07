/**
 * Name: ConnectionListener
 * 
 * What:
 *   This file contains an interface definition for objects that
 *   listen to concrete implementations of AbstractConnections.
 *   
 * Special Considerations:
 */
package cats.network;

/**
 *   This file contains an interface definition for objects that
 *   listen to concrete implementations of AbstractConnections.
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

public interface ConnectionListener {
 
    /**
     * is called when the AbstractConnection is dropped
     * @param connection being dropped.  It is needed so that a listener can
     * distinguish between multiple connections
     */
    public void connectionDropped(AbstractConnection connection);

}
/* @(#)AbstractConnection.java */