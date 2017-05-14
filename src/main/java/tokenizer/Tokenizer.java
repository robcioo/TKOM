package tokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CancellationException;

import javax.swing.event.ListSelectionEvent;

import files_loader.FileLoader;

public class Tokenizer {

	// private static final String[] TOKENS = { ",", ".", "{", "}", ";", "=",
	// "[", "]" };
	private static final String[] TOKENS = { "(", ")", ",", ".", "{", "}", ";", "!", "=", "|", "&", "==", "!=", "<",
			">", "<=", ">=", "+", "++", "-", "--", "*", "/", "[", "]", "+=", "-=" };
	public static final String[] HIGHEST_PRIORITY_OPERATOR = { "(", ")", "++", "--" };
	public static final String[] HIGH_PRIORITY_OPERATOR = { "*", "/" };
	public static final String[] MEDIUM_PRIORITY_OPERATOR = { "+", "-" };
	private static final String[] LOW_PRIORITY_OPERATOR = { "+=", "-=", "=" };
	private static final String[] LOGICAL_OPERATOR_H = { "!" };
	private static final String[] LOGICAL_OPERATOR_M = { "&" };
	private static final String[] LOGICAL_OPERATOR_L = { "|" };
	private static final String[] COMPARISON_OPERATOR = { "==", "!=", "<", ">", "<=", ">=" };
	private static final String[] OPERATOR = { ".", "{", "}", "[", "]", ";", "," };

	private static final String[] DATA_TYPES = { "bool", "long", "double", "string", "list" };
	private static final String[] KEY_WORDS = { "func", "if", "for", "while", "else", "return" };
	private static final char CHAR_a = 'a';
	private static final char CHAR_z = 'z';
	private static final char CHAR_A = 'A';
	private static final char CHAR_Z = 'Z';
	private static final char DIGIT_0 = '0';
	private static final char DIGIT_9 = '9';
	private static final char CHAR_ = '_';

	private ArrayList<Token> tokens;
	private int iterator;
	private long line = 0;

	public ArrayList<Token> tokenizeString(String source) {
		tokens = new ArrayList<>();
		iterator = 0;
		String line;
		line = source;
		tokens.addAll(tokenize(line));
		return tokens;
	}

	public ArrayList<Token> tokenize(FileLoader source) {
		tokens = new ArrayList<>();
		iterator = 0;
		String line;
		try {
			line = source.readLine();
			while (line != null) {
				++this.line;
				tokens.addAll(tokenize(line));
				line = source.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tokens;
	}

	private ArrayList<Token> tokenize(String line) {
		ArrayList<Token> tokens = new ArrayList<>();
		String newLine = line + "";
		TokenizerState state = TokenizerState.START;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < newLine.length(); ++i) {
			switch (state) {
			case START:
				state = getNextState(newLine.charAt(i), i);
				if (state != TokenizerState.START)
					--i;
				break;
			case NUMBER:
				state = parseNumber(newLine.charAt(i), sb, i, tokens);
				if (state == TokenizerState.START)
					--i;
				break;
			case SPECIAL:
				i = parseSpecial(i, newLine, sb, tokens);
				state = TokenizerState.START;
				break;
			case WORD:
				state = parseWord(newLine.charAt(i), sb, tokens);
				if (state == TokenizerState.START)
					--i;
				break;
			case STRING:
				state = praseString(newLine.charAt(i), sb, tokens);
				break;
			}

		}
		if (state != TokenizerState.START) {
			if (state == TokenizerState.STRING)
				throw new CancellationException("Brak '\"' kończącego stałą typu string. Kolumna: " + line.length());
		}
		return tokens;

	}

	private TokenizerState praseString(char c, StringBuilder sb, ArrayList<Token> tokens) {
		sb.append(c);
		if (sb.length() > 1 && c == '"') {
			tokens.add(new Token(sb.toString(), TokenType.STRING_CONST,this.line));
			sb.setLength(0);
			return TokenizerState.START;
		}
		return TokenizerState.STRING;
	}

	private TokenizerState parseWord(char c, StringBuilder sb, ArrayList<Token> tokens) {
		if (isAlphaNumeric(c)) {
			sb.append(c);
			return TokenizerState.WORD;
		} else {
			if (isDataType(sb.toString())) {
				tokens.add(new Token(sb.toString(), TokenType.DATA_TYPE,this.line));
			} else if (isKeyWord(sb.toString())) {
				tokens.add(new Token(sb.toString(), TokenType.KEY_WORD,this.line));
			} else {
				tokens.add(new Token(sb.toString(), TokenType.VAR,this.line));
			}
			sb.setLength(0);
			return TokenizerState.START;
		}
	}

	private int parseSpecial(int i, String newLine, StringBuilder sb, ArrayList<Token> tokens) {
		ArrayList<String> tok = null;
		for (; i < newLine.length(); ++i) {
			sb.append(newLine.charAt(i));
			tok = getTokensStartWith(sb.toString());
			if (tok.size() == 0) {
				sb.deleteCharAt(sb.length() - 1);
				if (getTokenEquals(sb.toString()) == null)
					throw new CancellationException("Błąd gramatyczny podczas wczytywania operatora. Kolumna: " + i);
				else {
					tokens.add(new Token(sb.toString(), getTokenType(sb.toString()),this.line));
					sb.setLength(0);
					--i;
					return i;
				}
			} else if (tok.size() == 1 && tok.get(0).equals(sb.toString())) {
				tokens.add(new Token(sb.toString(), getTokenType(sb.toString()),this.line));
				sb.setLength(0);
				return i;
			}
		}
		return i;
	}

	private TokenType getTokenType(String token) {
		if (Arrays.asList(HIGHEST_PRIORITY_OPERATOR).contains(token))
			return TokenType.HIGHEST_PRIORITY_OPERATOR;
		if (Arrays.asList(HIGH_PRIORITY_OPERATOR).contains(token))
			return TokenType.HIGH_PRIORITY_OPERATOR;
		if (Arrays.asList(MEDIUM_PRIORITY_OPERATOR).contains(token))
			return TokenType.MEDIUM_PRIORITY_OPERATOR;
		if (Arrays.asList(LOW_PRIORITY_OPERATOR).contains(token))
			return TokenType.LOW_PRIORITY_OPERATOR;
		if (Arrays.asList(LOGICAL_OPERATOR_H).contains(token))
			return TokenType.OPERATOR_NOT;
		if (Arrays.asList(LOGICAL_OPERATOR_M).contains(token))
			return TokenType.OPERATOR_AND;
		if (Arrays.asList(LOGICAL_OPERATOR_L).contains(token))
			return TokenType.OPERATOR_OR;
		if (Arrays.asList(COMPARISON_OPERATOR).contains(token))
			return TokenType.COMPARISON_OPERATOR;
		if (Arrays.asList(OPERATOR).contains(token))
			return TokenType.OPERATOR;
		throw new CancellationException("Brak definicji operatora dla tokena: " + token);
	}

	private TokenizerState parseNumber(char c, StringBuilder sb, int i, ArrayList<Token> tokens) {
		if (isDigit(c))
			sb.append(c);
		else if (isLetter(c) || c == CHAR_)
			throw new CancellationException("Niedozwolony znak. Kolumna: " + i);
		else {
			tokens.add(new Token(sb.toString(), TokenType.CONST,this.line));
			sb.setLength(0);

			return TokenizerState.START;
		}
		return TokenizerState.NUMBER;
	}

	private TokenizerState getNextState(char c, int i) {
		if (Character.isWhitespace(c))
			return TokenizerState.START;
		else if (isDigit(c))
			return TokenizerState.NUMBER;
		else if (isLetter(c))
			return TokenizerState.WORD;
		else if (isToken(new Character(c).toString()))
			return TokenizerState.SPECIAL;
		else if (c == '"')
			return TokenizerState.STRING;
		else
			throw new CancellationException("Niedozwolony znak. Kolumna: " + i);
	}

	private boolean isLetter(char c) {
		return (c >= CHAR_a && c <= CHAR_z) || (c >= CHAR_A && c <= CHAR_Z);
	}

	private boolean isDigit(char c) {
		return (c >= DIGIT_0 && c <= DIGIT_9);
	}

	private boolean isAlphaNumeric(char c) {
		return isDigit(c) || isLetter(c) || c == CHAR_;
	}

	private boolean isToken(String token) {
		for (int i = 0; i < TOKENS.length; ++i) {
			if (TOKENS[i].startsWith(token))
				return true;
		}
		return false;
	}

	private boolean isKeyWord(String token) {
		for (int i = 0; i < KEY_WORDS.length; ++i) {
			if (KEY_WORDS[i].equals(token))
				return true;
		}
		return false;
	}

	private boolean isDataType(String token) {
		for (int i = 0; i < DATA_TYPES.length; ++i) {
			if (DATA_TYPES[i].equals(token))
				return true;
		}
		return false;
	}

	private ArrayList<String> getTokensStartWith(String token) {
		ArrayList<String> tokens = new ArrayList<>();
		for (int i = 0; i < TOKENS.length; ++i) {
			if (TOKENS[i].startsWith(token))
				tokens.add(TOKENS[i]);
		}
		return tokens;
	}

	private String getTokenEquals(String token) {
		for (int i = 0; i < TOKENS.length; ++i) {
			if (TOKENS[i].equals(token))
				return TOKENS[i];
		}
		return null;
	}

	public Token getCurrentWithAdvance() {
		if (iterator < tokens.size()) {
			return tokens.get(iterator++);
		}
		return null;
	}

	public Token getCurrent() {
		if (iterator < tokens.size()) {
			return tokens.get(iterator);
		}
		return null;
	}

	public Token getNext() {
		if (iterator < tokens.size())
			return tokens.get(++iterator);
		return null;
	}

	public void advance() {
		++iterator;
	}

	public void regress() {
		--iterator;
	}

}
// private static final String[] MEDIUM_PRIORITY_OPERATOR = { "(", ")", ",",
// ".", "{", "}", ";", "!", "=", "|", "&", "==", "!=", "<", ">",
// "<=", ">=", "+", "++", "-", "--", "*", "/", "[", "]", "+=", "-=" };
