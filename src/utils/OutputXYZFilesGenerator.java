package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import model.Particle;
import model.VerletParticle;
import run.EscapeRunner;

public class OutputXYZFilesGenerator {

	private int frameNumber;
	private String path;

	public OutputXYZFilesGenerator(String directory, String file) {
		frameNumber = 0;
		this.path = directory + file;
		try {
			Files.createDirectories(Paths.get(directory));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void printState(List<? extends VerletParticle> particles) {
		List<String> lines = new LinkedList<String>();
		lines.add(String.valueOf(particles.size()));
		lines.add("ParticleId xCoordinate yCoordinate xDisplacement yDisplacement Radius R G B Transparency Selection");
		for (VerletParticle p : particles) {
			lines.add(getInfo(p, getColorByPresure(p), 0, 0));
		}
		lines.set(0, String.valueOf(Integer.valueOf(lines.get(0)) + addBorderParticles(lines)));
		writeFile(lines);
	}

	private String getColorByPresure(VerletParticle p) {
		double relativePresure = p.getPressure()/400;
		return relativePresure + " 0 " + (1 - relativePresure); 
	}

	private int addBorderParticles(List<String> lines) {
		int counter = 0;
		for (double i = 0; i * 0.02 <= EscapeRunner.L; i++) {
			lines.add("10000 0 " + (i * 0.02 + EscapeRunner.fall) + " 0 0 0.02 0 1 0 0 0");
			lines.add("10000 " + EscapeRunner.W + " " + (i * 0.02 + EscapeRunner.fall) + " 0 0 0.02 0 1 0 0 0");
			counter += 2;
		}
		for (int i = 0; i * 0.02 <= (EscapeRunner.W - EscapeRunner.D) / 2; i++) {
			lines.add("10000 " + i * 0.02 + " " + EscapeRunner.fall + " 0 0 0.02 0 1 0 0 0");
			lines.add("10000 " + (EscapeRunner.W - i * 0.02) + " " + EscapeRunner.fall + " 0 0 0.02 0 1 0 0 0");
			counter += 2;
		}
		return counter;
	}

	private String getInfo(Particle p, String color, double transparency, int selection) {
		return p.getId() + " " + p.getX() + " " + p.getY() + " " + p.getXVelocity() + " " + p.getYVelocity() + " "
				+ p.getRadius() + " " + "1 0 0" + " " + transparency + " " + selection;
	}

	private void writeFile(List<String> lines) {
		Path file = Paths.get(path + frameNumber + ".xyz");
		frameNumber++;
		try {
			Files.write(file, lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
