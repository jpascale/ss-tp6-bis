package run;

import model.EscapeParticle;
import model.Particle;
import model.Verlet;
import model.VerletParticle;
import utils.OutputFileGenerator;
import utils.Output;
import utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class Main {

	static private double time;
	static public double W = 20.0;
    static public double L = 20.0;
    static public double D = 1.2;
    static public double fall = 4.0;
	static public double drivingV = 0.8;
	static final public double Kn = 1.2e5, Kt = 2.4e5;
	static final public double A = 2000, B = 0.08;
	static private int N = 20;
	static final public double TAU = 0.5;
	static private int idCounter = 1;
	private static final double mass = 50;
	private static final double dt = 1e-4;
	private static final double dt2 = 1.0 / 250;



	private static EscapeParticle createRandomParticle() {
		double r = RandomUtils.getRandomDouble(0.5, 0.58) / 2.0;
		double x = RandomUtils.getRandomDouble(r, W - r);
		double y = RandomUtils.getRandomDouble(r + fall, (L + fall) - r);
		return new EscapeParticle(idCounter, x, y, 0, 0, mass, r);
	}

	private static List<VerletParticle> createParticles(int N) {
		List<VerletParticle> list = new ArrayList<>();
		while (idCounter - 1 < N) {
			EscapeParticle p = createRandomParticle();
			boolean areOverlapped = false;
			for (VerletParticle pp : list) {
				if (Particle.getE(p, pp)) {
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

    public static void main(String[] args){
        RandomUtils.setSeed(1234);
		Output output = new Output("out.txt");
		OutputFileGenerator kineticEnergy = new OutputFileGenerator("kinetic.txt");
		OutputFileGenerator caudal = new OutputFileGenerator("caudal.txt");
		List<VerletParticle> particles = createParticles(N);
		Verlet v = new Verlet(particles, dt);
		time = 0;
		int totalCaudal = 0;
		double lastTime = -dt2-1.0;
		double maxPressure = 0.0;
		double energy;

		while (totalCaudal < N) {
			if (lastTime + dt2 < time) {
				output.printState(particles);
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

	private static int getCaudal(List<VerletParticle> particles) {
		int caudal = 0;
		for (VerletParticle particle : particles) {
			if (particle.getOldPosition().y > fall && particle.getPosition().y <= fall) {
				caudal += 1;
			}
		}
		return caudal;
	}

	private static double getSystemKineticEnery(List<VerletParticle> particles) {
		double K = 0;
		for (VerletParticle vp : particles) {
			K += vp.getKineticEnergy();
		}
		return K;
	}

}
