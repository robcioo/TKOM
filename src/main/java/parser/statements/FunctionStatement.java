package parser.statements;

import java.util.ArrayList;
import java.util.concurrent.CancellationException;

import parser.Expression;
import parser.Statement;
import parser.Var;
import parser.VarType;
import semantics.Scope;
import tokenizer.TokenType;

public class FunctionStatement implements Statement {
	private ArrayList<VarDeclaration> arguments;
	private ArrayList<Statement> instructions;

	public FunctionStatement() {
		setArguments(new ArrayList<>());
		setInstructions(new ArrayList<>());
	}

	public void addArguments(ArrayList<VarDeclaration> args) {
		getArguments().addAll(args);
	}

	public void addInstruction(Statement instruction) {
		getInstructions().add(instruction);
	}

	public void addInstructions(ArrayList<Statement> parseInstructions) {
		getInstructions().addAll(parseInstructions);
	}

	public ArrayList<VarDeclaration> getArguments() {
		return arguments;
	}

	public void setArguments(ArrayList<VarDeclaration> arguments) {
		this.arguments = arguments;
	}

	public ArrayList<Statement> getInstructions() {
		return instructions;
	}

	public void setInstructions(ArrayList<Statement> instructions) {
		this.instructions = instructions;
	}

	@Override
	public Object execute(Scope scope, ArrayList<Object> args) {
		Scope currScope = new Scope();
		if (args.size() != arguments.size()) {
			throw new CancellationException(
					"Niepoprawna ilosc argumentow. Jest " + args.size() + " a powinno byc " + arguments.size());
		}
		int i = 0;
		for (VarDeclaration var : arguments) {
			var.execute(currScope, args);
			Object value = ((Expression)args.get(i)).evaluate(scope);
			currScope.setValue(var.getName(), value);
			++i;
		}
		return runInstructions(instructions, currScope);
	}


	public static Object runInstructions(ArrayList<Statement> instr, Scope scope) {
		for (Statement ins : instr) {
			if (ins instanceof ReturnStatement)
				return ins.execute(scope, null);
			Object result=ins.execute(scope, null);
			if(result!=null)//return w instrukcji
				return result;
		}
		return null;
	}
}
