package parser.statements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
		FunctionStatement func = functions.get(name);
		if(func==null)
			throw new CancellationException("Nie zdefiniowano funkcji: "+ name);
		return func.execute(new Scope(scope), new ArrayList<Object>(arguments));

	}

	public Object executeSystem(Scope scope, ArrayList<Object> args) {
		return executeSystem(name, scope, args.get(0), arguments);
	}

	private Object executeSystem(String func, Scope scope, Object var, List<Expression> args) {
		switch (func) {
		case "length":
			return length(var.toString(), scope);
		case "subList":
			return subList(var.toString(), scope, args);
		}
		throw new CancellationException("Nieobslugiwana funkcja wbudowana: " + func);
	}

	private Object subList(String var, Scope scope, List<Expression> args) {
		if(args==null || args.size()!=2)
			throw new CancellationException("Błędna ilość argumentów funkcji subList. Powinno być 2 a jest:"+args.size());
		Object ob = scope.getValue(var);
		Object param1=args.get(0).evaluate(scope);
		Object param2=args.get(1).evaluate(scope);
		if(!(ob instanceof ArrayList))
			throw new CancellationException("Nie można wywolać funkcji subList na zmiennej typu "+ob.getClass());
		if(!(param1 instanceof Long))
			throw new CancellationException("Nie można wywolać funkcji subList z parametrem typu "+param1.getClass()+". Oczekiwano long.");
		if(!(param2 instanceof Long))
			throw new CancellationException("Nie można wywolać funkcji subList z parametrem typu "+param2.getClass()+". Oczekiwano long.");
		return ((ArrayList<Object>)ob).subList(((Long)param1).intValue(), ((Long)param2).intValue());
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