package model;

import run.Main;
import utils.ForcesUtils;

public class EscapeParticle extends VerletParticle {

	public EscapeParticle(int id, double x, double y, double vx, double vy, double m, double r) {
		super(id, x, y, vx, vy, m, r);
	}

	@Override
	public Point getOwnForce() {
		return getDrivingForce();
	}

	@Override
	public Point[] getForce(Particle p) {
		Point[] granularForce = getGranularForce(p);
		Point socialForce = getSocialForce(p);
		Point n = Point.sum(granularForce[0], socialForce);
		return new Point[] {n, granularForce[1]};
	}

	private Point getDrivingForce() {

		Point targetPosition = null;
		if (getY()> Main.fall) {
			double x;
			if(getX()< Main.W / 2 - Main.D/2 + getRadius()){
				x = Main.W / 2 - Main.D/2 + getRadius();
			}else
			if(getX()> Main.W / 2 + Main.D/2 - getRadius()){
				x = Main.W / 2 + Main.D/2 - getRadius();
			}else{
				x = getX();
			}
			targetPosition = new Point(Main.W / 2, Main.fall);
		}else{
			targetPosition = new Point(getX(), -1);
		}
		Point dir = Point.sub(targetPosition, position);
		dir.normalize();
		return ForcesUtils.getDrivingForce(getMass(), velocity, dir);
	}

	private Point getSocialForce(Particle p) {
		Point dir = Point.sub(position, p.position);
		double e = dir.abs() - p.getRadius() - getRadius();
		if (e > 1) {
			return new Point (0, 0);
		}
		dir.normalize();
		return ForcesUtils.getSocialForce(dir, e);
	}

	private Point[] getGranularForce(Particle p) {
		Point dir = Point.sub(p.position, position);
		double e = p.getRadius() + getRadius() - dir.abs();
		if (e < 0) {
			return new Point[] { new Point(0, 0), new Point(0, 0) };
		}
		dir.normalize();
		return ForcesUtils.getForce(Point.sub(velocity, p.velocity), dir, new Point(-dir.y, dir.x), e);
	}

}
