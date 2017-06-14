package model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import run.EscapeRunner;
import utils.CellIndexMethod;
import utils.ForcesUtils;

public class Verlet {

	private List<VerletParticle> particles;
	private double dt;
	private CellIndexMethod<VerletParticle> cim;
	private List<VerletParticle> vertexParticles;
	private LinkedList<VerletParticle> toRemove;

	public Verlet(List<VerletParticle> particles, double dt) {
		this.particles = particles;
		this.dt = dt;
		estimateOldPosition();
		vertexParticles = new LinkedList<>();
		vertexParticles
				.add(new EscapeParticle(0, EscapeRunner.W / 2 - EscapeRunner.D / 2, EscapeRunner.fall, 0, 0, 0, 0));
		vertexParticles
				.add(new EscapeParticle(0, EscapeRunner.W / 2 + EscapeRunner.D / 2, EscapeRunner.fall, 0, 0, 0, 0));
		cim = new CellIndexMethod<VerletParticle>(particles, EscapeRunner.L + EscapeRunner.fall, 1.6, 1, false);
		toRemove = new LinkedList<VerletParticle>();
	}

	private void estimateOldPosition() {
		for (VerletParticle p : particles) {
			p.updateOldPosition(p.getOwnForce(), dt);
		}
	}

	public void run() {
		Map<VerletParticle, Point> forces = new HashMap<VerletParticle, Point>();
		Map<VerletParticle, Set<VerletParticle>> neighbours = cim.getNeighbours();
		for (VerletParticle p : neighbours.keySet()) {
			p.resetPressure();
			Point force = p.getOwnForce();
			for (VerletParticle q : neighbours.get(p)) {
				Point[] forceComponents = p.getForce(q);
				force.add(Point.sum(forceComponents[0], forceComponents[1]));
				p.addPressure(forceComponents[0]);
			}
			force = Point.sum(force, wallForce(p));
			forces.put(p, force);
		}

		time += dt;
		for (VerletParticle p : neighbours.keySet()) {
			Point oldPosition = p.getOldPosition();
			updatePosition(p, forces.get(p), dt);
			updateVelocity(p, oldPosition, dt);
		}
		while(!toRemove.isEmpty()){
			VerletParticle p = toRemove.removeFirst();
			particles.remove(p);
		}
	}

	static double time = 0;

	private Point wallForce(VerletParticle p) {
		Point sum = new Point(0, 0);
		if (p.position.x - p.getRadius() < 0 && p.position.y > EscapeRunner.fall) {
			Point[] force = ForcesUtils.wallLeftForce(p);
			sum.add(Point.sum(force[0], force[1]));
			p.addPressure(force[0]);
		}
		if (p.position.x + p.getRadius() > EscapeRunner.W && p.position.y > EscapeRunner.fall) {
			Point[] force = ForcesUtils.wallRightForce(p);
			sum.add(Point.sum(force[0], force[1]));
			p.addPressure(force[0]);
		}
		if (Math.abs(p.position.y - EscapeRunner.fall) < p.getRadius()) {
			if (inGap(p)) {
				for (VerletParticle particle : vertexParticles) {
					Point[] forceComponents = p.getForce(particle);
					sum.add(Point.sum(forceComponents[0], forceComponents[1]));
					p.addPressure(forceComponents[0]);
				}
			} else {
				Point[] force = ForcesUtils.wallBottomForce(p);
				sum.add(Point.sum(force[0], force[1]));
				p.addPressure(force[0]);
			}
		}
		return sum;
	}

	public boolean inGap(VerletParticle verletParticle) {
		double x = verletParticle.getX();
		double w2 = EscapeRunner.W / 2;
		double d2 = EscapeRunner.D / 2;
		return x >= w2 - d2 && x <= w2 + d2;
	}

	private void updatePosition(VerletParticle p, Point force, double dt) {
		double rx = 2 * p.position.x - p.getOldPosition().x + force.x * Math.pow(dt, 2) / p.getMass();
		double ry = 2 * p.position.y - p.getOldPosition().y + force.y * Math.pow(dt, 2) / p.getMass();

		p.updatePosition(rx, ry);
		if(ry<0){
			toRemove.add(p);
		}
	}

	private void updateVelocity(VerletParticle p, Point oldPosition, double dt) {
		double vx = (p.position.x - oldPosition.x) / (2 * dt);
		double vy = (p.position.y - oldPosition.y) / (2 * dt);
		p.updateVelocity(vx, vy);
	}
}
