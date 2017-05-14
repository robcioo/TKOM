package app;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import files_loader.FileLoader;
import parser.Parser;
import tokenizer.Tokenizer;

public class Application {
	public static void main(String[] c) {
		try {
			Tokenizer tokenizer = new Tokenizer();
			FileLoader source = new FileLoader(Paths.get(c[0]));
			tokenizer.tokenize(source);
			Parser parser = new Parser(tokenizer);
			parser.parse();
			System.out.println("Operacja przebiegła pomyślnie");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
