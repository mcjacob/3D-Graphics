public abstract class Operations
{
	public static int fastFloor(double n)
	{
		return n >= 0 ? (int) n : (int) (n - 1);
	}
	
	public static double pow(double base, int exp)
	{
		double result = base;
		
		for (int i = 0; i < exp; i++)
			result *= base;
		
		return result;
	}
	
	public static double max(double a, double b)
	{
		return a > b ? a : b;
	}
	
	public static double min(double a, double b)
	{
		return a < b ? a : b;
	}
}