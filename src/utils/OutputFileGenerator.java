package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class OutputFileGenerator {
	
	private int frameNumber;
	private String path;
	List<String> lines;
	
	public OutputFileGenerator(String directory, String file) {
		frameNumber = 0;
		this.path = directory + file;
		lines = new LinkedList<String>();
		try {
			Files.createDirectories(Paths.get(directory));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addLine(String line) {
		lines.add(line);
	}
	
	public void writeFile() {
		Path file = Paths.get(path + ".txt");
		frameNumber++;
		try {
			Files.write(file, lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
