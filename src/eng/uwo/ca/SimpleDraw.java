package eng.uwo.ca;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

import javax.swing.*;

class SimpleDraw extends JFrame implements ActionListener, MouseListener, MouseMotionListener {
	// (x1,y1) = coordinate of mouse pressed
	// (x2,y2) = coordinate of mouse released
	int x1;
	int y1;
	int x2;
	int y2;

	// polygon global variables
	boolean polygon_first = true;
	ArrayList<Integer> polygon_xPoints = new ArrayList<Integer>();
	ArrayList<Integer> polygon_yPoints = new ArrayList<Integer>();
	ArrayList<Shape> temp_Lines = new ArrayList<Shape>(); 

	ArrayList<Shape> shapes = new ArrayList<Shape>();
	Shape prev = null;
	String shapeType = "Rectangle";
	
	public static int[] convertIntegers(List<Integer> integers)
	{
	    int[] ret = new int[integers.size()];
	    for (int i=0; i < ret.length; i++)
	    {
	        ret[i] = integers.get(i).intValue();
	    }
	    return ret;
	}
	

	public SimpleDraw() {
		this.setTitle("Simple DRAW");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// add check box group
		ButtonGroup cbg = new ButtonGroup();
		JRadioButton freehandButton = new JRadioButton("FreeHand");
		JRadioButton lineButton = new JRadioButton("Line");
		JRadioButton ovalButton = new JRadioButton("Oval");
		JRadioButton circleButton = new JRadioButton("Circle");
		JRadioButton rectangleButton = new JRadioButton("Rectangle");
		JRadioButton squareButton = new JRadioButton("Square");
		JRadioButton openPolygonButton = new JRadioButton("OpenPolygon");
		JRadioButton closedPolygonButton = new JRadioButton("ClosedPolygon");

		cbg.add(freehandButton);
		cbg.add(lineButton);
		cbg.add(ovalButton);
		cbg.add(circleButton);
		cbg.add(rectangleButton);
		cbg.add(squareButton);
		cbg.add(openPolygonButton);
		cbg.add(closedPolygonButton);

		freehandButton.addActionListener(this);
		lineButton.addActionListener(this);
		ovalButton.addActionListener(this);
		circleButton.addActionListener(this);
		rectangleButton.addActionListener(this);
		squareButton.addActionListener(this);
		openPolygonButton.addActionListener(this);
		closedPolygonButton.addActionListener(this);

		rectangleButton.setSelected(true);

		JPanel radioPanel = new JPanel(new FlowLayout());

		radioPanel.add(freehandButton);
		radioPanel.add(lineButton);
		radioPanel.add(ovalButton);
		radioPanel.add(circleButton);
		radioPanel.add(rectangleButton);
		radioPanel.add(squareButton);
		radioPanel.add(openPolygonButton);
		radioPanel.add(closedPolygonButton);
		this.addMouseListener(this);

		this.addMouseMotionListener(this);

		this.setLayout(new BorderLayout());
		this.add(radioPanel, BorderLayout.NORTH);
	}

	public void paint(Graphics g) {
		paintComponents(g);
		for (Shape shape : shapes) {
			Graphics2D g2 = (Graphics2D) g;
			g2.draw(shape);
		}

		if (prev != null) {
			Graphics2D g2 = (Graphics2D) g;
			g2.draw(prev);
		}

		/*
		if (!temp_Lines.isEmpty()){
			for (Shape shape : shapes) {
				Graphics2D g2 = (Graphics2D) g;
				g2.draw(shape);
			}
		}
		*/

	}

	public void actionPerformed(ActionEvent ae) {
		shapeType = ae.getActionCommand().toString();
	}

	public void mouseClicked(MouseEvent me) {
	}

	public void mouseEntered(MouseEvent me) {

	}

	public void mouseExited(MouseEvent me) {
	}

	public void mousePressed(MouseEvent me) {
		//variables
		Shape shape = null;
		
		// if left mouse button
		if (SwingUtilities.isLeftMouseButton(me)) {
			//System.out.println("LEFT CLICK!!!"); 
			
			// closed Polygon
			if (shapeType.equals("ClosedPolygon")) {
				System.out.println("Closed Polygon"); 
				// if first time
				if (polygon_first) {
					//System.out.println("FIRST TIME"); 
					x1 = me.getX();
					y1 = me.getY();

					polygon_xPoints.add(x1);
					polygon_yPoints.add(y1);

					polygon_first = false;
				}
				// else if not first time
				else {
					//System.out.println("Not first time"); 
					x2 = me.getX();
					y2 = me.getY();

					polygon_xPoints.add(x2);
					polygon_yPoints.add(y2);
					
					shape = new Line2D.Double(x1, y1, x2, y2); 
					x1 = x2; 
					y1 = y2; 
				}
			}
			// for any other shape
			else {
				x1 = me.getX();
				y1 = me.getY();
			}
			
			
		}

		// if right mouse button
		if (SwingUtilities.isRightMouseButton(me)) {

			// closed Polygon
			if (shapeType.equals("ClosedPolygon")) {

				//if not just initial x, y
				if (polygon_xPoints.size() > 1){
					int[] temp_xPoints = new int[polygon_xPoints.size()]; 
					int[] temp_yPoints = new int[polygon_yPoints.size()];
					
					temp_xPoints = convertIntegers(polygon_xPoints); 
					temp_yPoints = convertIntegers(polygon_yPoints); 
					
					shape = new Polygon(temp_xPoints, temp_yPoints, polygon_xPoints.size()); 
					
					polygon_first = true; 
				}
				//else clear the arraylist
				else{
					polygon_xPoints.clear();
					polygon_yPoints.clear(); 
					
					polygon_first = true; 
					this.prev = null; 
					this.repaint();
				}
				
			}

		}
		if (shape != null) {
			this.shapes.add(shape);
			this.repaint();
		}

	}

	public void mouseDragged(MouseEvent me) {

		x2 = me.getX();
		y2 = me.getY();

		int x1_old = x1;

		Shape shape = null;
		if (SwingUtilities.isLeftMouseButton(me)) {

			if (shapeType.equals("Rectangle")) {
				// a Rectangle cannot have a zero width or height
				if (x1 < x2 && y1 < y2) {
					shape = new Rectangle(x1, y1, Math.abs(x2 - x1), Math.abs(y2 - y1));
				} else if (x1 < x2 && y1 > y2) {
					shape = new Rectangle(x1, y2, Math.abs(x2 - x1), Math.abs(y2 - y1));
				} else if (x1 > x2 && y1 < y2) {
					shape = new Rectangle(x2, y1, Math.abs(x2 - x1), Math.abs(y2 - y1));
				} else if (x1 > x2 && y1 > y2) {
					shape = new Rectangle(x2, y2, Math.abs(x2 - x1), Math.abs(y2 - y1));
				}
			}

			if (shapeType.equals("Square")) {
				// quadrant 3
				if (x1 < x2 && y1 < y2) {
					shape = new Rectangle(x1, y1, Math.abs(x2 - x1), Math.abs(x2 - x1));
				}
				// quadrant 2
				else if (x1 < x2 && y1 > y2 && y2 > 0) {
					x1 = x1_old;
					shape = new Rectangle(x1, y2, Math.abs(y2 - y1), Math.abs(y2 - y1));
				}
				// quadrant 4
				else if (x1 > x2 && y1 < y2) {
					shape = new Rectangle(x2, y1, Math.abs(x2 - x1), Math.abs(x2 - x1));
				}
				// quadrant 1
				else if (x1 > x2 && y1 > y2 && y2 > 0) {
					int width = Math.abs(x2 - x1);
					shape = new Rectangle(x2, y1 - width, Math.abs(width), Math.abs(width));

				}
			}

			if (shapeType.equals("FreeHand")) {
				shape = new Line2D.Double(x1, y1, x2, y2);
				x1 = x2;
				y1 = y2;
			}

			if (shapeType.equals("Line")) {
				shape = new Line2D.Double(x1, y1, x2, y2);
			}

			if (shapeType.equals("Circle")) {
				// quadrant 3
				if (x1 < x2 && y1 < y2) {
					shape = new Ellipse2D.Double(x1, y1, Math.abs(x2 - x1), Math.abs(x2 - x1));
				}
				// quadrant 2
				else if (x1 < x2 && y1 > y2 && y2 > 0) {
					x1 = x1_old;
					shape = new Ellipse2D.Double(x1, y2, Math.abs(y2 - y1), Math.abs(y2 - y1));
				}
				// quadrant 4
				else if (x1 > x2 && y1 < y2) {
					shape = new Ellipse2D.Double(x2, y1, Math.abs(x2 - x1), Math.abs(x2 - x1));
				}
				// quadrant 1
				else if (x1 > x2 && y1 > y2 && y2 > 0) {
					int width = Math.abs(x2 - x1);
					shape = new Ellipse2D.Double(x2, y1 - width, Math.abs(width), Math.abs(width));
				}

			}

			if (shapeType.equals("Oval")) {

				if (x1 < x2 && y1 < y2) {
					shape = new Ellipse2D.Double(x1, y1, Math.abs(x2 - x1), Math.abs(y2 - y1));
				} else if (x1 < x2 && y1 > y2) {
					shape = new Ellipse2D.Double(x1, y2, Math.abs(x2 - x1), Math.abs(y2 - y1));
				} else if (x1 > x2 && y1 < y2) {
					shape = new Ellipse2D.Double(x2, y1, Math.abs(x2 - x1), Math.abs(y2 - y1));
				} else if (x1 > x2 && y1 > y2) {
					shape = new Ellipse2D.Double(x2, y2, Math.abs(x2 - x1), Math.abs(y2 - y1));
				}
			}

		}

		if (shape != null) {
			this.prev = shape;
			this.repaint();
		}
		if (shape != null && shapeType.equals("FreeHand")) {
			this.shapes.add(shape);
			this.repaint();
		}

	}

	public void mouseReleased(MouseEvent me) {
		x2 = me.getX();
		y2 = me.getY();
		Shape shape = null;

		int x1_old = x1;

		if (SwingUtilities.isLeftMouseButton(me)) {
			if (shapeType.equals("Rectangle")) {
				// a Rectangle cannot have a zero width or height

				if (x1 < x2 && y1 < y2) {
					shape = new Rectangle(x1, y1, Math.abs(x2 - x1), Math.abs(y2 - y1));
				} else if (x1 < x2 && y1 > y2) {
					shape = new Rectangle(x1, y2, Math.abs(x2 - x1), Math.abs(y2 - y1));
				} else if (x1 > x2 && y1 < y2) {
					shape = new Rectangle(x2, y1, Math.abs(x2 - x1), Math.abs(y2 - y1));
				} else if (x1 > x2 && y1 > y2) {
					shape = new Rectangle(x2, y2, Math.abs(x2 - x1), Math.abs(y2 - y1));
				}
			}

			if (shapeType.equals("Square")) {
				// quadrant 3
				if (x1 < x2 && y1 < y2) {
					shape = new Rectangle(x1, y1, Math.abs(x2 - x1), Math.abs(x2 - x1));
				}
				// quadrant 2
				else if (x1 < x2 && y1 > y2 && y2 > 0) {
					x1 = x1_old;
					shape = new Rectangle(x1, y2, Math.abs(y2 - y1), Math.abs(y2 - y1));
				}
				// quadrant 4
				else if (x1 > x2 && y1 < y2) {
					shape = new Rectangle(x2, y1, Math.abs(x2 - x1), Math.abs(x2 - x1));
				}
				// quadrant 1
				else if (x1 > x2 && y1 > y2 && y2 > 0) {
					int width = Math.abs(x2 - x1);
					shape = new Rectangle(x2, y1 - width, Math.abs(width), Math.abs(width));

				}
			}

			if (shapeType.equals("FreeHand")) {
				shape = new Line2D.Double(x1, y1, x2, y2);
				x1 = x2;
				y1 = y2;
			}

			if (shapeType.equals("Line")) {
				shape = new Line2D.Double(x1, y1, x2, y2);
			}

			if (shapeType.equals("Oval")) {

				if (x1 < x2 && y1 < y2) {
					shape = new Ellipse2D.Double(x1, y1, Math.abs(x2 - x1), Math.abs(y2 - y1));
				} else if (x1 < x2 && y1 > y2) {
					shape = new Ellipse2D.Double(x1, y2, Math.abs(x2 - x1), Math.abs(y2 - y1));
				} else if (x1 > x2 && y1 < y2) {
					shape = new Ellipse2D.Double(x2, y1, Math.abs(x2 - x1), Math.abs(y2 - y1));
				} else if (x1 > x2 && y1 > y2) {
					shape = new Ellipse2D.Double(x2, y2, Math.abs(x2 - x1), Math.abs(y2 - y1));
				}
			}

			if (shapeType.equals("Circle")) {
				// quadrant 3
				if (x1 < x2 && y1 < y2) {
					shape = new Ellipse2D.Double(x1, y1, Math.abs(x2 - x1), Math.abs(x2 - x1));
				}
				// quadrant 2
				else if (x1 < x2 && y1 > y2 && y2 > 0) {
					x1 = x1_old;
					shape = new Ellipse2D.Double(x1, y2, Math.abs(y2 - y1), Math.abs(y2 - y1));
				}
				// quadrant 4
				else if (x1 > x2 && y1 < y2) {
					shape = new Ellipse2D.Double(x2, y1, Math.abs(x2 - x1), Math.abs(x2 - x1));
				}
				// quadrant 1
				else if (x1 > x2 && y1 > y2 && y2 > 0) {
					int width = Math.abs(x2 - x1);
					shape = new Ellipse2D.Double(x2, y1 - width, Math.abs(width), Math.abs(width));
				}
			}
		}

		if (shape != null) {
			this.shapes.add(shape);
			this.repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent me) {
		//temporarily draws lines for closed Polygon so user can see them 
		Shape shape = null;
		
		if (shapeType.equals("ClosedPolygon") && !polygon_first) {
			//get coords
			x2 = me.getX();
			y2 = me.getY();
			

			shape = new Line2D.Double(x1, y1, x2, y2); 
		}
		if (shape != null) {
			this.prev = shape;
			this.repaint();
		}
	}

	// main
	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		SimpleDraw frame = new SimpleDraw();
		frame.pack();
		frame.setVisible(true);
	}

}