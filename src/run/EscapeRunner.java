package run;

import java.util.ArrayList;
import java.util.List;

import model.EscapeParticle;
import model.Particle;
import model.Verlet;
import model.VerletParticle;
import utils.OutputFileGenerator;
import utils.OutputXYZFilesGenerator;
import utils.RandomUtils;

public class EscapeRunner {

	private double time;
	static public double W = 20.0, L = 20.0, D = 1.2, fall = 4.0;
	static public double drivingV = 1.5;
	static final public double Kn = 1.2e5, Kt = 2.4e5;
	static final public double A = 2000, B = 0.08;
	private int N = 200;
	static final public double tau = 0.5;
	private int idCounter = 1;
	private final double mass = 50;
	private final double maxTime = 5.0;
	private final double dt = 1e-4;
	private final double dt2 = 1.0 / 250;
	private final double MAX_ENERGY = 1e-6;

	public EscapeRunner() {
		RandomUtils.setSeed(1234);
		this.run();
	}

	private EscapeParticle createRandomParticle() {
		double r = RandomUtils.getRandomDouble(0.5, 0.58) / 2.0;
		double x = RandomUtils.getRandomDouble(r, W - r);
		double y = RandomUtils.getRandomDouble(r + fall, (L + fall) - r);
		return new EscapeParticle(idCounter, x, y, 0, 0, mass, r);
	}

	private List<VerletParticle> createParticles(int N) {
		List<VerletParticle> list = new ArrayList<VerletParticle>();
		while (idCounter - 1 < N) {
			EscapeParticle p = createRandomParticle();
			boolean areOverlapped = false;
			for (VerletParticle pp : list) {
				if (Particle.areOverlapped(p, pp)) {
					areOverlapped = true;
					break;
				}
			}
			if (!areOverlapped) {
				list.add(p);
				idCounter++;
			}
		}
		return list;
	}

	private void run() {
		OutputXYZFilesGenerator outputXYZFilesGenerator = new OutputXYZFilesGenerator("animation/", "state");
		OutputFileGenerator kineticEnergy = new OutputFileGenerator("animation/", "kinetic");
		OutputFileGenerator caudal = new OutputFileGenerator("animation/", "caudal");
		List<VerletParticle> particles = createParticles(N);
		Verlet v = new Verlet(particles, dt);
		time = 0;
		int totalCaudal = 0;
		double lastTime = -dt2-1.0;
		double maxPressure = 0.0;
		double energy = Double.POSITIVE_INFINITY;

		while (totalCaudal < N) {
			if (lastTime + dt2 < time) {
				outputXYZFilesGenerator.printState(particles);
				energy = getSystemKineticEnery(particles);
				kineticEnergy.addLine(String.valueOf(energy));
				double mp = particles.stream().mapToDouble(x -> x.getPressure()).max().getAsDouble();
				if (maxPressure < mp) {
					maxPressure = mp;
				}
				lastTime = time;
			}
			v.run();
			int c = getCaudal(particles);
			totalCaudal += c;
			for (int i = 0; i < c; i++) {
				caudal.addLine(String.valueOf(time));
			}
			time += dt;
		}
		System.out.println("Time: " + time);
		kineticEnergy.writeFile();
		caudal.writeFile();
	}

	private int getCaudal(List<VerletParticle> particles) {
		int caudal = 0;
		for (VerletParticle particle : particles) {
			if (particle.getOldPosition().y > fall && particle.getPosition().y <= fall) {
				caudal += 1;
			}
		}
		return caudal;
	}

	private double getSystemKineticEnery(List<VerletParticle> particles) {
		double K = 0;
		for (VerletParticle vp : particles) {
			K += vp.getKineticEnergy();
		}
		return K;
	}

}
