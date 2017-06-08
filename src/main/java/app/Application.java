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
			if (c.length != 2)
				throw new CancellationException(
						"Niepoprwana ilość argumentów programu. Spodziwano się 2 argumentów: Ścieżka do pliku z kodem, nazwa funkcji rozpoczynającej program");
			Tokenizer tokenizer = new Tokenizer(new SourceLoader(Paths.get(c[0])));
			Parser parser = new Parser(tokenizer);
			parser.parse();
			Object result = parser.execute(c[1], new ArrayList<>());
			if (result != null)
				System.out.println(result.toString());
		} catch (CancellationException e) {
			System.out.println(e.getMessage());
			return;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return;
		} catch (Exception e) {
			System.out.println("Wystapil krytyczny błąd. Proszę skontaktować się z autorem programu.");
			return;
		}
	}
}
		