package parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CancellationException;

import Tree.Node;
import Tree.Tree;
import tokenizer.Token;
import tokenizer.TokenType;
import tokenizer.Tokenizer;

public class Parser {
	private static final String DOT = ".";
	private static final String NOT = "!";
	private static final String BODY = "BODY";
	private static final String RETURN = "return";
	private static final String FUNCTION_CALL = "functionCall";
	private static final String COMMA = ",";
	private static final String SEMICOLON = ";";
	private static final String WHILE = "while";
	private static final String ELSE = "else";
	private static final String FOR = "for";
	private static final String IF = "if";
	private static final String L_BR = "{";
	private static final String R_BR = "}";
	private static final String R_BRACKET = ")";
	private static final String L_BRACKET = "(";
	public static final String FUNC = "func";
	Tokenizer tokenizer;
	private HashMap<String, Tree<Token>> functions;

	public Parser(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
		setFunctions(new HashMap<>());
	}

	public void parse() {
		Token token = tokenizer.getCurrent();
		Tree<Token> tree = new Tree<Token>(token);
		Token functionNameToken = tokenizer.getNext();
		getFunctions().put(functionNameToken.getValue(), tree);
		tokenizer.regress();

		try {
			while (tokenizer.getCurrent() != null) {
				accept(TokenType.KEY_WORD, token);
				if (token.getValue().equals(FUNC)) {
					parseFunc((Node) tree);
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.out.println("Niespodziewany koniec kodu.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void parseFunc(Node<Token> node) {
		tokenizer.advance();
		Token token = tokenizer.getCurrentWithAdvance();
		accept(TokenType.VAR, token);
		Node<Token> newNode = node.addLChild(token);
		accept(L_BRACKET, tokenizer.getCurrentWithAdvance());
		if (!tokenizer.getCurrent().getValue().equals(L_BRACKET)) {
			parseFuncHeader(newNode);
		}
		accept(R_BRACKET, tokenizer.getCurrentWithAdvance());
		accept(L_BR, tokenizer.getCurrentWithAdvance());
		if (tokenizer.getCurrent().getValue() != R_BR) {
			parseInstructions(node);
		}
		accept(R_BR, tokenizer.getCurrentWithAdvance());
	}

	private void parseInstructions(Node<Token> node) {
		Node<Token> newNode = node.addLChild(new Token(BODY, TokenType.BODY, 0));
		while (!tokenizer.getCurrent().getValue().equals(R_BR)) {
			parseInstruction(newNode);
			accept(SEMICOLON, tokenizer.getCurrentWithAdvance());
		}

	}

	public void parseInstruction(Node<Token> node) {
		Token token = tokenizer.getCurrent();
		switch (token.getType()) {
		case VAR:
			parseStandardInstruction(node);
			break;
		case DATA_TYPE:
			parseCompoundInstruction(node);
			break;
		case KEY_WORD:
			if (IF.equals(token.getValue())) {
				parseIf(node);
			} else if (FOR.equals(token.getValue())) {
				parseFor(node);
			} else if (WHILE.equals(token.getValue()))
				parseWhile(node);
			else if (RETURN.equals(token.getValue()))
				parseReturn(node);
			break;

		default:
			throw new CancellationException("Niespodziewany token: " + token.getValue()
					+ " podczas parsowania instrukcji. Linia: " + token.getLine());
		}
	}

	public void parseCompoundInstruction(Node<Token> node) {
		parseVarDeclaration(node);
		tokenizer.regress();
		parseStandardInstruction(node);
	}

	private void parseReturn(Node<Token> node) {
		accept(RETURN, tokenizer.getCurrent());
		node.addLChild(tokenizer.getCurrentWithAdvance());
		parseAddExpression();
	}

	private void parseWhile(Node<Token> node) {
		Token token = tokenizer.getCurrentWithAdvance();
		accept(WHILE, token);
		Node<Token> newNode = node.addLChild(token);
		accept(L_BRACKET, tokenizer.getCurrentWithAdvance());
		parseCompoundCondition(node);
		accept(R_BRACKET, tokenizer.getCurrentWithAdvance());
		accept(L_BR, tokenizer.getCurrentWithAdvance());
		parseInstructions(newNode);
		accept(R_BR, tokenizer.getCurrentWithAdvance());

	}

	private void parseFor(Node<Token> node) {
		Token token = tokenizer.getCurrentWithAdvance();
		accept(FOR, token);
		Node<Token> newNode = node.addLChild(token);
		accept(L_BRACKET, tokenizer.getCurrentWithAdvance());
		parseCompoundInstruction(newNode);
		accept(SEMICOLON, tokenizer.getCurrentWithAdvance());
		parseCompoundCondition(newNode);
		accept(SEMICOLON, tokenizer.getCurrentWithAdvance());
		newNode.addLChild(parseAddExpression());
		accept(R_BRACKET, tokenizer.getCurrentWithAdvance());
		accept(L_BR, tokenizer.getCurrentWithAdvance());
		parseInstructions(newNode);
		accept(R_BR, tokenizer.getCurrentWithAdvance());

	}

	public void parseStandardInstruction(Node<Token> node) {
		if (DOT.equals(tokenizer.getNext().getValue())) {
			tokenizer.regress();
			Node<Token> newNode = new Node<Token>(tokenizer.getCurrentWithAdvance());
			node.addLChild(parseFunctionCall());
		} else if (L_BRACKET.equals(tokenizer.getCurrent().getValue())) {
			tokenizer.regress();
			tokenizer.regress();
			node.addLChild(parseFunctionCall());
		} else {
			tokenizer.regress();
			Token token = tokenizer.getCurrentWithAdvance();
			accept(TokenType.VAR, token);
			if (TokenType.LOW_PRIORITY_OPERATOR.equals(tokenizer.getCurrent().getType())) {
				accept(TokenType.LOW_PRIORITY_OPERATOR, tokenizer.getCurrent());
				Node<Token> operator = new Node<>(tokenizer.getCurrentWithAdvance());
				operator.addLChild(token);
				operator.addLChild(parseAddExpression());
				node.addLChild(operator);
			}
		}

	}

	private Node<Token> parseAddExpression() {
		Node<Token> newNode = parseMulExpression();
		if (!tokenizer.getCurrent().getType().equals(TokenType.MEDIUM_PRIORITY_OPERATOR))
			return newNode;
		Node<Token> addNode = new Node<Token>(tokenizer.getCurrent());
		addNode.addLChild(newNode);
		while (Arrays.asList(Tokenizer.MEDIUM_PRIORITY_OPERATOR).contains(tokenizer.getCurrent().getValue())) {
			if (!tokenizer.getCurrent().getValue().equals(addNode.getData().getValue())) {
				newNode = new Node<Token>(tokenizer.getCurrent());
				newNode.addLChild(addNode);
				addNode = newNode;
			}
			tokenizer.advance();
			addNode.addLChild(parseMulExpression());
		}
		return addNode;
	}

	private Node<Token> parseMulExpression() {
		Node<Token> newNode = parseHighExpression();
		if (!tokenizer.getCurrent().getType().equals(TokenType.HIGH_PRIORITY_OPERATOR))
			return newNode;
		Node<Token> andNode = new Node<Token>(tokenizer.getCurrent());
		andNode.addLChild(newNode);
		while (Arrays.asList(Tokenizer.HIGH_PRIORITY_OPERATOR).contains(tokenizer.getCurrent().getValue())) {
			if (!tokenizer.getCurrent().getValue().equals(andNode.getData().getValue())) {
				newNode = new Node<Token>(tokenizer.getCurrent());
				newNode.addLChild(andNode);
				andNode = newNode;
			}
			tokenizer.advance();
			andNode.addLChild(parseHighExpression());
		}
		return andNode;
	}

	private Node<Token> parseHighExpression() {
		Token token = tokenizer.getCurrentWithAdvance();
		Node<Token> newNode = null;
		if (token.getValue().equals("(")) {
			accept(L_BRACKET, token);
			newNode = parseAddExpression();
			accept(R_BRACKET, tokenizer.getCurrentWithAdvance());
		} else if (token.getValue().equals("--") || token.getValue().equals("++")) {
			newNode = new Node<Token>(token);
			newNode.addLChild(parseHighExpression());
		} else if (TokenType.VAR.equals(token.getType())) {
			tokenizer.regress();
			return parseVarExpression();
		} else if (TokenType.CONST.equals(token.getType())) {
			return new Node<Token>(token);
		} else if (TokenType.STRING_CONST.equals(token.getType())) {
			return new Node<Token>(token);
		}
		return newNode;
	}

	private Node<Token> parseVarExpression() {
		accept(TokenType.VAR, tokenizer.getCurrent());
		Node<Token> newNode = new Node<Token>(tokenizer.getCurrentWithAdvance());
		if (DOT.equals(tokenizer.getCurrent().getValue())) {
			newNode.addLChild(parseFunctionCall());
		}
		return newNode;
	}

	private Node<Token> parseFunctionCall() {
		tokenizer.advance();
		accept(TokenType.VAR, tokenizer.getCurrent());
		Node<Token> newNode = new Node<Token>(
				new Token(tokenizer.getCurrentWithAdvance().getValue(), TokenType.FUNCTION_CALL, 0));
		accept(L_BRACKET, tokenizer.getCurrentWithAdvance());
		if (!R_BRACKET.equals(tokenizer.getCurrent().getValue())) {
			parseStandardInstruction(newNode);
		}
		while (!R_BRACKET.equals(tokenizer.getCurrent().getValue())) {
			accept(COMMA, tokenizer.getCurrentWithAdvance());
			newNode.addLChild(parseAddExpression());
		}
		accept(R_BRACKET, tokenizer.getCurrentWithAdvance());
		return newNode;
	}

	public void parseIf(Node<Token> node) {
		Token token = tokenizer.getCurrentWithAdvance();
		accept(IF, token);
		Node<Token> newNode = node.addLChild(token);
		accept(L_BRACKET, tokenizer.getCurrentWithAdvance());
		parseCompoundCondition(newNode);
		accept(R_BRACKET, tokenizer.getCurrentWithAdvance());
		accept(L_BR, tokenizer.getCurrentWithAdvance());
		parseInstructions(newNode);
		accept(R_BR, tokenizer.getCurrentWithAdvance());
		if (ELSE.equals(tokenizer.getCurrent().getValue())) {
			accept(ELSE, tokenizer.getCurrentWithAdvance());
			accept(L_BR, tokenizer.getCurrentWithAdvance());
			parseInstructions(newNode);
			accept(R_BR, tokenizer.getCurrentWithAdvance());
		}
	}

	private void parseCompoundCondition(Node<Token> node) {
		node.addLChild(new Token("condition", TokenType.CONDITION, 0)).addLChild(parseCompareCondition());
	}

	private Node<Token> parseCompareCondition() {
		Node<Token> newNode = parseOrCondition();
		if (!tokenizer.getCurrent().getType().equals(TokenType.COMPARISON_OPERATOR))
			return newNode;
		Node<Token> orNode = new Node<Token>(tokenizer.getCurrent());
		orNode.addLChild(newNode);
		while (tokenizer.getCurrentWithAdvance().getType().equals(TokenType.COMPARISON_OPERATOR)) {
			orNode.addLChild(parseOrCondition());
		}
		tokenizer.regress();
		return orNode;
	}

	private Node<Token> parseOrCondition() {
		Node<Token> newNode = parseAndCondition();
		if (!tokenizer.getCurrent().getType().equals(TokenType.OPERATOR_OR))
			return newNode;
		Node<Token> orNode = new Node<Token>(new Token("|", TokenType.OPERATOR_OR, 0));
		orNode.addLChild(newNode);
		while (tokenizer.getCurrentWithAdvance().getValue().equals("|")) {
			orNode.addLChild(parseAndCondition());
		}
		tokenizer.regress();
		return orNode;
	}

	private Node<Token> parseAndCondition() {
		Node<Token> newNode = parseNotCondition();
		if (!tokenizer.getCurrent().getType().equals(TokenType.OPERATOR_AND))
			return newNode;
		Node<Token> andNode = new Node<Token>(new Token("&", TokenType.OPERATOR_AND, 0));
		andNode.addLChild(newNode);
		while (tokenizer.getCurrentWithAdvance().getValue().equals("&")) {
			andNode.addLChild(parseNotCondition());
		}
		tokenizer.regress();
		return andNode;
	}

	private Node<Token> parseNotCondition() {
		Token token = tokenizer.getCurrentWithAdvance();
		Node<Token> newNode = null;
		if (token.getValue().equals(NOT)) {
			accept(NOT, token);
			newNode = new Node<Token>(token);
			newNode.addLChild(parseNotCondition());
		} else if (token.getValue().equals(L_BRACKET)) {
			accept(L_BRACKET, token);
			newNode = parseOrCondition();
			accept(R_BRACKET, tokenizer.getCurrentWithAdvance());
		} else if (TokenType.VAR.equals(token.getType())) {
			tokenizer.regress();
			return parseVarExpression();
		} else if (TokenType.CONST.equals(token.getType())) {
			return new Node<Token>(token);
		} else if (TokenType.STRING_CONST.equals(token.getType())) {
			return new Node<Token>(token);
		} else
			throw new CancellationException(
					"Niespodziewany token: " + token.getValue() + ". Linia: " + token.getLine());
		return newNode;
	}

	private void parseFuncHeader(Node<Token> node) {
		Token token = tokenizer.getCurrent();
		if (!token.getValue().equals(R_BRACKET))
			parseVarDeclaration(node);
		while (!tokenizer.getCurrent().getValue().equals(R_BRACKET)) {
			accept(COMMA, tokenizer.getCurrentWithAdvance());
			parseVarDeclaration(node);
		}
	}

	private void parseVarDeclaration(Node<Token> node) {
		Token token = tokenizer.getCurrentWithAdvance();
		accept(TokenType.DATA_TYPE, token);
		Node<Token> newNode = node.addLChild(token);
		accept(TokenType.VAR, tokenizer.getCurrent());
		newNode = newNode.addLChild(tokenizer.getCurrentWithAdvance());
	}

	private void accept(String expectedToken, Token token) {
		if (!expectedToken.equals(token.getValue()))
			throw new CancellationException("Spodziewano się: " + expectedToken + ", a jest: " + token.getValue()
					+ ". Linia: " + token.getLine());
	}

	private void accept(TokenType expectedToken, Token token) {
		if (!expectedToken.equals(token.getType()))
			throw new CancellationException("Spodziewano się: " + expectedToken + ", a jest: " + token.getType()
					+ ". Linia: " + token.getLine());
	}

	public HashMap<String, Tree<Token>> getFunctions() {
		return functions;
	}

	public void setFunctions(HashMap<String, Tree<Token>> functions) {
		this.functions = functions;
	}
}