public class SimplexNoiseOctave
{
	public double frequency, amplitude, noise;
	
	private Point tempPoint, testPoint;
	private Point[] simplex, gradients, displacements;
	private double[] contributions;
	
	private Point[] possibleGradients = new Point[]
			{
					new Point(1, 1),
					new Point(1, 0),
					new Point(1, -1),
					new Point(0, 1),
					new Point(0, -1),
					new Point(-1, 1),
					new Point(-1, 0),
					new Point(-1, -1)
			};
	
	private int[][] hash = new int[256][256];
	
	public SimplexNoiseOctave(int octaveNumber, double persistence)
	{
		frequency = Math.pow(2, octaveNumber);
		amplitude = Math.pow(persistence, octaveNumber);
		
		for (int i = 0; i < 256; i++)
			for (int j = 0; j < 256; j++)
				hash[i][j] = References.RNG.nextInt(8);
	}
	
	public double getNoise(Point p)
	{
		testPoint = new Point(p.x / frequency, p.y / frequency);
		
		skewPoint();
		selectSimplex();
		getGradients();
		unskewSystem();
		getDisplacements();
		getContributions();
		
		noise = 0;
		for (double d: contributions)
			noise += d;
		
		noise *= 170;
		noise = Operations.max(noise, -1);
		noise = Operations.min(noise, 1);
		
		noise *= amplitude;
		
		return noise;
	}
	
	private void skewPoint()
	{
		tempPoint = new Point(testPoint);
		
		testPoint.x = tempPoint.x + References.F * (tempPoint.x + tempPoint.y);
		testPoint.y = tempPoint.y + References.F * (tempPoint.x + tempPoint.y);
	}
	
	private void selectSimplex()
	{
		simplex = new Point[3];
		
		simplex[0] = new Point(Operations.fastFloor(testPoint.x), Operations.fastFloor(testPoint.y));
		simplex[1] = new Point(simplex[0].x + 1, simplex[0].y + 1);
		
		if (testPoint.x > testPoint.y)
			simplex[2] = new Point(simplex[0].x + 1, simplex[0].y);
		else
			simplex[2] = new Point(simplex[0].x, simplex[0].y + 1);
	}
	
	private void getGradients()
	{
		gradients = new Point[3];
		
		for (int i = 0; i < 3; i++)
			gradients[i] = possibleGradients[hash[(int) (simplex[i].x * 31) & 255][(int) (simplex[i].y * 31) & 255]];
	}
	
	private void unskewSystem()
	{
		for (Point p: simplex)
		{
			tempPoint = new Point(p);
			
			p.x = tempPoint.x - References.G * (tempPoint.x + tempPoint.y);
			p.y = tempPoint.y - References.G * (tempPoint.x + tempPoint.y);
		}
		
		tempPoint = new Point(testPoint);
		
		testPoint.x = tempPoint.x - References.G * (tempPoint.x + tempPoint.y);
		testPoint.y = tempPoint.y - References.G * (tempPoint.x + tempPoint.y);
	}
	
	private void getDisplacements()
	{
		displacements = new Point[3];
		
		for (int i = 0; i < 3; i++)
			displacements[i] = new Point(testPoint.x - simplex[i].x, testPoint.y - simplex[i].y);
	}
	
	private void getContributions()
	{
		contributions = new double[3];
		
		for (int i = 0; i < 3; i++)
		{
			contributions[i] = Operations.pow(Operations.max(0, 0.5 - VectorOperations.dotProduct(displacements[i], displacements[i])), 4)
					* VectorOperations.dotProduct(displacements[i], gradients[i]);
		}
	}
}