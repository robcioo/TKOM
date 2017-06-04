package parser.statements;

import java.util.ArrayList;
import java.util.HashMap;

import parser.Statement;
import semantics.Scope;

public class FuctionCallStatement implements Statement {
	private String name;
	private ArrayList<Statement> arguments;
	private HashMap<String, FunctionStatement> functions;

	public FuctionCallStatement(String name, HashMap<String, FunctionStatement> functions) {
		super();
		this.functions=functions;
		arguments = new ArrayList<>();
		this.setName(name);
	}

	public void addArgument(Statement instruction) {
		arguments.add(instruction);
	}

	@Override
	public Object execute(Scope scope,ArrayList<Object> args) {
		if(args.size() ==2 && (boolean)args.get(0)){//systemowe
			return 2L;//executeSystem(name,args.get(1));
		}
		else{
			FunctionStatement func = functions.get(name);
			return func.execute(new Scope(scope), args);
		}
	}

	private Object executeSystem(String name2, Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}