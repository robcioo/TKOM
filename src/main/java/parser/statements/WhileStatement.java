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
	public Object execute(Scope scope, Object... args) {
		// TODO Auto-generated method stub
		
	}
	

}