package ar.edu.itba.ss.particle;

import ar.edu.itba.ss.cell.CellIndexMethod;
import ar.edu.itba.ss.main.Main;
import ar.edu.itba.ss.main.SocialModel;

import java.util.*;

public class Integrator {

	private List<EscapingParticle> particles;
	private double dt;
	private CellIndexMethod<EscapingParticle> cim;
	private List<EscapingParticle> vertexParticles;
	private LinkedList<EscapingParticle> toRemove;

	public Integrator(List<EscapingParticle> particles, double dt) {
		this.particles = particles;
		this.dt = dt;
		estimateOldPosition();
		vertexParticles = new LinkedList<>();
		vertexParticles
				.add(new FallParticle(0, Main.W / 2 - Main.D / 2, Main.floorDistance, 0, 0, 0, 0));
		vertexParticles
				.add(new FallParticle(0, Main.W / 2 + Main.D / 2, Main.floorDistance, 0, 0, 0, 0));
		cim = new CellIndexMethod<EscapingParticle>(particles, Main.L + Main.floorDistance, 1.6, 1, false);
		toRemove = new LinkedList<EscapingParticle>();
	}

	private void estimateOldPosition() {
		for (EscapingParticle p : particles) {
			p.updateOldPosition(p.getOwnForce(), dt);
		}
	}

	public void run() {
		Map<EscapingParticle, Pair> forces = new HashMap<EscapingParticle, Pair>();
		Map<EscapingParticle, Set<EscapingParticle>> neighbours = cim.getNeighbours();
		for (EscapingParticle p : neighbours.keySet()) {
			p.resetPressure();
			Pair force = p.getOwnForce();
			for (EscapingParticle q : neighbours.get(p)) {
				Pair[] forceComponents = p.getForce(q);
				force.add(Pair.sum(forceComponents[0], forceComponents[1]));
				p.addPressure(forceComponents[0]);
			}
			force = Pair.sum(force, wallForce(p));
			forces.put(p, force);
		}

		time += dt;
		for (EscapingParticle p : neighbours.keySet()) {
			Pair oldPosition = p.getOldPosition();
			updatePosition(p, forces.get(p), dt);
			updateVelocity(p, oldPosition, dt);
		}
		while(!toRemove.isEmpty()){
			EscapingParticle p = toRemove.removeFirst();
			particles.remove(p);
		}
	}

	static double time = 0;

	private Pair wallForce(EscapingParticle p) {
		Pair sum = new Pair(0, 0);
		if (p.position.x - p.getRadius() < 0 && p.position.y > Main.floorDistance) {
			Pair[] force = SocialModel.checkWallLeft(p);
			sum.add(Pair.sum(force[0], force[1]));
			p.addPressure(force[0]);
		}
		if (p.position.x + p.getRadius() > Main.W && p.position.y > Main.floorDistance) {
			Pair[] force = SocialModel.checkWallRight(p);
			sum.add(Pair.sum(force[0], force[1]));
			p.addPressure(force[0]);
		}
		if (Math.abs(p.position.y - Main.floorDistance) < p.getRadius()) {
			if (inGap(p)) {
				for (EscapingParticle particle : vertexParticles) {
					Pair[] forceComponents = p.getForce(particle);
					sum.add(Pair.sum(forceComponents[0], forceComponents[1]));
					p.addPressure(forceComponents[0]);
				}
			} else {
				Pair[] force = SocialModel.checkWallBottom(p);
				sum.add(Pair.sum(force[0], force[1]));
				p.addPressure(force[0]);
			}
		}
		return sum;
	}

	public boolean inGap(EscapingParticle verletParticle) {
		double x = verletParticle.getX();
		double w2 = Main.W / 2;
		double d2 = Main.D / 2;
		return x >= w2 - d2 && x <= w2 + d2;
	}

	private void updatePosition(EscapingParticle p, Pair force, double dt) {
		double rx = 2 * p.position.x - p.getOldPosition().x + force.x * Math.pow(dt, 2) / p.getMass();
		double ry = 2 * p.position.y - p.getOldPosition().y + force.y * Math.pow(dt, 2) / p.getMass();

		p.updatePosition(rx, ry);
		if(ry<0){
			toRemove.add(p);
		}
	}

	private void updateVelocity(EscapingParticle p, Pair oldPosition, double dt) {
		double vx = (p.position.x - oldPosition.x) / (2 * dt);
		double vy = (p.position.y - oldPosition.y) / (2 * dt);
		p.updateVelocity(vx, vy);
	}
}
