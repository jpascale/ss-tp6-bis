package model;

import run.Main;
import utils.ForcesUtils;

public class EscapeParticle extends VerletParticle {

	public EscapeParticle(int id, double x, double y, double vx, double vy, double m, double r) {
		super(id, x, y, vx, vy, m, r);
	}

	@Override
	public Pair getOwnForce() {
		return getDrivingForce();
	}

	@Override
	public Pair[] getForce(Particle p) {
		Pair[] granularForce = getGranularForce(p);
		Pair socialForce = getSocialForce(p);
		Pair n = Pair.sum(granularForce[0], socialForce);
		return new Pair[] {n, granularForce[1]};
	}

	private Pair getDrivingForce() {

		Pair targetPosition = null;
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
			targetPosition = new Pair(Main.W / 2, Main.fall);
		}else{
			targetPosition = new Pair(getX(), -1);
		}
		Pair dir = Pair.sub(targetPosition, position);
		dir.normalize();
		return ForcesUtils.getDrivingForce(getMass(), velocity, dir);
	}

	private Pair getSocialForce(Particle p) {
		Pair dir = Pair.sub(position, p.position);
		double e = dir.abs() - p.getRadius() - getRadius();
		if (e > 1) {
			return new Pair(0, 0);
		}
		dir.normalize();
		return ForcesUtils.getSocialForce(dir, e);
	}

	private Pair[] getGranularForce(Particle p) {
		Pair dir = Pair.sub(p.position, position);
		double e = p.getRadius() + getRadius() - dir.abs();
		if (e < 0) {
			return new Pair[] { new Pair(0, 0), new Pair(0, 0) };
		}
		dir.normalize();
		return ForcesUtils.getForce(Pair.sub(velocity, p.velocity), dir, new Pair(-dir.y, dir.x), e);
	}

}
