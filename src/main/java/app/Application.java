package app;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;

import files_loader.SourceLoader;
import parser.Parser;
import tokenizer.Tokenizer;

public class Application {
	public static void main(String[] c) {
		try {
			Tokenizer tokenizer = new Tokenizer(new SourceLoader(Paths.get("/home/robeek/Desktop/TKOM/test2.txt")));
			Parser parser = new Parser(tokenizer);
			parser.parse();
			Object result = parser.execute("main", new ArrayList<>());
			if (result != null)
				System.out.println(result.toString());
		} catch (CancellationException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
