package files_loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;

public class SourceLoader {

	private static final int SIZE = 2;
	private BufferedReader bufferedReader;
	private String source;
	private int line = 0;
	private int column = 0;
	private int sourceStringIterator = 0;
	private int bufferIter = 1;
	private boolean lock = false;
	private ArrayList<Character> buffer;

	public SourceLoader(Path path) throws IOException {
		bufferedReader = Files.newBufferedReader(path);
		buffer = new ArrayList<>();
	}

	public SourceLoader(String source) throws IOException {
		this.source = source;
		buffer = new ArrayList<>();
	}

	public char advance() throws IOException {
		if(lock)
			throw new EndOfFileException();
		if (bufferIter == 0) {
			++bufferIter;
			return buffer.get(bufferIter);
		}
		Character c = null;
		if (bufferedReader != null) {
			int character=bufferedReader.read();
			if (character==-1){
				lock = true;
				c=new Character(' ');
			}
			else{
				c = new Character((char) character);
			}
		} else {
			if (source.length() > sourceStringIterator)
				c = new Character(source.charAt(sourceStringIterator++));
			else {
				c = new Character(' ');
				lock = true;
			}
		}
		if (buffer.size() >= SIZE)
			buffer.remove(0);
		buffer.add(c);
		if (c.equals('\n')) {
			++line;
			column = 0;
		} else
			++column;
		return c;
	}

	public char getCurrentChar() throws IOException {
		if (buffer.size() == 0)
			advance();
		if (buffer.size() == 1)
			return buffer.get(0);
		return buffer.get(bufferIter);
	}

	public char getCurrentCharAndAdvance() throws IOException {
		char c = getCurrentChar();
		advance();
		return c;
	}

	public void regress() {
		if (bufferIter > 0)
			--bufferIter;
	}

	// public char getNextChar() throws IOException {
	// if (buffer.size() <2)
	// return getNextCharAndAdvance();
	// return buffer.get(1);
	// }
	//
	// public char getNextCharAndAdvance() throws IOException {
	// Character c = null;
	// if (bufferedReader != null)
	// c = new Character((char) bufferedReader.read());
	// else
	// c = new Character(source.charAt(iterator++));
	// if (buffer.size() >= SIZE)
	// buffer.remove(0);
	// buffer.add(c);
	// if (c.equals('\n')) {
	// ++line;
	// column = 0;
	// } else
	// ++column;
	// return c;
	// }

	public int getColumn() {
		return column;
	}

	public int getLine() {
		return line;
	}
}
