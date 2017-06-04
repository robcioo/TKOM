package parser.statements;

import java.util.ArrayList;
import java.util.concurrent.CancellationException;

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
		if (args.size() != arguments.size()) {
			throw new CancellationException(
					"Niepoprawna ilosc argumentow. Jest " + args.size() + " a powinno byc " + arguments.size());
		}
		return runInstructions(instructions, scope);
	}

	private VarType validateAndChooseType(Class<? extends Object> class1, VarType varType) {

		switch (varType) {
		case BOOL:
			if (Boolean.class.equals(class1))
				throw new CancellationException("Bledny typ argumentu wywolania: " + class1);
			return VarType.BOOL;
		case DOUBLE:
			if (Double.class.equals(class1))
				throw new CancellationException("Bledny typ argumentu wywolania: " + class1);
			return VarType.DOUBLE;
		case LIST:
			if (ArrayList.class.equals(class1))
				throw new CancellationException("Bledny typ argumentu wywolania: " + class1);
			return VarType.LIST;
		case LONG:
			if (Long.class.equals(class1))
				throw new CancellationException("Bledny typ argumentu wywolania: " + class1);
			return VarType.LONG;
		case STRING:
			if (String.class.equals(class1))
				throw new CancellationException("Bledny typ argumentu wywolania: " + class1);
			return VarType.STRING;
		default:
			throw new CancellationException("Nieobsugiwany typ " + varType);

		}
	}
	
	public static Object runInstructions(ArrayList<Statement> instr, Scope scope){
		for (Statement ins : instr) {
			if (ins instanceof ReturnStatement)
				return ins.execute(scope,null);
			ins.execute(scope,null);
		}
		return new ArrayList<>();
	}
}
