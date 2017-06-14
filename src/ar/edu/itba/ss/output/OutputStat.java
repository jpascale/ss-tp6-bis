package ar.edu.itba.ss.output;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OutputStat {

	private String fileName;
	List<String> lines = new ArrayList<>();
	
	public OutputStat(String file) {
        this.fileName = file;
    }
	
	public void addLine(String line) {
		lines.add(line);
	}
	
	public void writeFile() {
		try {
		FileWriter fw = new FileWriter(fileName, true);
			for (String line : lines){
				fw.write(line + "\n");
			}
            fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
