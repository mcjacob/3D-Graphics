public class Ray
{
	public Vector direction, startingPosition, endPosition;
	public double length;
	
	public Ray(Vector d, Vector sp, double l)
	{
		direction = d.setToUnit();
		startingPosition = sp;
		length = l;
		
		endPosition = VectorOperations.addVectors(startingPosition, direction.getScaledVersion(length));
	}
	
	public Ray(Vector sp, Vector ep)
	{
		startingPosition = sp;
		endPosition = ep;
		
		direction = VectorOperations.subtractVectors(endPosition, startingPosition);
		length = direction.r;
		direction.setToUnit();
	}
	
	public Vector valueAt(double t)
	{
		if (t <= length && t >= 0)
			return VectorOperations.addVectors(startingPosition, direction.getScaledVersion(t));
		
		return null;
	}
}