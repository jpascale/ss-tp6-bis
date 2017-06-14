package ar.edu.itba.ss.output;

import ar.edu.itba.ss.particle.VerletParticle;
import ar.edu.itba.ss.main.Main;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class Output {

	private String fileName;

	public Output(String fileName) {
		this.fileName = fileName;
		try {
			Files.createDirectories(Paths.get("a/"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void printState(List<? extends VerletParticle> particles) {
		List<String> lines = new LinkedList<>();
		lines.add(String.valueOf(particles.size()));
		lines.add("Comment");
		for (VerletParticle p : particles) {
			lines.add(p.getInfo());
		}
		lines.set(0, String.valueOf(Integer.valueOf(lines.get(0)) + borders(lines)));
		writeFile(lines);
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

    private void writeFile(List<String> lines) {
		try {
            FileWriter fw = new FileWriter(fileName, true);
            for (String line: lines) {
                fw.write(line + "\n");
            }
            fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
