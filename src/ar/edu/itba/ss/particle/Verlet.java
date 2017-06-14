package ar.edu.itba.ss.particle;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ar.edu.itba.ss.main.Main;
import ar.edu.itba.ss.cell.CellIndexMethod;
import ar.edu.itba.ss.main.SocialModel;

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
				.add(new EscapeParticle(0, Main.W / 2 - Main.D / 2, Main.fall, 0, 0, 0, 0));
		vertexParticles
				.add(new EscapeParticle(0, Main.W / 2 + Main.D / 2, Main.fall, 0, 0, 0, 0));
		cim = new CellIndexMethod<VerletParticle>(particles, Main.L + Main.fall, 1.6, 1, false);
		toRemove = new LinkedList<VerletParticle>();
	}

	private void estimateOldPosition() {
		for (VerletParticle p : particles) {
			p.updateOldPosition(p.getOwnForce(), dt);
		}
	}

	public void run() {
		Map<VerletParticle, Pair> forces = new HashMap<VerletParticle, Pair>();
		Map<VerletParticle, Set<VerletParticle>> neighbours = cim.getNeighbours();
		for (VerletParticle p : neighbours.keySet()) {
			p.resetPressure();
			Pair force = p.getOwnForce();
			for (VerletParticle q : neighbours.get(p)) {
				Pair[] forceComponents = p.getForce(q);
				force.add(Pair.sum(forceComponents[0], forceComponents[1]));
				p.addPressure(forceComponents[0]);
			}
			force = Pair.sum(force, wallForce(p));
			forces.put(p, force);
		}

		time += dt;
		for (VerletParticle p : neighbours.keySet()) {
			Pair oldPosition = p.getOldPosition();
			updatePosition(p, forces.get(p), dt);
			updateVelocity(p, oldPosition, dt);
		}
		while(!toRemove.isEmpty()){
			VerletParticle p = toRemove.removeFirst();
			particles.remove(p);
		}
	}

	static double time = 0;

	private Pair wallForce(VerletParticle p) {
		Pair sum = new Pair(0, 0);
		if (p.position.x - p.getRadius() < 0 && p.position.y > Main.fall) {
			Pair[] force = SocialModel.wallLeftForce(p);
			sum.add(Pair.sum(force[0], force[1]));
			p.addPressure(force[0]);
		}
		if (p.position.x + p.getRadius() > Main.W && p.position.y > Main.fall) {
			Pair[] force = SocialModel.wallRightForce(p);
			sum.add(Pair.sum(force[0], force[1]));
			p.addPressure(force[0]);
		}
		if (Math.abs(p.position.y - Main.fall) < p.getRadius()) {
			if (inGap(p)) {
				for (VerletParticle particle : vertexParticles) {
					Pair[] forceComponents = p.getForce(particle);
					sum.add(Pair.sum(forceComponents[0], forceComponents[1]));
					p.addPressure(forceComponents[0]);
				}
			} else {
				Pair[] force = SocialModel.wallBottomForce(p);
				sum.add(Pair.sum(force[0], force[1]));
				p.addPressure(force[0]);
			}
		}
		return sum;
	}

	public boolean inGap(VerletParticle verletParticle) {
		double x = verletParticle.getX();
		double w2 = Main.W / 2;
		double d2 = Main.D / 2;
		return x >= w2 - d2 && x <= w2 + d2;
	}

	private void updatePosition(VerletParticle p, Pair force, double dt) {
		double rx = 2 * p.position.x - p.getOldPosition().x + force.x * Math.pow(dt, 2) / p.getMass();
		double ry = 2 * p.position.y - p.getOldPosition().y + force.y * Math.pow(dt, 2) / p.getMass();

		p.updatePosition(rx, ry);
		if(ry<0){
			toRemove.add(p);
		}
	}

	private void updateVelocity(VerletParticle p, Pair oldPosition, double dt) {
		double vx = (p.position.x - oldPosition.x) / (2 * dt);
		double vy = (p.position.y - oldPosition.y) / (2 * dt);
		p.updateVelocity(vx, vy);
	}
}
