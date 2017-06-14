package utils;

import model.Particle;
import model.VerletParticle;
import run.Main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

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
		List<String> lines = new LinkedList<>();
		lines.add(String.valueOf(particles.size()));
		lines.add("ParticleId xCoordinate yCoordinate Radius R G B"); //TODO:Cambiar
		for (VerletParticle p : particles) {
			lines.add(getInfo(p, getColorByPresure(p), 0, 0));
		}
		lines.set(0, String.valueOf(Integer.valueOf(lines.get(0)) + borders(lines)/*+ addBorderParticles(lines)*/));
		writeFile(lines);
	}

	private String getColorByPresure(VerletParticle p) {
		double relativePresure = p.getPressure() / 400;
		return relativePresure + " 0 " + (1 - relativePresure); 
	}

	private int borders(List<String> lines){
        lines.add("-1 0.0 4.0 0.3 1 0 0");
        lines.add("-1 20.0 4.0 0.3 1 0 0");
        lines.add("-1 5.0 4.0 0.3 1 0 0");
        lines.add("-1 15.0 4.0 0.3 1 0 0");
        lines.add("-1 0.0 0.0 0.3 1 0 0");
        lines.add("-1 20.0 0.0 0.3 1 0 0");
        lines.add("-1 0.0 24.0 0.3 1 0 0");
        lines.add("-1 20.0 24.0 0.3 1 0 0");

        lines.add("-1 " + String.valueOf(Main.W / 2.0 - Main.D / 2.0  - 0.3) + " 4.0 0.3 1 0 0");
        lines.add("-1 " + String.valueOf(Main.W / 2.0 + Main.D / 2.0  + 0.3) + " 4.0 0.3 1 0 0");
        return 10;
    }

	private int addBorderParticles(List<String> lines) {
		int counter = 0;
		for (double i = 0; i * 0.02 <= Main.L; i++) {
			lines.add("10000 0 " + (i * 0.02 + Main.fall) + " 0 0 0.02 0 1 0 0 0");
			lines.add("10000 " + Main.W + " " + (i * 0.02 + Main.fall) + " 0 0 0.02 0 1 0 0 0");
			counter += 2;
		}
		for (int i = 0; i * 0.02 <= (Main.W - Main.D) / 2; i++) {
			lines.add("10000 " + i * 0.02 + " " + Main.fall + " 0 0 0.02 0 1 0 0 0");
			lines.add("10000 " + (Main.W - i * 0.02) + " " + Main.fall + " 0 0 0.02 0 1 0 0 0");
			counter += 2;
		}
		return counter;
	}

	private String getInfo(Particle p, String color, double transparency, int selection) {
		return p.getId() + " " + p.getX() + " " + p.getY() + " " + p.getRadius() + " " + "255 255 255";
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
