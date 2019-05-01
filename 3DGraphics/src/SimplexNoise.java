public class SimplexNoise
{
	private SimplexNoiseOctave[] octaves;
	private double totalAmp = 0, totalNoise = 0;
	
	public SimplexNoise(int numberOfOctaves, double persistence)
	{
		octaves = new SimplexNoiseOctave[numberOfOctaves];
		for (int i = 0; i < numberOfOctaves; i++)
			octaves[i] = new SimplexNoiseOctave(i, persistence);
		
		for (SimplexNoiseOctave o: octaves)
			totalAmp += o.amplitude;
	}
	
	public double getNoise(Point p)
	{
		totalNoise = 0;
		
		for (SimplexNoiseOctave o: octaves)
			totalNoise += o.getNoise(p);
		
		return totalNoise / totalAmp;
	}
}