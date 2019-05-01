public class Vector
{
	public double x = 0, y = 0, z = 0, r = 0, theta = 0, phi = 0;
	
	public Vector()
	{
		this(0, 0, 0, false);
	}
	
	public Vector(double n, double m, double p, boolean polar)
	{
		if (polar)
		{
			r = n;
			theta = m;
			phi = p;
			
			normalize();
		}
		else
		{
			x = n;
			y = m;
			z = p;
			
			adjustPolar();
		}
	}
	
	public Vector(Vector v)
	{
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		this.r = v.r;
		this.theta = v.theta;
		this.phi = v.phi;
	}
	
	public void adjustCartesian()
	{
		x = r * Math.cos(phi) * Math.cos(theta);
		y = r * Math.cos(phi) * Math.sin(theta);
		z = r * Math.sin(phi);
	}
	
	public void adjustPolar()
	{
		r = Math.sqrt(x * x + y * y + z * z);
		
		if (isZero())
		{
			theta = 0;
			phi = 0;
		}
		else
		{
			phi = Math.asin(z / r);
			
			double projXY = r * Math.cos(phi);
			
			if (projXY != 0)
			{
				double sin = y / projXY, cos = x / projXY;
				
				if (sin >= 0)
					theta = Math.acos(cos);
				else if (cos >= 0)
					theta = Math.asin(sin);
				else
					theta = Math.PI + Math.asin(-sin);
			}
			else
				theta = 0;
		}
	}
	
	public void scaleBy(double scale)
	{
		r *= scale;
		
		adjustCartesian();
	}
	
	public Vector getScaledVersion(double scale)
	{
		return new Vector(x * scale, y * scale, z * scale, false);
	}
	
	public Vector setToUnit()
	{
		Vector temp = this.getScaledVersion(1.0 / r);
		
		this.x = temp.x;
		this.y = temp.y;
		this.z = temp.z;
		
		adjustPolar();
		
		return this;
	}
	
	public boolean isZero()
	{
		return r == 0;
	}
	
	public void normalize()
	{
		adjustCartesian();
		adjustPolar();
	}
	
	public void rotate(double angle, Vector axis)
	{
		double cos = Math.cos(angle), sin = Math.sin(angle);
		
		double i = x * (axis.x * axis.x * (1 - cos) + cos)
				+ y * (axis.x * axis.y * (1 - cos) - axis.z * sin)
				+ z * (axis.x * axis.z * (1 - cos) + axis.y * sin);
		
		double j = x * (axis.x * axis.y * (1 - cos) + axis.z * sin)
				+ y * (axis.y * axis.y * (1 - cos) + cos)
				+ z * (axis.y * axis.z * (1 - cos) - axis.x * sin);
		
		double k = x * (axis.x * axis.z * (1 - cos) - axis.y * sin)
				+ y * (axis.y * axis.z * (1 - cos) + axis.x * sin)
				+ z * (axis.z * axis.z * (1 - cos) + cos);
		
		x = i;
		y = j;
		z = k;
		
		adjustPolar();
	}
	
	public String getCartesian()
	{
		return "<" + x + "," + y + "," + z + ">";
	}
	
	public String getPolar()
	{
		return "<" + r + "," + theta + "," + phi + ">";
	}
	
	public Point to2D()
	{
		return new Point(x, y);
	}
}