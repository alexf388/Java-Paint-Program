package eng.uwo.ca;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Path2D.Float;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.lang.Math;

import javax.swing.*;

//import eng.uwo.ca.SimplePaint.SimplePaintPanel;

class SimpleDraw extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
	// (x1,y1) = coordinate of mouse pressed

	private final static int BLACK = 0, RED = 1, GREEN = 2, BLUE = 3, CYAN = 4, MAGENTA = 5, YELLOW = 6;

	public int currentColor = BLACK; // The currently selected drawing color,
	// coded as one of the above constants.
	
	// ///************/
	int x1;
	int y1;
	int x2;
	int y2;
	private static final int HIT_BOX_SIZE = 2;
	// select global variables
	Shape selectedShape;
	Rectangle2D handleRectangle;
	Cursor curCursor;
	Rectangle tempRect=null;
	// polygon global variables
	boolean polygon_first = true;
	ArrayList<Integer> polygon_xPoints = new ArrayList<Integer>();
	ArrayList<Integer> polygon_yPoints = new ArrayList<Integer>();
	ArrayList<Shape> temp_Lines = new ArrayList<Shape>();
	ArrayList<Integer> color = new ArrayList<Integer>();
	ArrayList<Shape> shapes = new ArrayList<Shape>();
	Shape prev = null;
	String shapeType = "Rectangle";
	Stack<ArrayList<Shape>> stack = new Stack<ArrayList<Shape>>();
	Stack<ArrayList<Shape>> redostack = new Stack<ArrayList<Shape>>();
	Stack<ArrayList<Integer>> colorstack = new Stack<ArrayList<Integer>>();
	Stack<ArrayList<Integer>> colorredostack = new Stack<ArrayList<Integer>>();
	
	public static int[] convertIntegers(List<Integer> integers) {
		int[] ret = new int[integers.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = integers.get(i).intValue();
		}
		return ret;
	}

	private void createMenuBar() {

		JMenuBar menubar = new JMenuBar();
		ImageIcon icon = new ImageIcon("exit.png");

		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);

		JMenuItem eMenuItem = new JMenuItem("Exit", icon);
		eMenuItem.setMnemonic(KeyEvent.VK_E);
		eMenuItem.setToolTipText("Exit application");
		eMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});

		file.add(eMenuItem);
		menubar.add(file);

		// setJMenuBar(menubar);
	}

	public SimpleDraw() {

		// createMenuBar();

		setBackground(Color.white);
		stack.push(shapes);
		colorstack.push(color);
		System.out.println(stack.toString());
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
		JRadioButton selectButton = new JRadioButton("Select");
		JRadioButton cutPasteButton = new JRadioButton("Cut/Paste");

		JButton undo = new JButton("Undo");
		JButton redo = new JButton("Redo");
		
		
		cbg.add(freehandButton);
		cbg.add(lineButton);
		cbg.add(ovalButton);
		cbg.add(circleButton);
		cbg.add(rectangleButton);
		cbg.add(squareButton);
		cbg.add(openPolygonButton);
		cbg.add(closedPolygonButton);
		cbg.add(selectButton);
		cbg.add(cutPasteButton);
		cbg.add(undo);
		cbg.add(redo);

		

		cutPasteButton.addActionListener(this);
		freehandButton.addActionListener(this);
		lineButton.addActionListener(this);
		ovalButton.addActionListener(this);
		circleButton.addActionListener(this);
		rectangleButton.addActionListener(this);
		squareButton.addActionListener(this);
		openPolygonButton.addActionListener(this);
		closedPolygonButton.addActionListener(this);
		selectButton.addActionListener(this);
		undo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				System.out.println("Hoho");
				if (stack.size()>1 && colorstack.size()>1){
					System.out.println("before: " + stack.toString());
					
					colorredostack.push(colorstack.pop());
					redostack.push(stack.pop());
					
					color = new ArrayList<Integer>(colorstack.peek());
					shapes = new ArrayList<Shape>(stack.peek());
					
					System.out.println("after" + stack.toString());
					repaint();
					if (redostack.size()>3){
						redostack.remove(redostack.size()-4);
					}
				}
			}
		});
		redo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				
				if (redostack.size()>0){
					System.out.println("redo: " + redostack.toString());
					color = new ArrayList<Integer>(colorredostack.peek());
					shapes = new ArrayList<Shape>(redostack.peek());
					colorstack.push(colorredostack.pop());
					stack.push(redostack.pop());
					repaint();
				}
				
			
			}
		});
		// rectangleButton is default
		rectangleButton.setSelected(true);

		JPanel radioPanel = new JPanel(new FlowLayout());
		JPanel radioPanel1 = new JPanel(new FlowLayout());

		radioPanel.add(freehandButton);
		radioPanel.add(lineButton);
		radioPanel.add(ovalButton);
		radioPanel.add(circleButton);
		radioPanel.add(rectangleButton);
		radioPanel.add(squareButton);
		radioPanel.add(openPolygonButton);
		radioPanel.add(closedPolygonButton);
		radioPanel.add(selectButton);
		radioPanel.add(cutPasteButton);
		radioPanel.add(undo);
		radioPanel.add(redo);

		radioPanel1.setLayout(new BoxLayout(radioPanel1, BoxLayout.Y_AXIS));

		this.addMouseListener(this);
		
		this.addMouseMotionListener(this);

		this.setLayout(new BorderLayout());
		this.setBackground(Color.white);
		this.add(radioPanel1, BorderLayout.WEST);
		this.add(radioPanel, BorderLayout.NORTH);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		
		int index = 0;
		g.setColor(Color.BLACK);
		for (Shape shape : shapes) {
			Graphics2D g2 = (Graphics2D) g;
			if (!color.isEmpty()){
			switch (color.get(index)) {
			case BLACK:
				g2.setColor(Color.BLACK);
				break;
			case RED:
				g2.setColor(Color.RED);
				break;
			case GREEN:
				g2.setColor(Color.GREEN);
				break;
			case BLUE:
				g2.setColor(Color.BLUE);
				break;
			case CYAN:
				g2.setColor(Color.CYAN);
				break;
			case MAGENTA:
				g2.setColor(Color.MAGENTA);
				break;
			case YELLOW:
				g2.setColor(Color.YELLOW);
				break;

			}
			}
			index++;

			g2.draw(shape);
		}

		if (prev != null) {
			Graphics2D g2 = (Graphics2D) g;
			g2.draw(prev);
		}

		if (!temp_Lines.isEmpty()) {
			for (Shape shape : temp_Lines) {
				Graphics2D g2 = (Graphics2D) g;
				g2.draw(shape);
			}
		}

		// if there is something in the handlerectangle (i.e. a shape is
		// selected),
		// call the function to draw black boxes around the selected shape
		if (handleRectangle != null) {
			Graphics2D g2 = (Graphics2D) g;
			drawHighlightSquares(g2, handleRectangle);
		}

		if (curCursor != null)
			setCursor(curCursor);

		/***************************************/
		int width = this.getWidth(); // Width of the panel.
		int height = this.getHeight(); // Height of the panel.

		int colorSpacing = ((height - 56) / 7);
		//System.out.println("Official colorSpacing: " + colorSpacing); 

		// Distance between the top of one colored rectangle in the palette
		// and the top of the rectangle below it. The height of the
		// rectangle will be colorSpacing - 3. There are 7 colored rectangles,
		// so the available space is divided by 7. The available space allows
		// for the gray border and the 50-by-50 CLEAR button.

		/*
		 * Draw a 3-pixel border around the applet in gray. This has to be done
		 * by drawing three rectangles of different sizes.
		 */

		g.setColor(Color.GRAY);
		g.drawRect(0, 34, width - 1, height - 1); // TODO: original value: 86
		g.drawRect(1, 35, width - 3, height - 3); // original value: 87
		g.drawRect(2, 36, width - 5, height - 5); // original value: 88

		/*
		 * Draw a 56-pixel wide gray rectangle along the right edge of the
		 * applet. The color palette and Clear button will be drawn on top of
		 * this. (This covers some of the same area as the border I just drew.
		 */

		g.fillRect(width - 56, 33, 56, height);

		/* Draw the seven color rectangles. */

		g.setColor(Color.BLACK);
		g.fillRect(width - 53, 37 + 0 * colorSpacing, 50, colorSpacing - 3);
		g.setColor(Color.RED);
		g.fillRect(width - 53, 37 + 1 * colorSpacing, 50, colorSpacing - 3);
		g.setColor(Color.GREEN);
		g.fillRect(width - 53, 37 + 2 * colorSpacing, 50, colorSpacing - 3);
		g.setColor(Color.BLUE);
		g.fillRect(width - 53, 37 + 3 * colorSpacing, 50, colorSpacing - 3);
		g.setColor(Color.CYAN);
		g.fillRect(width - 53, 37 + 4 * colorSpacing, 50, colorSpacing - 3);
		g.setColor(Color.MAGENTA);
		g.fillRect(width - 53, 37 + 5 * colorSpacing, 50, colorSpacing - 3);
		g.setColor(Color.YELLOW);
		g.fillRect(width - 53, 37 + 6 * colorSpacing, 50, colorSpacing - 3);

		/*
		 * Draw a 2-pixel white border around the color rectangle of the current
		 * drawing color.
		 */

		g.setColor(Color.WHITE);
		g.drawRect(width - 55, 35 + currentColor * colorSpacing, 53, colorSpacing);
		g.drawRect(width - 54, 36 + currentColor * colorSpacing, 51, colorSpacing - 2);

	}

	public void actionPerformed(ActionEvent ae) {
		
		System.out.println("djskdjks");
		
		
			shapeType = ae.getActionCommand().toString();
		
	}

	public void mouseClicked(MouseEvent me) {
	}

	public void mouseEntered(MouseEvent me) {

	}

	public void mouseExited(MouseEvent me) {
	}

	public void mousePressed(MouseEvent me) {
		// variables
		Shape shape = null;
		GeneralPath closed_polygon = null;

		// if left mouse button
		if (SwingUtilities.isLeftMouseButton(me)) {

			int boxX = me.getX() - HIT_BOX_SIZE / 2;
			int boxY = me.getY() - HIT_BOX_SIZE / 2;

			int boxWidth = HIT_BOX_SIZE;
			int boxHeight = HIT_BOX_SIZE;

			if (shapeType.equals("Select")) {
				for (int i = 0; i < shapes.size(); i++) {
					if (!shapes.isEmpty()) {
						
						if (shapes.get(i).intersects(boxX, boxY, boxWidth, boxHeight)) {
							
							selectedShape = shapes.get(i);
							
							currentColor = color.get(i);
							if (selectedShape.getClass().getSimpleName().equals("Rectangle")){
								System.out.println("RECTANGLEMOVE");
								Rectangle temp = (Rectangle) shapes.get(i);
								int x = (int) (temp.getX());
								int y = (int) (temp.getY());
								int h = (int) temp.getHeight();
								int w = (int) temp.getWidth();
								tempRect = new Rectangle(x,y,w,h);
							}
							//temp.setFrame(x, y, w, h);
							
							
							
							
							
							if (handleRectangle != null)
								handleRectangle = shapes.get(i).getBounds2D();
						} else {
							handleRectangle = null;
						}

					}
				}

				this.repaint();
				x1 = me.getX();
				y1 = me.getY();
			}

			// open polygon
			if (shapeType.equals("OpenPolygon")) {
				if (polygon_first) {
					// System.out.println("FIRST TIME");
					x1 = me.getX();
					y1 = me.getY();

					polygon_xPoints.add(x1);
					polygon_yPoints.add(y1);

					polygon_first = false;
				} else {
					// System.out.println("Not first time");
					x2 = me.getX();
					y2 = me.getY();

					polygon_xPoints.add(x2);
					polygon_yPoints.add(y2);

					shape = new Line2D.Double(x1, y1, x2, y2);
					x1 = x2;
					y1 = y2;
				}
				// add the temporary line2d to temp_Lines to be shown on canvas
				if (shape != null) {
					this.temp_Lines.add(shape);
					this.repaint();
				}
			}

			// closed Polygon
			if (shapeType.equals("ClosedPolygon")) {
				System.out.println("Closed Polygon");
				// if first time
				if (polygon_first) {
					// System.out.println("FIRST TIME");
					x1 = me.getX();
					y1 = me.getY();

					polygon_xPoints.add(x1);
					polygon_yPoints.add(y1);

					polygon_first = false;
				}
				// else if not first time
				else {
					// System.out.println("Not first time");
					x2 = me.getX();
					y2 = me.getY();

					polygon_xPoints.add(x2);
					polygon_yPoints.add(y2);

					shape = new Line2D.Double(x1, y1, x2, y2);
					x1 = x2;
					y1 = y2;
				}
				// add the temporary line2d to temp_Lines to be shown on canvas
				if (shape != null) {
					this.temp_Lines.add(shape);
					this.repaint();
				}
			}
			// for any other shape
			else {
				x1 = me.getX();
				y1 = me.getY();
			}

			int width = this.getWidth(); 
			x2 = me.getX(); 
			y2 = me.getY(); 
			if (x2 > width - 53) {
				System.out.println("y2: " + y2); 
				changeColor(y2); // TODO: Clicked on the color palette.
			}
			
		}

		// if right mouse button
		if (SwingUtilities.isRightMouseButton(me)) {
			// open Polygon
			if (shapeType.equals("OpenPolygon")) {
				// if there is more than one point (i.e. shape with at least two
				// points by definition is a polygon)
				if (polygon_xPoints.size() > 1) {

					int[] temp_xPoints = new int[polygon_xPoints.size()];
					int[] temp_yPoints = new int[polygon_yPoints.size()];

					temp_xPoints = convertIntegers(polygon_xPoints);
					temp_yPoints = convertIntegers(polygon_yPoints);

					closed_polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, temp_xPoints.length);
					closed_polygon.moveTo(temp_xPoints[0], temp_yPoints[0]);

					for (int index = 1; index < temp_xPoints.length; index++) {
						closed_polygon.lineTo(temp_xPoints[index], temp_yPoints[index]);
					}

					// clear temp_Lines
					temp_Lines.clear();

					polygon_xPoints.clear();
					polygon_yPoints.clear();
					polygon_first = true;
					this.prev = null;
					this.repaint();
				}
				// else clear the arraylist
				else {
					polygon_xPoints.clear();
					polygon_yPoints.clear();

					polygon_first = true;
					this.prev = null;
					this.repaint();
				}

				// if the shape isn't null, add the shape to shapes to be drawn
				if (shape != null) {
					this.shapes = new ArrayList<Shape>(shapes);
					this.color = new ArrayList<Integer>(color);
					
					this.shapes.add(shape);
					stack.push(shapes);
					this.color.add(currentColor);
					colorstack.push(color);
					
					this.repaint();
				}
				// if closed_polygon isn't null, add the shape to shapes to be
				// drawn
				if (closed_polygon != null) {
					this.shapes = new ArrayList<Shape>(shapes);
					this.color = new ArrayList<Integer>(color);
					
					this.shapes.add(closed_polygon);
					stack.push(shapes);
					this.color.add(currentColor);
					colorstack.push(color);
					
					this.repaint();
				}
			}

			// closed Polygon
			if (shapeType.equals("ClosedPolygon")) {

				// if there is more than one point (i.e. shape with at least two
				// points by definition is a polygon)
				if (polygon_xPoints.size() > 1) {
					int[] temp_xPoints = new int[polygon_xPoints.size()];
					int[] temp_yPoints = new int[polygon_yPoints.size()];

					temp_xPoints = convertIntegers(polygon_xPoints);
					temp_yPoints = convertIntegers(polygon_yPoints);

					shape = new Polygon(temp_xPoints, temp_yPoints, polygon_xPoints.size());

					// clear temp_Lines
					temp_Lines.clear();

					polygon_xPoints.clear();
					polygon_yPoints.clear();
					polygon_first = true;
					this.prev = null;
					this.repaint();
				}
				// else clear the arraylist
				else {
					polygon_xPoints.clear();
					polygon_yPoints.clear();

					polygon_first = true;
					this.prev = null;
					this.repaint();
				}

				// if the shape isn't null, add the shape to shapes to be drawn
				if (shape != null) {
					this.shapes = new ArrayList<Shape>(shapes);
					this.color = new ArrayList<Integer>(color);
					
					this.shapes.add(shape);
					stack.push(shapes);
					this.color.add(currentColor);
					colorstack.push(color);
					
					this.repaint();
				}

			}
			
			//System.out.println(stack.toString());

		}

	}

	//TODO: FIX THIS SHIT 
	private void changeColor(int y) {

		int width = this.getWidth(); // Width of applet.
		int height = this.getHeight(); // Height of applet.
		int colorSpacing = ((height - 56) / 7); // Space for one color
					
		System.out.println("colorspacing: " + colorSpacing);
		
		// rectangle.
		int newColor = ((y-34) / colorSpacing); // Which color number was clicked?

		System.out.println("NewColor: "+ newColor); 
		if (newColor < 0 || newColor > 6) 
			return;

		Graphics g = getGraphics();
		g.setColor(Color.GRAY);
		g.drawRect(width - 55, 35 + currentColor * colorSpacing, 53, colorSpacing);
		g.drawRect(width - 54, 36 + currentColor * colorSpacing, 51, colorSpacing - 2);
		currentColor = newColor;
		g.setColor(Color.WHITE);
		g.drawRect(width - 55, 35 + currentColor * colorSpacing, 53, colorSpacing);
		g.drawRect(width - 54, 36 + currentColor * colorSpacing, 51, colorSpacing - 2);
		g.dispose();
		repaint();

	} // en
	
	public void mouseDragged(MouseEvent me) {
		Graphics g = getGraphics();
		x2 = me.getX();
		y2 = me.getY();

		int x1_old = x1;
		int boxX = me.getX() - HIT_BOX_SIZE / 2;
		int boxY = me.getY() - HIT_BOX_SIZE / 2;

		int boxWidth = HIT_BOX_SIZE;
		int boxHeight = HIT_BOX_SIZE;
		Shape shape = null;
		
		if (SwingUtilities.isLeftMouseButton(me)) {

			if (x2 < 10)
				x2 = (10 - me.getX()) + me.getX();
			if (x2 > 1262)
				x2 = me.getX() - (me.getX() - 1262);

			if (y2 < 80)
				y2 = (80 - me.getY()) + me.getY();

			// if select
			if (shapeType.equals("Select")) {
				for (int i = 0; i < shapes.size(); i++) {
					if (shapes.get(i).intersects(boxX - 30, boxY - 30, boxWidth + 60, boxHeight + 60)
							&& selectedShape.equals(shapes.get(i))) {
						currentColor = color.get(i);
						g.setColor(Color.BLACK);
						// TODO: figure out a way to get the x, y coords of a
						// shape
						x2 = me.getX();
						y2 = me.getY();

						// every shape has its own stupid and different way of
						// getting x, y. Thank you Java for this mess.
						//System.out.println("shape name: " + shapes.get(i).getClass().getSimpleName());
						//System.out.println("canonical name: " + shapes.get(i).getClass().getCanonicalName());

						// if the shape is a rectangle
						if (shapes.get(i).getClass().getSimpleName().equals("Rectangle")) {
							Rectangle temp = (Rectangle) shapes.get(i);
							
							
							 int x = (int) (temp.getX() + x2 - x1);
							 int y = (int) (temp.getY() + y2 - y1);
							 int h = (int) temp.getHeight();
							 int w = (int) temp.getWidth();
							//prev =  new Rectangle(x,y,w,h);
							temp.setFrame(x, y, w, h);
							prev = temp;
							//shapes.remove(i);
							//shapes.add(i, tempShape);
							//this.color.add(i, currentColor);
							
						//	this.color.remove(i + 1);

							x1 = x2;
							y1 = y2;

						}
						// ellipse
						else if (shapes.get(i).getClass().getCanonicalName().equals("java.awt.geom.Ellipse2D.Double")) {
							Ellipse2D.Double temp = (java.awt.geom.Ellipse2D.Double) shapes.get(i);

							int h = (int) temp.getHeight();
							int w = (int) temp.getWidth();
							int x = (int) (temp.x + x2 - x1);
							int y = (int) (temp.y + y2 - y1);
							temp.setFrame(x, y, w, h);

							this.prev = temp;

							//shapes.add(i, temp);
							//this.color.add(i, currentColor);
							//shapes.remove(i + 1);
							//this.color.remove(i + 1);

							x1 = x2;
							y1 = y2;
						}
						// closed polygon
						else if (shapes.get(i).getClass().getSimpleName().equals("Polygon")) {
							Polygon temp = (Polygon) shapes.get(i);

							// update xPoints
							temp.translate(x2 - x1, y2 - y1);
							this.prev = temp;

							//shapes.add(i, temp);
							//this.color.add(i, currentColor);
							//shapes.remove(i + 1);
							//this.color.remove(i + 1);

							x1 = x2;
							y1 = y2;
						}

						// open polygon
						else if (shapes.get(i).getClass().getSimpleName().equals("GeneralPath")) {
							GeneralPath temp = (GeneralPath) shapes.get(i);

							AffineTransform at = AffineTransform.getTranslateInstance(x2 - x1, y2 - y1);
							temp.transform(at);

							this.prev = temp;

							//shapes.add(i, temp);
							//this.color.add(i, currentColor);
							//shapes.remove(i + 1);
							//this.color.remove(i + 1);

							x1 = x2;
							y1 = y2;
						}

						else if (selectedShape.equals(shapes.get(i))) {
							System.out.println("Fuck this motherfucker");
							System.out.println("shape name: " + shapes.get(i).getClass().getSimpleName());
							System.out.println("canonical name: " + shapes.get(i).getClass().getCanonicalName());

							if (shapes.get(i).getClass().getCanonicalName().equals("java.awt.geom.Line2D.Double")) {
								System.out.println("Dragging");
								Line2D.Double temp = (Line2D.Double) shapes.get(i);

								double x1_original = temp.getX1() + x2 - x1;
								double x2_original = temp.getX2() + x2 - x1;
								double y1_original = temp.getY1() + y2 - y1;
								double y2_original = temp.getY2() + y2 - y1;

								// temp = new Line2D.Double(x1_original,
								// y1_original, x2_original, y2_original);
								temp.setLine(x1_original, y1_original, x2_original, y2_original);

								this.prev = temp;
								//shapes.add(i, temp);
								//shapes.remove(i + 1);

								x1 = x2;
								y1 = y2;
							}
						}
					}

					this.repaint();
				}
			}

			if (shapeType.equals("Rectangle")) {
				// a Rectangle cannot have a zero width or height

				if (x1 > 10 && y1 > 80 && x1 < 1262) {
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
				if (x1 > 10 && y1 > 80 && x1 < 1262) {
					shape = new Line2D.Double(x1, y1, x2, y2);
				}
			}

			if (shapeType.equals("Circle")) {
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
				if (x1 > 10 && y1 > 80 && x1 < 1262) {
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

		}

		if (shape != null && !shapeType.equals("Select")) {
			this.prev = shape;
			this.repaint();
		}
		if (shape != null && shapeType.equals("FreeHand")) {
			this.shapes.add(shape);
			this.color.add(currentColor);
			this.repaint();
		}

	}

	public void mouseReleased(MouseEvent me) {
		x2 = me.getX();
		y2 = me.getY();
		Shape shape = null;
		
		int x1_old = x1;
		int boxX = me.getX() - HIT_BOX_SIZE / 2;
		int boxY = me.getY() - HIT_BOX_SIZE / 2;

		int boxWidth = HIT_BOX_SIZE;
		int boxHeight = HIT_BOX_SIZE;
		if (SwingUtilities.isLeftMouseButton(me)) {

			if (x2 < 10)
				x2 = (10 - me.getX()) + me.getX();
			if (x2 > 1262)
				x2 = me.getX() - (me.getX() - 1262);

			if (y2 < 80)
				y2 = (80 - me.getY()) + me.getY();

			// Select
			if (shapeType.equals("Select")) {
				for (int i = 0; i < shapes.size(); i++) {
					
					if (shapes.get(i).intersects(boxX, boxY, boxWidth, boxHeight)) {
						handleRectangle = shapes.get(i).getBounds2D();
						selectedShape = shapes.get(i);
						if (selectedShape.getClass().getSimpleName().equals("Rectangle")){
							
							for (ArrayList<Shape> shap : stack){
								if(i<shap.size()){
									shap.remove(i);
									shap.add(i,tempRect);
								}
							}
							
						}
						if (prev != null){
							
							this.shapes = new ArrayList<Shape>(shapes);
							this.color = new ArrayList<Integer>(color);
							this.shapes.remove(i);
							this.shapes.add(i,prev);
							this.stack.push(shapes);
							this.color.add(currentColor);
							this.colorstack.push(color);

							this.repaint();
							prev = null;
						this.repaint();
						
					}
					
				}
				
					System.out.println("moved" + stack.toString());
				}
			}

			if (shapeType.equals("Rectangle")) {
				// a Rectangle cannot have a zero width or height
				if (x1 > 10 && y1 > 80 && x1 < 1262) {
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
			}

			if (shapeType.equals("Square")) {
				// quadrant 3
				if (x1 > 10 && y1 > 80 && x1 < 1262) {
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
			}

			if (shapeType.equals("FreeHand")) {
				shape = new Line2D.Double(x1, y1, x2, y2);
				x1 = x2;
				y1 = y2;
			}

			if (shapeType.equals("Line")) {
				if (x1 > 10 && y1 > 80 && x1 < 1262) {
					shape = new Line2D.Double(x1, y1, x2, y2);
				}
			}

			if (shapeType.equals("Oval")) {
				if (x1 > 10 && y1 > 80 && x1 < 1262) {
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

			if (shapeType.equals("Circle")) {
				// quadrant 3
				if (x1 > 10 && y1 > 80 && x1 < 1262) {
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
		}
		
		if (shape != null && (!shapeType.equals("Select"))) {
			prev = null;
		
			this.shapes = new ArrayList<Shape>(shapes);
			this.color = new ArrayList<Integer>(color);
			
			this.shapes.add(shape);
			
			this.stack.push(shapes);
			this.color.add(currentColor);
			this.colorstack.push(color);
			
			this.repaint();
			System.out.println("new" +stack.toString());
			
		}
	}

	@Override
	public void mouseMoved(MouseEvent me) {
		// temporarily draws lines for closed Polygon so user can see them
		Shape shape = null;

		int boxX = me.getX() - HIT_BOX_SIZE / 2;
		int boxY = me.getY() - HIT_BOX_SIZE / 2;

		int boxWidth = HIT_BOX_SIZE;
		int boxHeight = HIT_BOX_SIZE;

		// TODO: cursor changes when you hover over a shape
		if (shapeType.equals("Select")) {
			for (int i = 0; i < shapes.size(); i++) {
				if (!shapes.isEmpty()) {
					if (shapes.get(i).contains(me.getX(), me.getY())) {
						//System.out.println("FOUND THE SHAPE");
						curCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
					} else {
						curCursor = Cursor.getDefaultCursor();
					}
					if (shapes.get(i).intersects(boxX, boxY, boxWidth, boxHeight)) {
						//System.out.println("FOUND THE SHAPE2");
						curCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
					} else {
						curCursor = Cursor.getDefaultCursor();
					}

				}
				this.repaint();
			}
		}

		if (shapeType.equals("OpenPolygon") && !polygon_first) {
			// get coords
			x2 = me.getX();
			y2 = me.getY();

			shape = new Line2D.Double(x1, y1, x2, y2);
		}

		if (shapeType.equals("ClosedPolygon") && !polygon_first) {
			// get coords
			x2 = me.getX();
			y2 = me.getY();

			shape = new Line2D.Double(x1, y1, x2, y2);
		}
		if (shape != null) {
			this.prev = shape;
			this.repaint();
		}
	}

	public static void main(String[] args) {

		JFrame frame = new JFrame();
		frame.getContentPane().add(new SimpleDraw());
		frame.pack();
		frame.setVisible(true);
		frame.setSize(new Dimension(1000, 500));
		frame.getContentPane().setBackground(Color.WHITE);
	}

	// draw Highlight squares when a shape is selected
	public void drawHighlightSquares(Graphics2D g2D, Rectangle2D r) {
		double x = r.getX();
		double y = r.getY();
		double w = r.getWidth();
		double h = r.getHeight();

		g2D.setColor(Color.black);

		g2D.fill(new Rectangle.Double(x - 3.0, y - 3.0, 6.0, 6.0));
		g2D.fill(new Rectangle.Double(x + w * 0.5 - 3.0, y - 3.0, 6.0, 6.0));
		g2D.fill(new Rectangle.Double(x + w - 3.0, y - 3.0, 6.0, 6.0));
		g2D.fill(new Rectangle.Double(x - 3.0, y + h * 0.5 - 3.0, 6.0, 6.0));
		g2D.fill(new Rectangle.Double(x + w - 3.0, y + h * 0.5 - 3.0, 6.0, 6.0));
		g2D.fill(new Rectangle.Double(x - 3.0, y + h - 3.0, 6.0, 6.0));
		g2D.fill(new Rectangle.Double(x + w * 0.5 - 3.0, y + h - 3.0, 6.0, 6.0));
		g2D.fill(new Rectangle.Double(x + w - 3.0, y + h - 3.0, 6.0, 6.0));
	}
	
	
	
	

}