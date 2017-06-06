package parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CancellationException;

import files_loader.EndOfFileException;
import parser.statements.ForStatement;
import parser.statements.FuctionCallStatement;
import parser.statements.FunctionStatement;
import parser.statements.IfStatement;
import parser.statements.ObjectFunctionCall;
import parser.statements.ReturnStatement;
import parser.statements.VarDeclaration;
import parser.statements.WhileStatement;
import parser.values.AdditiveExpression;
import parser.values.AndOperator;
import parser.values.ComparisonOperator;
import parser.values.Const;
import parser.values.Decrement;
import parser.values.Division;
import parser.values.Equal;
import parser.values.Equals;
import parser.values.GreaterEqualThan;
import parser.values.GreaterThan;
import parser.values.Increment;
import parser.values.ListConst;
import parser.values.ListIndexOperator;
import parser.values.LowerEqualThan;
import parser.values.LowerThan;
import parser.values.Multiplication;
import parser.values.MultiplicativeExpression;
import parser.values.Not;
import parser.values.NotEquals;
import parser.values.OrOperator;
import parser.values.StringConst;
import parser.values.SubEq;
import parser.values.Subtraction;
import parser.values.Sum;
import parser.values.SumEq;
import parser.values.Value;
import semantics.Scope;
import tokenizer.Pair;
import tokenizer.Token;
import tokenizer.TokenType;
import tokenizer.Tokenizer;
import tokenizer.VarToken;

public class Parser {
	Tokenizer tokenizer;
	private HashMap<String, FunctionStatement> functions;

	public Parser(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
		setFunctions(new HashMap<>());
	}

	public void parse() throws IOException {
		while (tokenizer.getCurrentToken() != null) {
			accept(TokenType.FUNC, tokenizer.getCurrentTokenAndAdvance());
			Token functionNameToken = tokenizer.getCurrentTokenAndAdvance();
			accept(TokenType.VAR, functionNameToken);
			Pair<FunctionStatement, Boolean> result = parseFunc();
			functions.put(((VarToken) functionNameToken).getValue(), result.getLeft());
			if (result.getRight().equals(false))
				break;
		}
	}

	private Pair<FunctionStatement, Boolean> parseFunc() throws IOException {
		accept(TokenType.L_BRACKET, tokenizer.getCurrentTokenAndAdvance());
		FunctionStatement functionStatement = new FunctionStatement();
		if (!tokenizer.getCurrentToken().getType().equals(TokenType.R_BRACKET)) {
			functionStatement.addArguments(parseFuncHeader());
		}
		accept(TokenType.R_BRACKET, tokenizer.getCurrentTokenAndAdvance());
		accept(TokenType.L_BR, tokenizer.getCurrentTokenAndAdvance());
		if (tokenizer.getCurrentToken().getType() != TokenType.R_BR) {
			functionStatement.addInstructions(parseInstructions());
		}
		accept(TokenType.R_BR, tokenizer.getCurrentToken());
		try {
			tokenizer.advance();
		} catch (EndOfFileException e) {
			return new Pair<FunctionStatement, Boolean>(functionStatement, new Boolean(false));
		}
		return new Pair<FunctionStatement, Boolean>(functionStatement, new Boolean(true));
	}

	private ArrayList<Statement> parseInstructions() throws IOException {
		ArrayList<Statement> instructions = new ArrayList<>();
		while (!TokenType.R_BR.equals(tokenizer.getCurrentToken().getType())) {
			instructions.addAll(parseInstruction());
			accept(TokenType.SEMICOLON, tokenizer.getCurrentTokenAndAdvance());
		}
		return instructions;

	}

	public ArrayList<Statement> parseInstruction() throws IOException {
		Token token = tokenizer.getCurrentToken();
		switch (token.getParentType()) {
		case VAR:
			ArrayList<Statement> retArr = new ArrayList<>();
			retArr.add(parseStandardInstruction());
			return retArr;
		case DATA_TYPE:
			return parseCompoundInstruction();
		case KEY_WORD:
			if (TokenType.IF.equals(token.getType())) {
				ArrayList<Statement> arr = new ArrayList<>();
				arr.add(parseIf());
				return arr;
			} else if (TokenType.FOR.equals(token.getType())) {
				ArrayList<Statement> arr = new ArrayList<>();
				arr.add(parseFor());
				return arr;
			} else if (TokenType.WHILE.equals(token.getType())) {
				ArrayList<Statement> arr = new ArrayList<>();
				arr.add(parseWhile());
				return arr;
			} else if (TokenType.RETURN.equals(token.getType())) {
				ArrayList<Statement> arr = new ArrayList<>();
				arr.add(parseReturn());
				return arr;
			}
			break;
		}
		throw new CancellationException("Niespodziewany token: " + token.getType()
				+ " podczas parsowania instrukcji. Linia: " + tokenizer.getLine());
	}

	public ArrayList<Statement> parseCompoundInstruction() throws IOException {
		ArrayList<Statement> instructions = new ArrayList<>();
		instructions.add(parseVarDeclaration());
		if (!tokenizer.getCurrentToken().getType().equals(TokenType.SEMICOLON)) {
			tokenizer.regress();
			instructions.add(parseStandardInstruction());
		}
		return instructions;
	}

	private Statement parseReturn() throws IOException {
		accept(TokenType.RETURN, tokenizer.getCurrentTokenAndAdvance());
		ReturnStatement returnStatement = new ReturnStatement();
		returnStatement.setExpresion(parseAddExpression());
		return returnStatement;
	}

	private Statement parseWhile() throws IOException {
		Token token = tokenizer.getCurrentTokenAndAdvance();
		accept(TokenType.WHILE, token);
		WhileStatement whileStatement = new WhileStatement();
		accept(TokenType.L_BRACKET, tokenizer.getCurrentTokenAndAdvance());
		whileStatement.setCondition(parseCondition());
		accept(TokenType.R_BRACKET, tokenizer.getCurrentTokenAndAdvance());
		accept(TokenType.L_BR, tokenizer.getCurrentTokenAndAdvance());
		whileStatement.addInstructions(parseInstructions());
		accept(TokenType.R_BR, tokenizer.getCurrentTokenAndAdvance());
		return whileStatement;
	}

	private Statement parseFor() throws IOException {
		Token token = tokenizer.getCurrentTokenAndAdvance();
		accept(TokenType.FOR, token);
		ForStatement forStatement = new ForStatement();
		accept(TokenType.L_BRACKET, tokenizer.getCurrentTokenAndAdvance());
		forStatement.addInit(parseCompoundInstruction());
		accept(TokenType.SEMICOLON, tokenizer.getCurrentTokenAndAdvance());
		forStatement.setCondition(parseCondition());
		accept(TokenType.SEMICOLON, tokenizer.getCurrentTokenAndAdvance());
		forStatement.addPostInstruction(parseAddExpression());
		accept(TokenType.R_BRACKET, tokenizer.getCurrentTokenAndAdvance());
		accept(TokenType.L_BR, tokenizer.getCurrentTokenAndAdvance());
		forStatement.addInstructions(parseInstructions());
		accept(TokenType.R_BR, tokenizer.getCurrentTokenAndAdvance());
		return forStatement;
	}

	public Statement parseStandardInstruction() throws IOException {
		Token token = tokenizer.getCurrentTokenAndAdvance();
		if (TokenType.DOT.equals(tokenizer.getCurrentToken().getType())) {
			ObjectFunctionCall functionCall = new ObjectFunctionCall(((VarToken) token).getValue(), functions);
			tokenizer.advance();
			functionCall.addFunctionCall(parseFunctionCall());
			return functionCall;
		} else if (TokenType.L_BRACKET.equals(tokenizer.getCurrentToken().getType())) {
			tokenizer.regress();
			return parseFunctionCall();
		} else {
			// accept(TokenType.VAR,
			// tokenizer.getCurrentTokenAndAdvance().getParentType());
			if (TokenType.LOW_PRIORITY_OPERATOR.equals(tokenizer.getCurrentToken().getParentType())) {
				switch (tokenizer.getCurrentTokenAndAdvance().getType()) {
				case EQUAL:
					return new Equal(((VarToken) token).getValue(), parseAddExpression());
				case SUB_EQ:
					return new SubEq(((VarToken) token).getValue(), parseAddExpression());
				case SUM_EQ:
					return new SumEq(((VarToken) token).getValue(), parseAddExpression());
				}
				throw new CancellationException("Nieobslugiwany operator przypisania. Linia: " + tokenizer.getLine());
			} else
				throw new CancellationException("Nieobslugiwany operator przypisania. Linia: " + tokenizer.getLine());
		}

	}

	private Expression parseList() throws IOException {
		ListConst listConst = new ListConst();
		if (TokenType.R_BR != tokenizer.getCurrentToken().getType())
			listConst.addArgument(parseAddExpression());
		while (TokenType.R_BR != tokenizer.getCurrentToken().getType()) {
			accept(TokenType.COMMA, tokenizer.getCurrentTokenAndAdvance());
			listConst.addArgument(parseAddExpression());
		}
		accept(TokenType.R_BR, tokenizer.getCurrentTokenAndAdvance());
		return listConst;
	}

	private Expression parseAddExpression() throws IOException {
		Expression arg = parseMulExpression();
		Token token = tokenizer.getCurrentToken();
		if (!TokenType.MEDIUM_PRIORITY_OPERATOR.equals(token.getParentType()))
			return arg;
		tokenizer.advance();

		AdditiveExpression operation;
		if (TokenType.SUM.equals(token.getType())) {
			operation = new Sum();
		} else {
			operation = new Subtraction();
		}
		operation.addArgument(arg);
		operation.addArgument(parseAddExpression());
		return operation;
	}

	private Expression parseMulExpression() throws IOException {
		Expression arg = parseHighExpression();
		if (!TokenType.HIGH_PRIORITY_OPERATOR.equals(tokenizer.getCurrentToken().getParentType()))
			return arg;
		MultiplicativeExpression operation;
		if (TokenType.MUL.equals(tokenizer.getCurrentTokenAndAdvance().getType())) {
			operation = new Multiplication();
		} else {
			operation = new Division();
		}
		operation.addArgument(arg);
		operation.addArgument(parseMulExpression());
		return operation;
	}

	private Expression parseHighExpression() throws IOException {
		Token token = tokenizer.getCurrentTokenAndAdvance();
		if (token.getType().equals(TokenType.L_BRACKET)) {
			Expression expression = parseAddExpression();
			accept(TokenType.R_BRACKET, tokenizer.getCurrentTokenAndAdvance());
			return expression;
		} else if (TokenType.DEC.equals(token.getType()) || TokenType.INC.equals(token.getType())) {
			accept(TokenType.VAR, tokenizer.getCurrentToken());
			Expression expression;
			if (TokenType.DEC.equals(token.getType())) {
				expression = new Decrement(((VarToken) tokenizer.getCurrentTokenAndAdvance()).getValue());
			} else {
				expression = new Increment(((VarToken) tokenizer.getCurrentTokenAndAdvance()).getValue());
			}
			return expression;
		} else if (TokenType.VAR.equals(token.getType())) {
			tokenizer.regress();
			return parseVarExpression();
		} else if (TokenType.CONST.equals(token.getType())) {
			return new Const(((VarToken) token).getValue());
		} else if (TokenType.STRING_CONST.equals(token.getType())) {
			return new StringConst(((VarToken) token).getValue());
		} else if (TokenType.L_BR.equals(token.getType())) {
			return parseList();
		} else if (TokenType.SUBTRACTION.equals(token.getType())) {
			token = tokenizer.getCurrentTokenAndAdvance();
			Const contValue = new Const(((VarToken) token).getValue());
			contValue.changeSign();
			return contValue;
		} else
			throw new CancellationException(
					"Nie obsugiwany token w wyrazeniu" + token.getType() + ". Linia " + tokenizer.getLine());
	}

	private Expression parseListIndexOperator(VarToken token) throws IOException {
		ListIndexOperator operator = new ListIndexOperator(token.getValue());
		operator.setIndex(parseAddExpression());
		accept(TokenType.R_INDEX_OPERATOR, tokenizer.getCurrentTokenAndAdvance());
		return operator;
	}

	private Expression parseVarExpression() throws IOException {
		accept(TokenType.VAR, tokenizer.getCurrentToken());
		Token token = tokenizer.getCurrentTokenAndAdvance();
		if (TokenType.DOT.equals(tokenizer.getCurrentToken().getType())) {
			ObjectFunctionCall functionCall = new ObjectFunctionCall(((VarToken) token).getValue(), functions);
			tokenizer.advance();
			functionCall.addFunctionCall(parseFunctionCall());
			return functionCall;
		} else if (TokenType.L_INDEX_OPERATOR.equals(tokenizer.getCurrentToken().getType())) {
			tokenizer.advance();
			return parseListIndexOperator((VarToken) token);
		} else if (TokenType.L_BRACKET.equals(tokenizer.getCurrentToken().getType())) {
			tokenizer.regress();
			return parseFunctionCall();
		}
		return new Value(((VarToken) token).getValue());
	}

	private FuctionCallStatement parseFunctionCall() throws IOException {
		accept(TokenType.VAR, tokenizer.getCurrentToken());
		FuctionCallStatement oper = new FuctionCallStatement(
				((VarToken) tokenizer.getCurrentTokenAndAdvance()).getValue(), functions);
		accept(TokenType.L_BRACKET, tokenizer.getCurrentTokenAndAdvance());
		if (!TokenType.R_BRACKET.equals(tokenizer.getCurrentToken().getType())) {
			oper.addArgument(parseAddExpression());
		}
		while (!TokenType.R_BRACKET.equals(tokenizer.getCurrentToken().getType())) {
			accept(TokenType.COMMA, tokenizer.getCurrentTokenAndAdvance());
			oper.addArgument(parseAddExpression());
		}
		accept(TokenType.R_BRACKET, tokenizer.getCurrentTokenAndAdvance());
		return oper;
	}

	public Statement parseIf() throws IOException {
		Token token = tokenizer.getCurrentTokenAndAdvance();
		accept(TokenType.IF, token);
		IfStatement ifStatement = new IfStatement();
		accept(TokenType.L_BRACKET, tokenizer.getCurrentTokenAndAdvance());
		ifStatement.setCondition(parseCondition());
		accept(TokenType.R_BRACKET, tokenizer.getCurrentTokenAndAdvance());
		accept(TokenType.L_BR, tokenizer.getCurrentTokenAndAdvance());
		ifStatement.addIfInstructions(parseInstructions());
		accept(TokenType.R_BR, tokenizer.getCurrentTokenAndAdvance());
		if (TokenType.ELSE.equals(tokenizer.getCurrentToken().getType())) {
			accept(TokenType.ELSE, tokenizer.getCurrentTokenAndAdvance());
			accept(TokenType.L_BR, tokenizer.getCurrentTokenAndAdvance());
			ifStatement.addElseInstructions(parseInstructions());
			accept(TokenType.R_BR, tokenizer.getCurrentTokenAndAdvance());
		}
		return ifStatement;
	}

	private Expression parseCondition() throws IOException {
		return parseCompareCondition();
	}

	private Expression parseCompareCondition() throws IOException {
		Expression arg = parseOrCondition();
		if (!tokenizer.getCurrentToken().getParentType().equals(TokenType.COMPARISON_OPERATOR))
			return arg;
		Token tok = tokenizer.getCurrentTokenAndAdvance();
		ComparisonOperator operator;
		if (TokenType.EQUALS.equals(tok.getType())) {
			operator = new Equals();
		} else if (TokenType.NEQUALS.equals(tok.getType())) {
			operator = new NotEquals();
		} else if (TokenType.LT.equals(tok.getType())) {
			operator = new LowerThan();
		} else if (TokenType.LET.equals(tok.getType())) {
			operator = new LowerEqualThan();
		} else if (TokenType.GT.equals(tok.getType())) {
			operator = new GreaterThan();
		} else {
			operator = new GreaterEqualThan();
		}
		operator.addArgument(arg);
		operator.addArgument(parseOrCondition());
		return operator;
	}

	private Expression parseOrCondition() throws IOException {
		Expression arg = parseAndCondition();
		if (!tokenizer.getCurrentToken().getType().equals(TokenType.OR))
			return arg;
		tokenizer.advance();
		OrOperator or = new OrOperator();
		or.addArgument(arg);
		or.addArgument(parseOrCondition());
		return or;
	}

	private Expression parseAndCondition() throws IOException {
		Expression arg = parseNotCondition();
		if (!tokenizer.getCurrentToken().getType().equals(TokenType.AND))
			return arg;
		tokenizer.advance();
		AndOperator and = new AndOperator();
		and.addArgument(arg);
		and.addArgument(parseAndCondition());
		return and;
	}

	private Expression parseNotCondition() throws IOException {
		Token token = tokenizer.getCurrentTokenAndAdvance();
		if (TokenType.NOT.equals(token.getType())) {
			return new Not(parseNotCondition());
		} else if (token.getType().equals(TokenType.L_BRACKET)) {
			Expression ex = parseCompareCondition();
			accept(TokenType.R_BRACKET, tokenizer.getCurrentTokenAndAdvance());
			return ex;
		} else if (TokenType.VAR.equals(token.getType())) {
			tokenizer.regress();
			return parseVarExpression();
		} else if (TokenType.CONST.equals(token.getType())) {
			return new Const(((VarToken) token).getValue());
		} else if (TokenType.STRING_CONST.equals(token.getType())) {
			return new StringConst(((VarToken) token).getValue());
		} else
			throw new CancellationException("Niespodziewany token: " + token.getType()
					+ " podczas parsowania instrukcji. Linia: " + tokenizer.getLine());
	}

	private ArrayList<VarDeclaration> parseFuncHeader() throws IOException {
		ArrayList<VarDeclaration> header = new ArrayList<>();
		Token token = tokenizer.getCurrentToken();
		if (!TokenType.R_BRACKET.equals(token.getType()))
			header.add(parseVarDeclaration());
		while (!TokenType.R_BRACKET.equals(tokenizer.getCurrentToken().getType())) {
			accept(TokenType.COMMA, tokenizer.getCurrentTokenAndAdvance());
			header.add(parseVarDeclaration());
		}
		return header;
	}

	private VarDeclaration parseVarDeclaration() throws IOException {
		Token dataType = tokenizer.getCurrentTokenAndAdvance();
		accept(TokenType.DATA_TYPE, dataType.getParentType());
		accept(TokenType.VAR, tokenizer.getCurrentToken());
		Token name = tokenizer.getCurrentTokenAndAdvance();
		return new VarDeclaration(dataType.getType(), ((VarToken) name).getValue());
	}

	private void accept(TokenType expectedToken, Token token) {
		if (!expectedToken.equals(token.getType()))
			throw new CancellationException("Spodziewano się: " + expectedToken + ", a jest: " + token.getType()
					+ ". Linia: " + tokenizer.getLine());
	}

	private void accept(TokenType expectedToken, TokenType type) {
		if (!expectedToken.equals(type))
			throw new CancellationException(
					"Spodziewano się: " + expectedToken + ", a jest: " + type + ". Linia: " + tokenizer.getLine());
	}

	public HashMap<String, FunctionStatement> getFunctions() {
		return functions;
	}

	public void setFunctions(HashMap<String, FunctionStatement> functions) {
		this.functions = functions;
	}

	public Object execute(String func, ArrayList<Object> args) {
		FunctionStatement functionStatement = functions.get(func);
		return functionStatement.execute(new Scope(), args);

	}
}