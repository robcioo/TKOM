package parser.values;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;

import parser.Expression;
import semantics.Scope;

public class ListIndexOperator implements Expression {
	private Expression index;
	public ListIndexOperator(String var) {
		super();
		this.var = var;
	}

	String var;

	@Override
	public Object evaluate(Scope scope) {
		Object ob=scope.getValue(var);
		Object exp=index.evaluate(scope);
		if(ob instanceof ArrayList && exp instanceof Number){
			try{
			return ((ArrayList)ob).get(new BigDecimal(exp.toString()).intValue());
			}catch (IndexOutOfBoundsException e) {
				throw new CancellationException("Index poza zakresem: "+e.getMessage());
			}
		}
		else if(ob instanceof String && exp instanceof Number){
			return ((String)ob).charAt(new BigDecimal(exp.toString()).intValue());
		}
		else 
			throw new CancellationException("Nie mozna odwolac sie za pomoca indexu do zmiennej typu "+ob.getClass());
	}

	public Expression getIndex() {
		return index;
	}

	public void setIndex(Expression index) {
		this.index = index;
	}
}
