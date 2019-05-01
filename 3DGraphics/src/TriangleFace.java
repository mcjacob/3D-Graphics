import java.awt.*;

public class TriangleFace
{
	public Vector[] corners;
	public Vector normal;
	
	private Vector v1, v2;
	
	public double lightLevel = 0;
	public Color color;
	
	public TriangleFace(Vector[] corners)
	{
		this.corners = corners;
		setNormal();
	}
	
	public TriangleFace(Vector[] corners, Color color, double lightLevel)
	{
		this.corners = corners;
		this.color = color;
		this.lightLevel = lightLevel;
		setNormal();
	}
	
	public TriangleFace(TriangleFace tf)
	{
		this(tf.corners, tf.color, tf.lightLevel);
	}
	
	private void setNormal()
	{
		if (corners[0] != null && corners[1] != null && corners[2] != null)
		{
			v1 = VectorOperations.subtractVectors(corners[1], corners[0]);
			v2 = VectorOperations.subtractVectors(corners[2], corners[0]);
			
			normal = VectorOperations.crossProduct(v1, v2);
			
			if (normal.z <= 0)
				normal = normal.getScaledVersion(-1);
			
			normal.setToUnit();
		}
	}
	
	public Vector getIntersectionPoint(Ray r)
	{
		double num = VectorOperations.dotProduct(normal, VectorOperations.subtractVectors(corners[0], r.startingPosition));
		double den = VectorOperations.dotProduct(normal, r.direction);
		
		if (den != 0)
		{
			double t = num / den;
			
			if (t > 0.001 && t < r.length && Math.abs(r.direction.x * t) > 0.1 && Math.abs(r.direction.y * t) > 0.1)
				return new Vector(r.direction.x * t + r.startingPosition.x,
						r.direction.y * t + r.startingPosition.y,
						r.direction.z * t + r.startingPosition.z, false);
			else
				return null;
		}
		else
			return null;
	}
	
	public boolean containsPoint(Vector v)
	{
		if (v == null)
			return false;
		
		Vector toPoint = VectorOperations.subtractVectors(v, corners[0]);
		Vector toCorner1 = VectorOperations.subtractVectors(corners[1], corners[0]);
		Vector toCorner2 = VectorOperations.subtractVectors(corners[2], corners[0]);
		
		double comp1 = VectorOperations.getComp(toCorner1, toPoint);
		double comp2 = VectorOperations.getComp(toCorner2, toPoint);
		
		if (comp1 < 0 || comp2 < 0 || comp1 + comp2 > 1)
			return false;
		
		return true;
	}
	
	public Vector getCenter()
	{
		return new Vector(
				(corners[0].x + corners[1].x + corners[2].x) / 3,
				(corners[0].y + corners[1].y + corners[2].y) / 3,
				(corners[0].z + corners[1].z + corners[2].z) / 3, false);
	}
}