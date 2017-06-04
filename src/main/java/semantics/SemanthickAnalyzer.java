package semantics;

import java.util.HashMap;
import java.util.concurrent.CancellationException;

import Tree.Node;
import Tree.Tree;
import parser.TreeNode;
import tokenizer.Token;
import tokenizer.TokenType;

public class SemanthickAnalyzer {
	private HashMap<String, Tree<TreeNode>> functions;

	public SemanthickAnalyzer(HashMap<String, Tree<Token>> functions) {
		this.functions = functions;
	}

	public void checkSematics() {
		if (!functions.containsKey("main"))
			throw new CancellationException("Function 'main' is required");
		for (String func : functions.keySet()) {
			Node<Token> rootNode = functions.get(func);
			checkFuncSematics(rootNode);
		}
	}

	private void checkFuncSematics(Node<Token> rootNode) {
		Scope varBlock = new Scope();
		for (int i = 1; i < rootNode.getChildren().size(); ++i) {
			Node<Token> t = rootNode.getChildren().get(i);
			switch (t.getData().getType()) {
			case BODY:
				checkBody(t, varBlock);
				break;
			case DATA_TYPE:
				varBlock.putVar(t.getChildren().get(0).getData().getValue(), t.getData().getValue());
				break;
			}
		}
	}

	private void checkBody(Node<Token> t, Scope varBlock) {
		for (Node<Token> child : t.getChildren())
			checkInstr(child, varBlock);
	}

	private void checkCondition(Node<Token> child, Scope varBlock) {
		for (Node<Token> c : child.getChildren()) {
			if (c.getData().getType().equals(TokenType.OPERATOR_AND)
					|| c.getData().getType().equals(TokenType.OPERATOR_OR)
					|| c.getData().getType().equals(TokenType.OPERATOR_NOT)
					|| c.getData().getType().equals(TokenType.COMPARISON_OPERATOR)) {
				checkCondition(c, varBlock);
			} else if (c.getData().getType().equals(TokenType.VAR)) {
				varBlock.notContainsWithException(c.getData().getValue());
			}
		}
	}

	private void checkInstr(Node<Token> child, Scope varBlock) {
		if (child.getData().getType().equals(TokenType.DATA_TYPE))
			varBlock.putVar(child.getChildren().get(0).getData().getValue(), child.getData().getValue());
		else if (child.getData().getType().equals(TokenType.VAR)) {
			if (child.getChildren().get(0).getData().getType().equals(TokenType.VAR)) {// wywołanie
				varBlock.notContainsWithException(child.getData().getValue());
			} else if (child.getChildren().get(0).getData().getType().equals(TokenType.FUNCTION_CALL)) {
				if (!functions.containsKey(child.getData().getValue()))
					throw new CancellationException("Brak definicji funkcji: " + child.getData().getValue());
			}
		} else if (child.getData().getValue().equals("if")) {
			checkIf(child, varBlock);
		} else if (child.getData().getValue().equals("while")) {
			checkWhile(child, varBlock);
		} else if (child.getData().getValue().equals("for")) {
			checkFor(child, varBlock);
		} else if (child.getData().getType().equals(TokenType.LOW_PRIORITY_OPERATOR)) {
			checkExpression(child, varBlock);
		}
	}

	private void checkExpression(Node<Token> child, Scope varBlock) {
		for (Node<Token> c : child.getChildren()) {
			if (c.getData().getType().equals(TokenType.LOW_PRIORITY_OPERATOR)
					|| c.getData().getType().equals(TokenType.MEDIUM_PRIORITY_OPERATOR)
					|| c.getData().getType().equals(TokenType.HIGH_PRIORITY_OPERATOR)
					|| c.getData().getType().equals(TokenType.HIGHEST_PRIORITY_OPERATOR)) {
				checkExpression(c, varBlock);
			} else if (c.getData().getType().equals(TokenType.VAR)) {
				varBlock.notContainsWithException(c.getData().getValue());
			}
		}
	}

	private void checkFor(Node<Token> child, Scope varBlock) {
		Scope newVarBlock = new Scope(varBlock);
		checkInstr(child.getChildren().get(0), varBlock);
		checkInstr(child.getChildren().get(1), varBlock);
		checkCondition(child.getChildren().get(2).getChildren().get(0), varBlock);
		checkInstr(child.getChildren().get(3), varBlock);
		checkBody(child.getChildren().get(4), newVarBlock);
	}

	private void checkWhile(Node<Token> child, Scope varBlock) {
		checkCondition(child.getChildren().get(0).getChildren().get(0), varBlock);
		Scope newVarBlock = new Scope(varBlock);
		checkBody(child.getChildren().get(1), newVarBlock);
	}

	private void checkIf(Node<Token> child, Scope varBlock) {
		checkCondition(child.getChildren().get(0).getChildren().get(0), varBlock);
		Scope newVarBlock = new Scope(varBlock);
		checkBody(child.getChildren().get(1), newVarBlock);
		if (child.getChildren().get(0).getChildren().size() > 2) {
			newVarBlock = new Scope(varBlock);
			checkCondition(child.getChildren().get(0).getChildren().get(2), newVarBlock);
		}
	}
}
