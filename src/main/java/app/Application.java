package app;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import files_loader.FileLoader;
import parser.Parser;
import tokenizer.Tokenizer;

public class Application {
	public static void main(String[] c){
		try {
//			ArrayList<String> source=(ArrayList<String>) FileLoader.loadFile(Paths.get(c[0]));
			Tokenizer tokenizer= new Tokenizer();
			System.out.println(tokenizer.tokenize(new FileLoader(Paths.get(c[0]))).toString());
			Parser parser=new Parser(tokenizer);
			parser.parse();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
