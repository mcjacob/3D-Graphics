import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class Window extends JPanel implements MouseMotionListener, KeyListener
{
	private Vector cameraPos = new Vector(0, 0, 15, false);
	private Vector cameraRay = new Vector(0, 0, -1, false);
	private Vector cameraVel = new Vector();
	
	private double pixPerRad;
	private int width = 0, height = 0, xRobot = 0, yRobot = 0;
	private int[] keysPressed = new int[7];
	private Dimension dim;
	
	private Vector[][] points = new Vector[References.TOTAL_CHUNKS_X * References.CHUNK_SIZE][References.TOTAL_CHUNKS_Y * References.CHUNK_SIZE];
	private Vector pointVector;
	private int[][] xValues = new int[points.length][points[0].length];
	private int[][] yValues = new int[points.length][points[0].length];
	private boolean[][] inCameraView = new boolean[points.length][points[0].length];
	private boolean inCameraViewX, inCameraViewY;
	
	private TriangleFace[][][] faces = new TriangleFace[points.length - 1][points[0].length - 1][2];
	private boolean[][][] visible = new boolean[faces.length][faces[0].length][faces[0][0].length];
	private Ray sunRay, drawingRay;
	private int[] polyX, polyY;
	private int brightness;
	
	private SimplexNoise noise;
	
	public Window()
	{
		this.addMouseMotionListener(this);
		this.addKeyListener(this);
		this.setFocusable(true);
		
		this.setCursor(References.BLANK_CURSOR);
		
		noise = new SimplexNoise(5, 0.5);
		
		double x, y;
		for (int i = 0; i < points.length; i++)
			for (int j = 0; j < points[0].length; j++)
			{
				x = (i - (points.length - 1) / 2.0) * 0.5;
				y = (j - (points[0].length - 1) / 2.0) * 0.5;
				
				points[i][j] = new Vector(x, y, References.AMPLITUDE * noise.getNoise(new Point(x / References.FREQUENCY, y / References.FREQUENCY)), false);
			}
		
		while (true)
			if (!interpolate())
				break;
		
		for (int i = 0; i < points.length - 1; i++)
			for (int j = 0; j < points[0].length - 1; j++)
			{
				faces[i][j][0] = new TriangleFace(new Vector[] {points[i][j], points[i + 1][j], points[i + 1][j + 1]});
				faces[i][j][1] = new TriangleFace(new Vector[] {points[i][j], points[i][j + 1], points[i + 1][j + 1]});
				
				brightness = (int) (255 * Math.abs(VectorOperations.dotProduct(faces[i][j][0].normal, References.SUN)) / faces[i][j][0].normal.r / References.SUN.r);
				faces[i][j][0].color = new Color(brightness, brightness, brightness, brightness);
				
				brightness = (int) (255 * Math.abs(VectorOperations.dotProduct(faces[i][j][1].normal, References.SUN)) / faces[i][j][1].normal.r / References.SUN.r);
				faces[i][j][1].color = new Color(brightness, brightness, brightness, brightness);
			}
		
		for (int i = 0; i < faces.length; i++)
			for (int j = 0; j < faces.length; j++)
			{
				sunRay = new Ray(faces[i][j][0].getCenter(), References.SUN);
				if (intersectsFace(sunRay))
					faces[i][j][0].color = faces[i][j][0].color.darker();
				
				sunRay = new Ray(faces[i][j][1].getCenter(), References.SUN);
				if (intersectsFace(sunRay))
					faces[i][j][1].color = faces[i][j][1].color.darker();
			}
		
		keysPressed[6] = 1;
	}
	
	private boolean intersectsFace(Ray r)
	{
		TriangleFace prevFace = null, currFace;
		Vector pos;
		int count = 0;
		
		for (double t = 0; t <= r.length && count < 5; t += 0.1)
		{
			pos = r.valueAt(t);
			currFace = getFaceAt(pos);
			
			if (currFace == null)
				return false;
			else if (currFace != prevFace)
			{
				if (currFace.containsPoint(currFace.getIntersectionPoint(r)))
					return true;
				
				count = 0;
			}
			else
				count++;
			
			prevFace = currFace;
		}
		
		return false;
	}
	
	private TriangleFace getFaceAt(Vector pos)
	{
		int i = Operations.fastFloor(0.5 * (4 * pos.x + faces.length));
		int j = Operations.fastFloor(0.5 * (4 * pos.y + faces[0].length));
		
		if (i >= faces.length || j >= faces[0].length || i < 0 || j < 0)
			return null;
		
		if (pos.x - points[i][j].x > pos.y - points[i][j].y)
			return faces[i][j][0];
		else
			return faces[i][j][1];
	}
	
	private boolean interpolate()
	{
		boolean interpolated = false;
		double average;
		for (int i = 0; i < points.length; i++)
			for (int j = 0; j < points[0].length; j++)
			{
				if (i < points.length - 1 && Math.abs(points[i][j].z - points[i + 1][j].z) > References.AMPLITUDE / 16)
				{
					interpolated = true;
					
					average = (points[i][j].z + points[i + 1][j].z) / 2;
					
					points[i][j].z -= (points[i][j].z - average) / 32;
					points[i + 1][j].z -= (points[i + 1][j].z - average) / 32;
					
					points[i][j].adjustPolar();
					points[i + 1][j].adjustPolar();
				}
				
				if (j < points[0].length - 1 && Math.abs(points[i][j].z - points[i][j + 1].z) > References.AMPLITUDE / 16)
				{
					interpolated = true;
					
					average = (points[i][j].z + points[i][j + 1].z) / 2;
					
					points[i][j].z -= (points[i][j].z - average) / 32;
					points[i][j + 1].z -= (points[i][j + 1].z - average) / 32;
					
					points[i][j].adjustPolar();
					points[i][j + 1].adjustPolar();
				}
			}
		
		return interpolated;
	}
	
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		dim = this.getSize();
		
		width = dim.width;
		height = dim.height;
		pixPerRad = height / (Math.PI / 3);
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		
		g.setColor(Color.BLACK);
		g.drawRect(width / 2 - 10, height / 2 - 1, 20, 2);
		g.drawRect(width / 2 - 1, height / 2 - 10, 2, 20);
		
		cameraVel = new Vector(keysPressed[0] - keysPressed[2], keysPressed[1] - keysPressed[3], keysPressed[5] - keysPressed[4], false);
		
		if (!cameraVel.isZero())
		{
			cameraVel.setToUnit();
			cameraVel.theta += cameraRay.theta;
			cameraVel.normalize();
			cameraPos = VectorOperations.addVectors(cameraPos, cameraVel.getScaledVersion(References.dt * References.CAMERA_SPEED * keysPressed[6]));
		}
		
		References.HORIZONTAL_AXIS.theta = cameraRay.theta + Math.PI / 2;
		References.HORIZONTAL_AXIS.normalize();
		
		for (int i = 0; i < points.length; i++)
			for (int j = 0; j < points[0].length; j++)
			{
				pointVector = VectorOperations.subtractVectors(points[i][j], cameraPos);
				
				inCameraView[i][j] = pointVector.r < References.RENDER_DISTANCE;
				
				if (inCameraView[i][j])
				{
					drawingRay = new Ray(points[i][j], cameraPos);
					
					inCameraView[i][j] = !intersectsFace(drawingRay);
					
					pointVector.rotate(cameraRay.phi, References.HORIZONTAL_AXIS);
					pointVector.rotate(-cameraRay.theta, References.VERTICAL_AXIS);
					
					xValues[i][j] = (int) (-pointVector.theta * pixPerRad) + width / 2;
					yValues[i][j] = (int) (-pointVector.phi * pixPerRad) + height / 2;
					
					if (inCameraView[i][j])
					{
						inCameraViewX = Math.abs(xValues[i][j] - width / 2) <= width / 2;
						inCameraViewY = Math.abs(yValues[i][j] - height / 2) <= height / 2;
						inCameraView[i][j] = inCameraViewX && inCameraViewY;
					}
				}
			}
		
		for (int i = 0; i < faces.length; i++)
			for (int  j = 0; j < faces[0].length; j++)
			{
				visible[i][j][0] = (inCameraView[i][j] || inCameraView[i + 1][j] || inCameraView[i + 1][j + 1]) && intersectsFace(new Ray(faces[i][j][0].getCenter(), cameraPos));
				visible[i][j][1] = (inCameraView[i][j] || inCameraView[i][j + 1] || inCameraView[i + 1][j + 1]) && intersectsFace(new Ray(faces[i][j][1].getCenter(), cameraPos));
			}
		
		for (int i = 0; i < points.length; i++)
			for (int j = 0; j < points[0].length; j++)
			{
				if (inCameraView[i][j])
				{
					g.setColor(Color.BLACK);
					
					if (i > 0)
						if (inCameraView[i - 1][j])
							g.drawLine(xValues[i][j], yValues[i][j], xValues[i - 1][j], yValues[i - 1][j]);
					if (j > 0)
						if (inCameraView[i][j - 1])
							g.drawLine(xValues[i][j], yValues[i][j], xValues[i][j - 1], yValues[i][j - 1]);
					if (i > 0 && j > 0)
						if (inCameraView[i - 1][j - 1])
							g.drawLine(xValues[i][j], yValues[i][j], xValues[i - 1][j - 1], yValues[i - 1][j - 1]);
					
					if (i < faces.length && j < faces[0].length)
					{
						if (visible[i][j][0])
						{
							polyX = new int[] {xValues[i][j], xValues[i + 1][j], xValues[i + 1][j + 1]};
							polyY = new int[] {yValues[i][j], yValues[i + 1][j], yValues[i + 1][j + 1]};
							
							g.setColor(faces[i][j][0].color);
							g.fillPolygon(polyX, polyY, 3);
						}
						
						if (visible[i][j][1])
						{
							polyX = new int[] {xValues[i][j], xValues[i][j + 1], xValues[i + 1][j + 1]};
							polyY = new int[] {yValues[i][j], yValues[i][j + 1], yValues[i + 1][j + 1]};
							
							g.setColor(faces[i][j][0].color);
							g.fillPolygon(polyX, polyY, 3);
						}
					}
				}
			}
	}
	
	public void resetCursorPos()
	{
		xRobot = this.getLocationOnScreen().x;
		yRobot = this.getLocationOnScreen().y;
		Graphics3D.robot.mouseMove(width / 2 + xRobot, height / 2 + yRobot);
	}

	@Override
	public void keyPressed(KeyEvent arg0)
	{	
		if (arg0.getKeyCode() == KeyEvent.VK_W)
			keysPressed[0] = 1;
		if (arg0.getKeyCode() == KeyEvent.VK_A)
			keysPressed[1] = 1;
		if (arg0.getKeyCode() == KeyEvent.VK_S)
			keysPressed[2] = 1;
		if (arg0.getKeyCode() == KeyEvent.VK_D)
			keysPressed[3] = 1;
		if (arg0.getKeyCode() == KeyEvent.VK_SHIFT)
			keysPressed[4] = 1;
		if (arg0.getKeyCode() == KeyEvent.VK_SPACE)
			keysPressed[5] = 1;
		if (arg0.getKeyCode() == KeyEvent.VK_CONTROL)
			keysPressed[6] = 2;
		
		if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			if (Graphics3D.activateRobot)
			{
				Graphics3D.activateRobot = false;
				this.setCursor(References.DEFAULT_CURSOR);
			}
			else
			{
				Graphics3D.activateRobot = true;
				this.setCursor(References.BLANK_CURSOR);
				resetCursorPos();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0)
	{
		if (arg0.getKeyCode() == KeyEvent.VK_W)
			keysPressed[0] = 0;
		if (arg0.getKeyCode() == KeyEvent.VK_A)
			keysPressed[1] = 0;
		if (arg0.getKeyCode() == KeyEvent.VK_S)
			keysPressed[2] = 0;
		if (arg0.getKeyCode() == KeyEvent.VK_D)
			keysPressed[3] = 0;
		if (arg0.getKeyCode() == KeyEvent.VK_SHIFT)
			keysPressed[4] = 0;
		if (arg0.getKeyCode() == KeyEvent.VK_SPACE)
			keysPressed[5] = 0;
		if (arg0.getKeyCode() == KeyEvent.VK_CONTROL)
			keysPressed[6] = 1;
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
	}

	@Override
	public void mouseDragged(MouseEvent arg0)
	{
	}

	@Override
	public void mouseMoved(MouseEvent arg0)
	{
		if (Graphics3D.activateRobot)
		{
			if (arg0.getY() < height / 2)
				cameraRay.phi = Math.min(cameraRay.phi - (arg0.getY() - height / 2) * References.MOUSE_SENSITIVITY, Math.PI / 2 - 0.0001);
			else
				cameraRay.phi = Math.max(cameraRay.phi - (arg0.getY() - height / 2) * References.MOUSE_SENSITIVITY, - (Math.PI / 2 - 0.0001));
			
			cameraRay.theta += (width / 2 - arg0.getX()) * References.MOUSE_SENSITIVITY;
			
			if (cameraRay.theta > 3 * Math.PI / 2 || cameraRay.theta < - Math.PI / 2)
				cameraRay.normalize();
			else
				cameraRay.adjustCartesian();
			resetCursorPos();
		}
	}
}
