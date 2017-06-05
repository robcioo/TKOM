package parser.statements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CancellationException;

import parser.Expression;
import parser.Statement;
import semantics.Scope;

public class FuctionCallStatement implements Statement, Expression {
	private String name;
	private ArrayList<Expression> arguments;
	private HashMap<String, FunctionStatement> functions;

	public FuctionCallStatement(String name, HashMap<String, FunctionStatement> functions) {
		super();
		this.functions = functions;
		arguments = new ArrayList<>();
		this.setName(name);
	}

	public void addArgument(Expression instruction) {
		arguments.add(instruction);
	}

	@Override
	public Object execute(Scope scope, ArrayList<Object> args) {
		if (args.size() == 2 && (boolean) args.get(0)) {// systemowe
			return executeSystem(name, scope, args.get(1));
		} else {
			FunctionStatement func = functions.get(name);
			return func.execute(new Scope(scope), new ArrayList<Object>(arguments));
		}
	}

	private Object executeSystem(String func, Scope scope, Object var) {
		switch (func) {
		case "length":
			return length(var.toString(), scope);
		}
		throw new CancellationException("Nieobslugiwana funkcja wbudowana: " + func);
	}

	private Object length(String var, Scope scope) {
		Object ob = scope.getValue(var);
		if (ob instanceof ArrayList)
			return ((ArrayList) ob).size();
		if (ob instanceof String)
			return ((String) ob).length();
		return new Long(1);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Object evaluate(Scope scope) {
		return execute(scope, new ArrayList<Object>(arguments));
	}

}