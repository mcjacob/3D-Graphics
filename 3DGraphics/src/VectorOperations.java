public abstract class VectorOperations
{
	public static Vector addVectors(Vector v1, Vector v2)
	{
		return new Vector(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z, false);
	}
	
	public static Vector subtractVectors(Vector v1, Vector v2)
	{
		return new Vector(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z, false);
	}
	
	public static Point addPoints(Point p1, Point p2)
	{
		return new Point(p1.x + p2.x, p1.y + p2.y);
	}
	
	public static double dotProduct(Vector v1, Vector v2)
	{
		if (v1 == null || v2 == null)
			return 0;
		
		return (v1.x * v2.x) + (v1.y * v2.y) + (v1.z * v2.z);
	}
	
	public static double dotProduct(Point p1, Point p2)
	{
		if (p1 == null || p2 == null)
			return 0;
		
		return (p1.x * p2.x) + (p1.y * p2.y);
	}
	
	public static Vector crossProduct(Vector v1, Vector v2)
	{
		if (v1 == null || v2 == null)
			return null;
		
		return new Vector(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x, false);
	}
	
	public static Vector getProj(Vector direction, Vector projected)
	{
		return direction.getScaledVersion(dotProduct(direction, projected) / (direction.r * direction.r));
	}
	
	public static double getComp(Vector direction, Vector projected)
	{
		return dotProduct(direction, projected) / direction.r;
	}
	
	public static void printVector(Vector v)
	{
		System.out.println("x: " + v.x);
		System.out.println("y: " + v.y);
		System.out.println("z: " + v.z);
		System.out.println("r: " + v.r);
		System.out.println("theta: " + v.theta);
		System.out.println("phi: " + v.phi);
	}
	
	public static void printPoint(Point p)
	{
		System.out.println("x: " + p.x);
		System.out.println("y: " + p.y);
	}
}