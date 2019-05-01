import java.awt.*;
import java.awt.image.*;
import java.util.*;

public abstract class References
{
	// ALL THE FINALS
	public static final double CAMERA_SPEED = 25.0;
	public static final double MOUSE_SENSITIVITY = 0.0001;
	public static final double AMPLITUDE = 10.0;
	public static final double FREQUENCY = 20.0;
	public static final double RENDER_DISTANCE = 40.0;
	
	public static final int POINT_RADIUS = 10;
	
	public static final BufferedImage CURSOR_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	public static final Cursor BLANK_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(CURSOR_IMAGE, new java.awt.Point(0, 0), "blank cursor");
	public static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
	
	public static final int CHUNK_SIZE = 16;
	public static final int TOTAL_CHUNKS_X = 1;
	public static final int TOTAL_CHUNKS_Y = 1;
	
	public static final Point[] points = 
		{
				new Point(1, 1),
				new Point(-1, 1),
				new Point(1, -1),
				new Point(-1, -1)
		};
	
	public static Vector HORIZONTAL_AXIS = new Vector(1, 0, 0, true);
	public static final Vector VERTICAL_AXIS = new Vector(0, 0, 1, false);
	public static final Vector SUN = new Vector(0, 0, 25000, false);
	
	public static final double F = (Math.sqrt(3) - 1) / 2;
	public static final double G = (1 - 1 / Math.sqrt(3)) / 6;
	
	// ALL THE GLOBAL VARIABLES
	public static double dt = 0;
	public static long seed;
	
	// THE RNG
	public static final Random RNG = new Random(seed);
}