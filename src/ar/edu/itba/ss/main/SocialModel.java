package ar.edu.itba.ss.main;

import ar.edu.itba.ss.particle.Pair;
import ar.edu.itba.ss.particle.EscapingParticle;

public class SocialModel {


	public static Pair[] checkWallRight(EscapingParticle p) {
		double e = p.getX() - Main.W + p.getRadius();
		return getContactForce(p.getVelocity(), new Pair(1, 0), new Pair(0, 1), e);
	}

	public static Pair[] checkWallLeft(EscapingParticle p) {
		double e = p.getRadius() - p.getX();
		return getContactForce(p.getVelocity(), new Pair(-1, 0), new Pair(0, -1), e);
	}

	public static Pair[] checkWallBottom(EscapingParticle p) {
		double e = -(p.getY() - Main.floorDistance) + p.getRadius();
		return getContactForce(p.getVelocity(), new Pair(0, -1), new Pair(1, 0), e);
	}

	public static Pair[] getContactForce(Pair relativeVelocity, Pair normal, Pair tangential, double e) {
		double Kn = Main.Kn;
		double Kt = Main.Kt;
		Pair n = normal.clone();
		n.applyFunction(x -> (-Kn * e * x));
		double prod = Pair.scalarProd(relativeVelocity, tangential);
		Pair t = new Pair(-Kt * e * prod * tangential.x, -Kt * e * prod * tangential.y);
		return new Pair[] {n, t};
	}

	public static Pair getDrivingForce(Double mass, Pair velocity, Pair normal) {
		double drivingV = Main.desiredVelocity;
		double tau = Main.TAU;
		Pair n = normal.clone();
		n.times(drivingV);
		Pair f = Pair.sub(n, velocity);
		f.times(mass/ tau);
		return f;
	}
	
	public static Pair getSocialForce(Pair normal, double e) {
		double A = Main.A;
		double B = Main.B;
		Pair n = normal.clone();
		n.times(A * Math.exp(-e/B));
		return n;
	}
}
