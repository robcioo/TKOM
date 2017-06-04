package parser.statements;

import java.util.ArrayList;

import parser.Expression;
import parser.Statement;
import semantics.Scope;

public class IfStatement implements Statement {
	private Expression condition;
	private ArrayList<Statement> ifInsturctions;
	private ArrayList<Statement> elseInsturctions;

	public IfStatement() {
		ifInsturctions = new ArrayList<>();
		elseInsturctions = new ArrayList<>();
	}

	public void addIfInstruction(Statement instruction) {
		ifInsturctions.add(instruction);

	}

	public void addIfInstructions(ArrayList<Statement> parseInstructions) {
		ifInsturctions.addAll(parseInstructions);
	}

	public void addElseInstruction(Statement instruction) {
		elseInsturctions.add(instruction);

	}

	public void addElseInstructions(ArrayList<Statement> parseInstructions) {
		elseInsturctions.addAll(parseInstructions);
	}

	public void setCondition(Expression condition) {
		this.condition = condition;
	}

	@Override
	public Object execute(Scope scope, ArrayList<Object> args) {
		Scope currScope = new Scope(scope);
		if (((Boolean) condition.evaluate(currScope))) {
			return FunctionStatement.runInstructions(ifInsturctions, currScope);
		} else {
			return FunctionStatement.runInstructions(elseInsturctions, currScope);
		}
	}
}