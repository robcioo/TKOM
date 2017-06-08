package app;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;

import files_loader.SourceLoader;
import parser.Parser;
import tokenizer.Tokenizer;

public class JList {
	public static Object execute(String source, String func, ArrayList<Object> args) throws IOException ,CancellationException{
		return execute(new SourceLoader(source), func, args);
	}
	public static Object execute(Path source, String func, ArrayList<Object> args) throws IOException,CancellationException {
		return execute(new SourceLoader(source), func, args);
	}

	private static Object execute(SourceLoader sourceLoader, String func, ArrayList<Object> args) throws IOException, CancellationException {
		ArrayList<Object> newArgs=new ArrayList<>();
		for(Object arg: args)
			newArgs.add(new Wrapper(arg));
		Tokenizer tokenizer = new Tokenizer(sourceLoader);
		Parser parser = new Parser(tokenizer);
		parser.parse();
		return parser.execute(func, newArgs);
	}
}
