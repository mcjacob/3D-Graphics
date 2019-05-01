import java.awt.*;
import javax.swing.*;
import java.util.*;

public class Graphics3D
{
	private static long prevTime = 0, currTime = 0;
	private static double maxFPS = 100;
	
	public static Robot robot;
	public static boolean activateRobot = true;
	
	public static JFrame frame;
	public static Window window;
	
	public static void main(String[] args)
	{
		References.seed = new Random().nextLong();
		
		System.out.println(References.seed);
		
		try
		{
			robot = new Robot();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		frame = new JFrame();
		window = new Window();
		
		frame.add(window);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		frame.setVisible(true);
		
		window.resetCursorPos();
		
		prevTime = System.currentTimeMillis();
		
		while (true)
		{
			currTime = System.currentTimeMillis();
			
			if ((currTime - prevTime) / 1000.0 >= 1.0 / maxFPS)
			{
				References.dt = (currTime - prevTime) / 1000.0;
				window.repaint();
				prevTime = currTime;
			}
		}
	}
	
}