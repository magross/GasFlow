/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package ds.graph;

/**
 * This class represents a rectangle used to specify the region that is covered by a node.
 * The rectangle is given by four objects of the inner class <code>NodeRectanglePoint</code> that
 * tell the position of the four corners.
 */
public class NodeRectangle{
	
	/**
	 * This enclosed class of <code>NodeRectangle</code> represents a point by specifying its x- and y-coordinate.
	 */
	public class NodeRectanglePoint{
		
		/** The coordinates of the point. */
		private int x,y;
		
		/**
		 * Creates a new point with the coordinates <code>x</code> and <code>y</code>.
		 * @param x the x-coordinate of the new point.
		 * @param y the y-coordinate of the new point.
		 */
		public NodeRectanglePoint(int x, int y){
			this.x = x;
			this.y = y;
		}
		
		/**
		 * Returns the x-coordinate of this point.
		 * @return the x-coordinate of this point.
		 */
		public int getX(){
			return x;
		}
		
		/**
		 * Returns the y-coordinate of this point.
		 * @return the y-coordinate of this point.
		 */
		public int getY(){
			return y;
		}
	}
	
	/**
	 * The four corners of the rectangle, named with the directions north, south, west and east.
	 */
	private NodeRectanglePoint nw, ne, sw, se;
	
	/**
	 * Creates a new <code>NodeRectangle</code> with four corners given as <code>NodeRectanglePoint</code> objects.
	 * @param nw The north west corner of the rectangle.
	 * @param ne The north east corner of the rectangle.
	 * @param sw The south west corner of the rectangle.
	 * @param se The south east corner of the rectangle.
	 */
	public NodeRectangle(NodeRectanglePoint nw, NodeRectanglePoint ne, NodeRectanglePoint sw, NodeRectanglePoint se){
		this.nw = nw;
		this.ne = ne;
		this.sw = sw;
		this.se = se;
	}
	
	/**
	 * Creates a new <code>NodeRectangle</code> with four corners given by the coordinates of the corners.
	 * @param nw_x The x-coordinate of the north west corner of the rectangle.
	 * @param nw_y The y-coordinate of the north west corner of the rectangle.
	 * @param ne_x The x-coordinate of the north east corner of the rectangle.
	 * @param ne_y The y-coordinate of the north east corner of the rectangle.
	 * @param sw_x The x-coordinate of the south west corner of the rectangle. 
	 * @param sw_y The y-coordinate of the south west corner of the rectangle.
	 * @param se_x The x-coordinate of the south east corner of the rectangle.
	 * @param se_y The y-coordinate of the south east corner of the rectangle.
	 */
	public NodeRectangle(int nw_x, int nw_y, int ne_x, int ne_y, int sw_x, int sw_y, int se_x, int se_y){
		this.nw = new NodeRectanglePoint(nw_x, nw_y);
		this.ne = new NodeRectanglePoint(ne_x, ne_y); 
		this.sw = new NodeRectanglePoint(sw_x, sw_y);
		this.se = new NodeRectanglePoint(se_x, se_y);
	}
	
	/**
	 * Creates a new <code>NodeRectangle</code> given by the upper left and lower down corner specified by coordinates.
	 * @param nw_x The x-coordinate of the north west corner of the rectangle.
	 * @param nw_y The y-coordinate of the north west corner of the rectangle.
	 * @param se_x The x-coordinate of the south east corner of the rectangle.	 
	 * @param se_y The y-coordinate of the south east corner of the rectangle.	 
	 */
	public NodeRectangle(int nw_x, int nw_y, int se_x, int se_y){
		this(nw_x, nw_y, se_x, nw_y, nw_x, se_y, se_x, se_y);
	}
	
	/**
	 * Creates a new <code>NodeRectangle</code> given by the upper left and lower down corner specified by <code>NodeRectanglePoint</code> objects.
	 * @param nw The north west corner of the rectangle.
	 * @param se The south east corner of the rectangle.
	 */
	public NodeRectangle(NodeRectanglePoint nw, NodeRectanglePoint se){
		this(nw.getX(), nw.getY(), se.getX(), se.getY());
	}

	
	/**
	 * Returns the north west corner of the rectangle as <code>NodeRectanglePoint</code> object.
	 * @return the north west corner of the rectangle as <code>NodeRectanglePoint</code> object.
	 */
	public NodeRectanglePoint get_nw_point(){
		return nw;
	}
	
	/**
	 * Returns the north east corner of the rectangle as <code>NodeRectanglePoint</code>object.
	 * @return the north east corner of the rectangle as <code>NodeRectanglePoint</code>object.
	 */
	public NodeRectanglePoint get_ne_point(){
		return ne;
	}
	
	/**
	 * Returns the south west corner of the rectangle as <code>NodeRectanglePoint</code>object.
	 * @return the south west corner of the rectangle as <code>NodeRectanglePoint</code>object.
	 */
	public NodeRectanglePoint get_sw_point(){
		return sw;
	}
	
	/**
	 * Returns the south east corner of the rectangle as <code>NodeRectanglePoint</code>object.
	 * @return the south east corner of the rectangle as <code>NodeRectanglePoint</code>object.
	 */
	public NodeRectanglePoint get_se_point(){
		return se;
	}
	
	/**
	 * Returns a <code>String</code> containing the north west and the south east corner of the rectangle.
	 * @return a <code>String</code> containing the north west and the south east corner of the rectangle.
	 */
	@Override
	public String toString(){
		String result = "R[("+nw.getX()+","+nw.getY()+");("+se.getX()+","+se.getY()+")]";
		return result;
	}
	
	
}
