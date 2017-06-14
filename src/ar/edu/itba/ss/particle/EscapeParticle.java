package ar.edu.itba.ss.particle;

import ar.edu.itba.ss.main.Main;
import ar.edu.itba.ss.main.SocialModel;

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

		Pair targetPosition;
		if (getY()> Main.floorDistance) {
			targetPosition = new Pair(Main.W / 2, Main.floorDistance);
		}else{
			targetPosition = new Pair(getX(), -1);
		}
		Pair dir = Pair.sub(targetPosition, position);
		dir.normalize();
		return SocialModel.getDrivingForce(getMass(), velocity, dir);
	}

	private Pair getSocialForce(Particle p) {
		Pair dir = Pair.sub(position, p.position);
		double e = dir.abs() - p.getRadius() - getRadius();
		if (e > 1) {
			return new Pair(0, 0);
		}
		dir.normalize();
		return SocialModel.getSocialForce(dir, e);
	}

	private Pair[] getGranularForce(Particle p) {
		Pair dir = Pair.sub(p.position, position);
		double e = p.getRadius() + getRadius() - dir.abs();
		if (e < 0) {
			return new Pair[] { new Pair(0, 0), new Pair(0, 0) };
		}
		dir.normalize();
		return SocialModel.getContactForce(Pair.sub(velocity, p.velocity), dir, new Pair(-dir.y, dir.x), e);
	}

}
