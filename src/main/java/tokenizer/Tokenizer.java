package tokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.CancellationException;

import javax.swing.event.ListSelectionEvent;

import files_loader.SourceLoader;

public class Tokenizer {

	private static final int SIZE = 2;
	private SourceLoader fileLoader;
	private ArrayList<Token> tokens;
	private int bufferIter;

	public Tokenizer(SourceLoader fileLoader) {
		this.fileLoader = fileLoader;
		tokens = new ArrayList<>();
	}

	public Token getCurrentToken() {
		if (tokens.size() == 0)
			advance();
		if (tokens.size() == 1)
			return tokens.get(0);
		return tokens.get(bufferIter);
	}

	public Token getCurrentTokenAndAdvance() throws IOException {
		Token c = getCurrentToken();
		advance();
		return c;
	}

	public void advance() {
		if (tokens.size() != 0 && bufferIter + 1 < tokens.size()) {
			++bufferIter;
			return;
		}
		addToken(tokenizeOneToken());
	}

	public void regress() {
		--bufferIter;
	}

	public Token tokenizeOneToken() {
		try {
			if (Character.isWhitespace(fileLoader.getCurrentChar()))
				while (Character.isWhitespace(fileLoader.getCurrentChar()))
					fileLoader.advance();
			if (fileLoader.getCurrentChar() == ' ') {

			}
			TokenizerState state = getNextState(fileLoader.getCurrentChar());
			switch (state) {
			case NUMBER:
				return tokenizeNumber();
			case SPECIAL:
				return parseSpecial();
			case WORD:
				return parseWord();
			case STRING:
				return praseString();
			default:
				return null;
			}
		} catch (IOException e) {
			throw new CancellationException("Błąd podczas wczytywania pliku.");
		}
	}

	private Token praseString() throws IOException {
		StringBuilder sb = new StringBuilder();
		while (true) {
			if (sb.length() > 1 && fileLoader.getCurrentChar() == '"') {
				sb.append(fileLoader.getCurrentCharAndAdvance());
				return new VarToken(sb.toString(), TokenType.STRING_CONST, TokenPriority.LOW, TokenType.STRING_CONST,
						fileLoader.getLine());
			} else {
				sb.append(fileLoader.getCurrentCharAndAdvance());
			}
		}
	}

	private Token parseWord() throws IOException {
		StringBuilder sb = new StringBuilder();
		while (true) {
			if (isAlphaNumeric(fileLoader.getCurrentChar())) {
				sb.append(fileLoader.getCurrentCharAndAdvance());
			} else {
				TokenType tokType = getTokenForString(sb.toString());
				if (tokType != null) {
					return new Token(getParentTokenType(tokType), getTokenPriority(tokType), tokType,
							fileLoader.getLine());
				} else {
					return new VarToken(sb.toString(), TokenType.VAR, TokenPriority.LOW, TokenType.VAR,
							fileLoader.getLine());
				}
			}
		}
	}

	private TokenType getParentTokenType(TokenType tokType) {
		for (TokenType tokenType : Arrays.asList(HIGHEST_PRIORITY_OPERATOR))
			if (tokenType.equals(tokType))
				return TokenType.HIGHEST_PRIORITY_OPERATOR;
		for (TokenType tokenType : Arrays.asList(HIGH_PRIORITY_OPERATOR))
			if (tokenType.equals(tokType))
				return TokenType.HIGH_PRIORITY_OPERATOR;
		for (TokenType tokenType : Arrays.asList(MEDIUM_PRIORITY_OPERATOR))
			if (tokenType.equals(tokType))
				return TokenType.MEDIUM_PRIORITY_OPERATOR;
		for (TokenType tokenType : Arrays.asList(LOW_PRIORITY_OPERATOR))
			if (tokenType.equals(tokType))
				return TokenType.LOW_PRIORITY_OPERATOR;
		for (TokenType tokenType : Arrays.asList(LOGICAL_OPERATOR))
			if (tokenType.equals(tokType))
				return TokenType.LOGICAL_OPERATOR;
		for (TokenType tokenType : Arrays.asList(COMPARISON_OPERATOR))
			if (tokenType.equals(tokType))
				return TokenType.COMPARISON_OPERATOR;
		for (TokenType tokenType : Arrays.asList(DATA_TYPES))
			if (tokenType.equals(tokType))
				return TokenType.DATA_TYPE;
		for (TokenType tokenType : Arrays.asList(KEY_WORDS))
			if (tokenType.equals(tokType))
				return TokenType.KEY_WORD;
		for (TokenType tokenType : Arrays.asList(OPERATOR))
			if (tokenType.equals(tokType))
				return TokenType.OPERATOR;
		return null;
	}

	private TokenPriority getTokenPriority(TokenType tokType) {
		return PRIORITY.get(tokType);
	}

	private Token parseSpecial() throws IOException {
		ArrayList<TokenType> tok;
		StringBuilder sb = new StringBuilder();
		while (true) {
			sb.append(fileLoader.getCurrentCharAndAdvance());
			tok = getTokensStartWith(sb.toString());
			if (tok.size() == 0) {
				sb.deleteCharAt(sb.length() - 1);
				TokenType tokType = getTokenEquals(sb.toString());
				if (tokType == null)
					throw new CancellationException("Błąd gramatyczny podczas wczytywania operatora.");
				else {
					fileLoader.regress();
					return new Token(getParentTokenType(tokType), getTokenPriority(tokType), tokType,
							fileLoader.getLine());
				}
			} else if (tok.size() == 1 && tok.get(0).equals(TOKENS.get(sb.toString()))) {
				return new Token(getParentTokenType(TOKENS.get(sb.toString())),
						getTokenPriority(TOKENS.get(sb.toString())), TOKENS.get(sb.toString()), fileLoader.getLine());
			}
		}

	}

	private Token tokenizeNumber() throws IOException {
		StringBuilder sb = new StringBuilder();
		boolean flag = false;
		if (fileLoader.getCurrentChar() == '-')
			sb.append(fileLoader.getCurrentCharAndAdvance());
		while (true) {
			if (isDigit(fileLoader.getCurrentChar())) {
				sb.append(fileLoader.getCurrentCharAndAdvance());
			} else if (fileLoader.getCurrentChar() == '.' && !flag) {
				flag = true;
				sb.append(fileLoader.getCurrentCharAndAdvance());
			} else if (fileLoader.getCurrentChar() == '.' && flag) {
				throw new CancellationException("Niedozwolony znak '.'. Kolumna: " + fileLoader.getColumn());
			} else if (isLetter(fileLoader.getCurrentChar()) || fileLoader.getCurrentChar() == CHAR_)
				throw new CancellationException("Niedozwolony znak. Kolumna: " + fileLoader.getColumn());
			else {
				return new VarToken(sb.toString(), TokenType.CONST, TokenPriority.LOW, TokenType.CONST,
						fileLoader.getLine());
			}
		}
	}

	private TokenizerState getNextState(char c) throws IOException {
		if (isDigit(c)) {
			return TokenizerState.NUMBER;
		}
		if (isLetter(c))
			return TokenizerState.WORD;
		else if (isToken(new Character(c).toString()))
			return TokenizerState.SPECIAL;
		else if (c == '"')
			return TokenizerState.STRING;
		else
			throw new CancellationException(
					"Niedozwolony znak. Linia: " + fileLoader.getLine() + " Kolumna: " + fileLoader.getColumn());
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

	private TokenType getTokenForString(String token) {
		if (TOKENS.containsKey(token))
			return TOKENS.get(token);
		return null;
	}

	private ArrayList<TokenType> getTokensStartWith(String token) {
		ArrayList<TokenType> tokens = new ArrayList<>();
		for (String key : TOKENS.keySet()) {
			if (key.startsWith(token))
				tokens.add(TOKENS.get(key));
		}
		return tokens;
	}

	private TokenType getTokenEquals(String token) {
		return TOKENS.get(token);
	}

	public void addToken(Token token) {
		if (tokens.size() >= SIZE)
			tokens.remove(0);
		if (bufferIter < tokens.size())
			++bufferIter;
		tokens.add(token);
	}

	private boolean isToken(String token) {
		for (int i = 0; i < SPECIAL_TOKENS.length; ++i) {
			if (SPECIAL_TOKENS[i].startsWith(token))
				return true;
		}
		return false;
	}

	private static final String[] SPECIAL_TOKENS = { "(", ")", ",", ".", "{", "}", ";", "!", "=", "|", "&", "==", "!=",
			"<", ">", "<=", ">=", "+", "++", "-", "--", "*", "/", "[", "]", "+=", "-=" };
	private static final HashMap<String, TokenType> TOKENS;
	private static final HashMap<TokenType, TokenPriority> PRIORITY;
	static {
		TOKENS = new HashMap<String, TokenType>();
		TOKENS.put("(", TokenType.L_BRACKET);
		TOKENS.put(")", TokenType.R_BRACKET);
		TOKENS.put(",", TokenType.COMMA);
		TOKENS.put(".", TokenType.DOT);
		TOKENS.put("{", TokenType.L_BR);
		TOKENS.put("}", TokenType.R_BR);
		TOKENS.put(";", TokenType.SEMICOLON);
		TOKENS.put("!", TokenType.NOT);
		TOKENS.put("=", TokenType.EQUAL);
		TOKENS.put("|", TokenType.OR);
		TOKENS.put("&", TokenType.AND);
		TOKENS.put("==", TokenType.EQUALS);
		TOKENS.put("!=", TokenType.NEQUALS);
		TOKENS.put("<", TokenType.LT);
		TOKENS.put(">", TokenType.GT);
		TOKENS.put("<=", TokenType.LET);
		TOKENS.put(">=", TokenType.GET);
		TOKENS.put("+", TokenType.SUM);
		TOKENS.put("++", TokenType.INC);
		TOKENS.put("-", TokenType.SUBTRACTION);
		TOKENS.put("--", TokenType.DEC);
		TOKENS.put("*", TokenType.MUL);
		TOKENS.put("/", TokenType.DIVIDE);
		TOKENS.put("[", TokenType.L_INDEX_OPERATOR);
		TOKENS.put("]", TokenType.R_INDEX_OPERATOR);
		TOKENS.put("long", TokenType.LONG);
		TOKENS.put("bool", TokenType.BOOL);
		TOKENS.put("double", TokenType.DOUBLE);
		TOKENS.put("string", TokenType.STRING);
		TOKENS.put("list", TokenType.LIST);
		TOKENS.put("+=", TokenType.SUM_EQ);
		TOKENS.put("-=", TokenType.SUB_EQ);
		TOKENS.put("if", TokenType.IF);
		TOKENS.put("else", TokenType.ELSE);
		TOKENS.put("for", TokenType.FOR);
		TOKENS.put("while", TokenType.WHILE);
		TOKENS.put("func", TokenType.FUNC);
		TOKENS.put("return", TokenType.RETURN);

		PRIORITY = new HashMap<TokenType, TokenPriority>();
		PRIORITY.put(TokenType.L_BRACKET, TokenPriority.HIGHEST);
		PRIORITY.put(TokenType.R_BRACKET, TokenPriority.HIGHEST);
		PRIORITY.put(TokenType.DOT, TokenPriority.HIGHEST);
		PRIORITY.put(TokenType.NOT, TokenPriority.HIGH);
		PRIORITY.put(TokenType.EQUAL, TokenPriority.LOW);
		PRIORITY.put(TokenType.OR, TokenPriority.LOW);
		PRIORITY.put(TokenType.AND, TokenPriority.MED);
		PRIORITY.put(TokenType.EQUALS, TokenPriority.LOW);
		PRIORITY.put(TokenType.NEQUALS, TokenPriority.LOW);
		PRIORITY.put(TokenType.LT, TokenPriority.LOW);
		PRIORITY.put(TokenType.GT, TokenPriority.LOW);
		PRIORITY.put(TokenType.LET, TokenPriority.LOW);
		PRIORITY.put(TokenType.GET, TokenPriority.LOW);
		PRIORITY.put(TokenType.SUM, TokenPriority.MED);
		PRIORITY.put(TokenType.INC, TokenPriority.HIGHEST);
		PRIORITY.put(TokenType.SUBTRACTION, TokenPriority.MED);
		PRIORITY.put(TokenType.DEC, TokenPriority.HIGHEST);
		PRIORITY.put(TokenType.MUL, TokenPriority.HIGH);
		PRIORITY.put(TokenType.DIVIDE, TokenPriority.HIGH);
		PRIORITY.put(TokenType.L_INDEX_OPERATOR, TokenPriority.HIGHEST);
		PRIORITY.put(TokenType.R_INDEX_OPERATOR, TokenPriority.HIGHEST);
		PRIORITY.put(TokenType.SUM_EQ, TokenPriority.LOW);
		PRIORITY.put(TokenType.SUB_EQ, TokenPriority.LOW);
	}

	public static final TokenType[] HIGHEST_PRIORITY_OPERATOR = { TokenType.L_BRACKET, TokenType.R_BRACKET,
			TokenType.INC, TokenType.DEC };
	public static final TokenType[] HIGH_PRIORITY_OPERATOR = { TokenType.MUL, TokenType.DIVIDE };
	public static final TokenType[] MEDIUM_PRIORITY_OPERATOR = { TokenType.SUM, TokenType.SUBTRACTION };
	private static final TokenType[] LOW_PRIORITY_OPERATOR = { TokenType.EQUAL, TokenType.SUB_EQ, TokenType.SUM_EQ };
	private static final TokenType[] LOGICAL_OPERATOR = { TokenType.NOT, TokenType.AND, TokenType.OR };
	private static final TokenType[] COMPARISON_OPERATOR = { TokenType.EQUALS, TokenType.NEQUALS, TokenType.LT,
			TokenType.GT, TokenType.GET, TokenType.LET };
	private static final TokenType[] OPERATOR = { TokenType.DOT, TokenType.L_BR, TokenType.R_BR,
			TokenType.L_INDEX_OPERATOR, TokenType.R_INDEX_OPERATOR, TokenType.SEMICOLON, TokenType.COMMA };

	private static final TokenType[] DATA_TYPES = { TokenType.BOOL, TokenType.LIST, TokenType.LONG, TokenType.STRING,
			TokenType.DOUBLE };
	private static final TokenType[] KEY_WORDS = { TokenType.FUNC, TokenType.IF, TokenType.FOR, TokenType.WHILE,
			TokenType.ELSE, TokenType.RETURN };
	private static final char CHAR_a = 'a';
	private static final char CHAR_z = 'z';
	private static final char CHAR_A = 'A';
	private static final char CHAR_Z = 'Z';
	private static final char DIGIT_0 = '0';
	private static final char DIGIT_9 = '9';
	private static final char CHAR_ = '_';

	public long getLine() {
		return fileLoader.getLine();
	}

}
