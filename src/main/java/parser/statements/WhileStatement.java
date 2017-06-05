package parser.statements;

import java.util.ArrayList;

import parser.Expression;
import parser.Statement;
import semantics.Scope;

public class WhileStatement implements Statement{
	private Expression condition;
	private ArrayList<Statement> instructions;
	
	public WhileStatement() {
		instructions=new ArrayList<>();
	}

	public void setCondition(Expression condition) {
		this.condition = condition;
	}
	
	public void addInstruction(Statement ins){
		instructions.add(ins);
	}
	public void addInstructions(ArrayList<Statement> ins){
		instructions.addAll(ins);
	}

	@Override
	public Object execute(Scope scope, ArrayList<Object> args) {
		Scope currScope=new Scope(scope);
		while((Boolean)condition.evaluate(currScope)){
			Object ret=FunctionStatement.runInstructions(instructions, new Scope(currScope));
			if(ret!=null)
				return ret;
		}
		return null;
	}
	

}