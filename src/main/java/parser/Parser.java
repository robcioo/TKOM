package parser;

import java.util.HashMap;
import java.util.concurrent.CancellationException;

import Tree.Node;
import Tree.Tree;
import tokenizer.Token;
import tokenizer.TokenType;
import tokenizer.Tokenizer;

public class Parser {
	private static final String WHILE = "while";
	private static final String RETURN = "return";
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
			if (!token.getType().equals(TokenType.KEY_WORD))
				throw new CancellationException(
						"Spodziewano się " + TokenType.KEY_WORD + ", a jest: " + token.getType());
			if (token.getValue().equals(FUNC)) {
				parseFunc((Node) tree);
			}
		} catch (NullPointerException e) {
			System.out.println("Niespodziewany koniec kodu.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void parseFunc(Node<Token> node) {
		tokenizer.advance();
		Token token = tokenizer.getCurrentWithAdvance();
		accept(TokenType.VAR, token.getType());
		Node<Token> newNode = node.addLChild(token);
		accept(L_BRACKET, tokenizer.getCurrentWithAdvance().getValue());
		if (!tokenizer.getCurrent().getValue().equals(L_BRACKET)) {
			parseFuncHeader(newNode);
		}
		accept(R_BRACKET, tokenizer.getCurrentWithAdvance().getValue());
		accept(L_BR, tokenizer.getCurrentWithAdvance().getValue());
		if (tokenizer.getCurrent().getValue() != R_BR) {
			parseInstructions(node);
		}
		accept(R_BR, tokenizer.getCurrentWithAdvance().getValue());
	}

	private void parseInstructions(Node<Token> node) {
		Node<Token> newNode = node.addLChild(new Token("BODY", TokenType.BODY));
		while (!tokenizer.getCurrent().getValue().equals(R_BR)) {
			newNode = parseInstruction(newNode);
		}

	}

	private Node<Token> parseInstruction(Node<Token> node) {
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
			break;
		}
		return null;
	}

	private void parseCompoundInstruction(Node<Token> node) {
		parseVarDeclaration(node);
		tokenizer.regress();
		parseStandardInstruction(node);
	}

	private void parseReturn(Node<Token> node) {
		// TODO Auto-generated method stub

	}

	private void parseWhile(Node<Token> node) {
		Token token = tokenizer.getCurrentWithAdvance();
		accept(WHILE, token.getValue());
		Node<Token> newNode = node.addLChild(token);
		accept(L_BRACKET, tokenizer.getCurrentWithAdvance().getValue());
		parseCompoundCondition(node);
		accept(R_BRACKET, tokenizer.getCurrentWithAdvance().getValue());
		accept(L_BR, tokenizer.getCurrentWithAdvance().getValue());
		parseInstructions(newNode);
		accept(R_BR, tokenizer.getCurrentWithAdvance().getValue());

	}

	private void parseFor(Node<Token> node) {
		Token token = tokenizer.getCurrentWithAdvance();
		accept(FOR, token.getValue());
		Node<Token> newNode = node.addLChild(token);
		accept(L_BRACKET, tokenizer.getCurrentWithAdvance().getValue());

		parseCompoundCondition(node);
		accept(R_BRACKET, tokenizer.getCurrentWithAdvance().getValue());
		accept(L_BR, tokenizer.getCurrentWithAdvance().getValue());
		parseInstructions(newNode);
		accept(R_BR, tokenizer.getCurrentWithAdvance().getValue());

	}

	private void parseStandardInstruction(Node<Token> node) {
		Token token = tokenizer.getCurrentWithAdvance();
		accept(TokenType.VAR, token.getType());
		accept(TokenType.LOW_PRIORITY_OPERATOR, tokenizer.getCurrent().getType());
		Node<Token> operator = new Node<>(tokenizer.getCurrent());
		operator.addLChild(token);
		operator.addLChild(parseExpressAddExpression());
	}

	private Node<Token> parseExpressAddExpression() {
		Node<Token> newNode = parseAndCondition();
		Node<Token> orNode = new Node<Token>(new Token("|", TokenType.OPERATOR_OR));
		orNode.addLChild(newNode);
		while (tokenizer.getCurrentWithAdvance().getValue().equals("|")) {
			orNode.addLChild(parseAndCondition());
		}
		tokenizer.regress();
		return orNode;
	}
	private Node<Token> parseExpressMulExpression() {
		Node<Token> newNode = parseNotCondition();
		Node<Token> andNode = new Node<Token>(new Token("&", TokenType.OPERATOR_AND));
		andNode.addLChild(newNode);
		while (tokenizer.getCurrentWithAdvance().getValue().equals("&")) {
			andNode.addLChild(parseNotCondition());
		}
		tokenizer.regress();
		return andNode;
	}
	private Node<Token> parseExpressHighExpression() {
		Token token = tokenizer.getCurrentWithAdvance();
		Node<Token> newNode = null;
		if (token.getValue().equals("!")) {
			accept("!", token.getValue());
			newNode = new Node<Token>(token);
			newNode.addLChild(parseNotCondition());
		} else if (token.getValue().equals("(")) {
			accept("(", token.getValue());
			newNode = parseOrCondition();
			accept(")",tokenizer.getCurrentWithAdvance().getValue());
		} else {
			accept(TokenType.VAR, token.getType());
			return new Node<Token>(token);
		}
		return newNode;
	}


	public void parseIf(Node<Token> node) {
		Token token = tokenizer.getCurrentWithAdvance();
		accept(IF, token.getValue());
		Node<Token> newNode = node.addLChild(token);
		accept(L_BRACKET, tokenizer.getCurrentWithAdvance().getValue());
		parseCompoundCondition(newNode);
		accept(R_BRACKET, tokenizer.getCurrentWithAdvance().getValue());
		accept(L_BR, tokenizer.getCurrentWithAdvance().getValue());
		parseInstructions(newNode);
		accept(R_BR, tokenizer.getCurrentWithAdvance().getValue());
		if (ELSE.equals(tokenizer.getCurrent().getValue())) {
			accept(ELSE, tokenizer.getCurrentWithAdvance().getValue());
			accept(L_BR, tokenizer.getCurrentWithAdvance().getValue());
			parseInstructions(newNode);
			accept(R_BR, tokenizer.getCurrentWithAdvance().getValue());
		}
	}

	private void parseCompoundCondition(Node<Token> node) {
		node.addLChild(new Token("condition", TokenType.CONDITION)).addLChild(parseOrCondition());
	}

	private Node<Token> parseOrCondition() {
		Node<Token> newNode = parseAndCondition();
		Node<Token> orNode = new Node<Token>(new Token("|", TokenType.OPERATOR_OR));
		orNode.addLChild(newNode);
		while (tokenizer.getCurrentWithAdvance().getValue().equals("|")) {
			orNode.addLChild(parseAndCondition());
		}
		tokenizer.regress();
		return orNode;
	}

	private Node<Token> parseAndCondition() {
		Node<Token> newNode = parseNotCondition();
		Node<Token> andNode = new Node<Token>(new Token("&", TokenType.OPERATOR_AND));
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
		if (token.getValue().equals("!")) {
			accept("!", token.getValue());
			newNode = new Node<Token>(token);
			newNode.addLChild(parseNotCondition());
		} else if (token.getValue().equals("(")) {
			accept("(", token.getValue());
			newNode = parseOrCondition();
			accept(")",tokenizer.getCurrentWithAdvance().getValue());
		} else {
			accept(TokenType.VAR, token.getType());
			return new Node<Token>(token);
		}
		return newNode;
	}

	private void parseFuncHeader(Node<Token> node) {
		Token token = tokenizer.getCurrent();
		if (!token.getValue().equals(R_BRACKET))
			parseVarDeclaration(node);
		while (!tokenizer.getCurrent().getValue().equals(R_BRACKET)) {
			accept(",", tokenizer.getCurrentWithAdvance().getValue());
			parseVarDeclaration(node);
		}
	}

	private void parseVarDeclaration(Node<Token> node) {
		Token token = tokenizer.getCurrentWithAdvance();
		Node<Token> newNode = null;
		accept(TokenType.DATA_TYPE, token.getType());
		newNode = node.addLChild(token);
		accept(TokenType.VAR, tokenizer.getCurrent().getType());
		newNode = newNode.addLChild(tokenizer.getCurrentWithAdvance());
	}

	private void accept(String expectedToken, String token) {
		if (!expectedToken.equals(token))
			throw new CancellationException("Spodziewano się: " + expectedToken + ", a jest: " + token);
	}

	private void accept(TokenType expectedToken, TokenType token) {
		if (!expectedToken.equals(token))
			throw new CancellationException("Spodziewano się: " + expectedToken + ", a jest: " + token);
	}

	public HashMap<String, Tree<Token>> getFunctions() {
		return functions;
	}

	public void setFunctions(HashMap<String, Tree<Token>> functions) {
		this.functions = functions;
	}
}