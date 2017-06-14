package ar.edu.itba.ss.main;

import ar.edu.itba.ss.output.Output;
import ar.edu.itba.ss.output.OutputStat;
import ar.edu.itba.ss.particle.FallParticle;
import ar.edu.itba.ss.particle.Particle;
import ar.edu.itba.ss.particle.Integrator;
import ar.edu.itba.ss.particle.EscapingParticle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

	private static int N = 20;
	public static double desiredVelocity = 3.4;
	private static final double dt = 1e-4;
	private static final double dt2 = 1.0 / 250;

	private static double time;
	public static double W = 20.0;
    public static double L = 20.0;
    public static double D = 1.2;
	public static final double Kn = 1.2e5, Kt = 2.4e5;
	public static final double A = 2000, B = 0.08;

    public static double floorDistance = 4.0;

	final public static double TAU = 0.5;
	private static int id_count = 1;
	private static final double mass = 50;


    private static final Random random = new Random();

	private static FallParticle createRandomParticle() {
		double r = randomNumber(0.5, 0.58) / 2.0;
		double x = randomNumber(r, W - r);
		double y = randomNumber(r + floorDistance, (L + floorDistance) - r);
		return new FallParticle(id_count, x, y, 0, 0, mass, r);
	}

    public static double randomNumber(double min, double max){
        return random.nextDouble() * (max - min) + min;
    }

	private static List<EscapingParticle> generateParticles(int N) {
		List<EscapingParticle> list = new ArrayList<>();
		while (id_count - 1 < N) {
			FallParticle p = createRandomParticle();
			boolean isValid = false;
			for (EscapingParticle pp : list) {
				if (Particle.getE(p, pp)) {
					isValid = true;
					break;
				}
			}
			if (!isValid) {
				list.add(p);
				id_count++;
			}
		}
		return list;
	}

    public static void main(String[] args){
        random.setSeed(1234);
		Output output = new Output("out.txt");
		OutputStat caudal = new OutputStat("caudal.txt");
		List<EscapingParticle> particles = generateParticles(N);
		Integrator v = new Integrator(particles, dt);
		time = 0;
		int totalCaudal = 0;
		double lastTime = - dt2 - 1.0;
		double maxPressure = 0.0;

		while (totalCaudal < N) {
			if (lastTime + dt2 < time) {
				output.printState(particles);
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
		System.out.println("Time elapsed: " + time);
		caudal.writeFile();
	}

	private static int getCaudal(List<EscapingParticle> particles) {
		int caudal = 0;
		for (EscapingParticle particle : particles) {
			if (particle.getOldPosition().y > floorDistance && particle.getPosition().y <= floorDistance) {
				caudal += 1;
			}
		}
		return caudal;
	}

}
