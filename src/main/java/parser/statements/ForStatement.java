package parser.statements;

import java.util.ArrayList;

import parser.Expression;
import parser.Statement;
import semantics.Scope;

public class ForStatement implements Statement {

	private Expression condition;
	private ArrayList<Statement> instructions;
	private ArrayList<Statement> init;
	private Expression post;

	public ForStatement() {
		init=new ArrayList<>();
		instructions=new ArrayList<>();
	}
	

	public void setCondition(Expression condition) {
		this.condition = condition;
	}

	public void addInstruction(Statement ins) {
		instructions.add(ins);
	}

	public void addInstructions(ArrayList<Statement> ins) {
		instructions.addAll(ins);
	}

	public void addInit(ArrayList<Statement> init) {
		this.init = init;
	}

	public void addPostInstruction(Expression post) {
		this.post = post;
	}

	@Override
	public Object execute(Scope scope, ArrayList<Object> args) {
		Scope currScope=new Scope(scope);
		FunctionStatement.runInstructions(init, currScope);
		Object ret=null;
		while((Boolean)condition.evaluate(currScope)){
			ret=FunctionStatement.runInstructions(instructions, currScope);
			post.evaluate(currScope);
		}
		if(ret==null)
			return new ArrayList<>();
		return ret;
		
	}
}