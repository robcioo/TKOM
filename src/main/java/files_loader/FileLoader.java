package files_loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileLoader {
	private BufferedReader bufferedReader;
	
	public FileLoader(Path path) throws IOException {
		 bufferedReader = Files.newBufferedReader(path);
	}
	
	public String readLine() throws IOException{
		return bufferedReader.readLine();
	}
}
