public class Point
{
public double x = 0, y = 0;
	
	public Point()
	{
		this(0, 0);
	}
	
	public Point(double n, double m)
	{
		x = n;
		y = m;
	}
	
	public Point(Point p)
	{
		this.x = p.x;
		this.y = p.y;
	}
	
	public Point getScaledVersion(double scale)
	{
		return new Point(x * scale, y * scale);
	}
	
	public String toString()
	{
		return "<" + x + "," + y + ">";
	}
}