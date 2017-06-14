package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
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
		lines = new LinkedList<>();
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
		try {
		FileWriter fw = new FileWriter(path + ".txt", true);
			for (String line : lines){
				fw.write(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
