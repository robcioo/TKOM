package app;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;

import files_loader.SourceLoader;
import parser.Parser;
import semantics.SemanthickAnalyzer;
import tokenizer.Tokenizer;

public class Application {
	public static void main(String[] c) {
		try {
			Tokenizer tokenizer = new Tokenizer(new SourceLoader(Paths.get(c[0])));
			Parser parser = new Parser(tokenizer);
			parser.parse();
			parser.execute("main", new ArrayList<>());
//			System.out.println("Operacja przebiegła pomyślnie");
		} catch (CancellationException e) {
			System.out.println(e.getMessage());
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
