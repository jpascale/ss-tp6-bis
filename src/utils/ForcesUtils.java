package utils;

import model.Point;
import model.VerletParticle;
import run.EscapeRunner;

public class ForcesUtils {

	public static Point[] getForce(Point relativeVelocity, Point normal, Point tangential, double e) {
		double Kn = EscapeRunner.Kn;
		double Kt = EscapeRunner.Kt;
		Point n = normal.clone();
		n.applyFunction(x -> (-Kn * e * x));
		double prod = Point.scalarProd(relativeVelocity, tangential);
		Point t = new Point(-Kt * e * prod * tangential.x, -Kt * e * prod * tangential.y);
		return new Point[] {n, t};
	}

	public static Point[] wallRightForce(VerletParticle p) {
		double e = p.getX() - EscapeRunner.W + p.getRadius();
		return getForce(p.getVelocity(), new Point(1, 0), new Point(0, 1), e);
	}

	public static Point[] wallLeftForce(VerletParticle p) {
		double e = p.getRadius() - p.getX();
		return getForce(p.getVelocity(), new Point(-1, 0), new Point(0, -1), e);
	}

	public static Point[] wallBottomForce(VerletParticle p) {
		double e = -(p.getY() - EscapeRunner.fall) + p.getRadius();
		return getForce(p.getVelocity(), new Point(0, -1), new Point(1, 0), e);
	}
	
	public static Point getDrivingForce(Double mass, Point velocity, Point normal) {
		//revisar esto
		double drivingV = EscapeRunner.drivingV;
		double tau = EscapeRunner.tau;
		Point n = normal.clone();
		n.times(drivingV);
		Point f = Point.sub(n, velocity);
		f.times(mass/ tau);
		return f;
	}
	
	public static Point getSocialForce(Point normal, double e) {
		double A = EscapeRunner.A;
		double B = EscapeRunner.B;
		Point n = normal.clone();
		n.times(A * Math.exp(-e/B));
		return n;
	}
}
