package ar.edu.itba.ss.main;

import ar.edu.itba.ss.output.Output;
import ar.edu.itba.ss.output.OutputStat;
import ar.edu.itba.ss.particle.EscapeParticle;
import ar.edu.itba.ss.particle.Particle;
import ar.edu.itba.ss.particle.Integrator;
import ar.edu.itba.ss.particle.VerletParticle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

	static private double time;
	static public double W = 20.0;
    static public double L = 20.0;
    static public double D = 1.2;
    static public double fall = 4.0;
	static public double desiredVelocity = 3.4;
	static final public double Kn = 1.2e5, Kt = 2.4e5;
	static final public double A = 2000, B = 0.08;
	static private int N = 200;
	static final public double TAU = 0.5;
	static private int id_count = 1;
	private static final double mass = 50;
	private static final double dt = 1e-4;
	private static final double dt2 = 1.0 / 250;
    private static final Random random = new Random();

	private static EscapeParticle createRandomParticle() {
		double r = randomNumber(0.5, 0.58) / 2.0;
		double x = randomNumber(r, W - r);
		double y = randomNumber(r + fall, (L + fall) - r);
		return new EscapeParticle(id_count, x, y, 0, 0, mass, r);
	}

    public static double randomNumber(double min, double max){
        return random.nextDouble() * (max - min) + min;
    }

	private static List<VerletParticle> generateParticles(int N) {
		List<VerletParticle> list = new ArrayList<>();
		while (id_count - 1 < N) {
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
				id_count++;
			}
		}
		return list;
	}

    public static void main(String[] args){
        random.setSeed(1234);
		Output output = new Output("out.txt");
		OutputStat cineticEnergy = new OutputStat("cinetic.txt");
		OutputStat caudal = new OutputStat("caudal.txt");
		List<VerletParticle> particles = generateParticles(N);
		Integrator v = new Integrator(particles, dt);
		time = 0;
		int totalCaudal = 0;
		double lastTime = -dt2-1.0;
		double maxPressure = 0.0;
		double energy;

		while (totalCaudal < N) {
			if (lastTime + dt2 < time) {
				output.printState(particles);
				energy = getSystemCineticEnery(particles);
				cineticEnergy.addLine(String.valueOf(energy));
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
		cineticEnergy.writeFile();
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

	private static double getSystemCineticEnery(List<VerletParticle> particles) {
		double K = 0;
		for (VerletParticle vp : particles) {
			K += vp.getKineticEnergy();
		}
		return K;
	}

}
