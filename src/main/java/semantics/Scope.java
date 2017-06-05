package semantics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CancellationException;

import parser.Var;
import parser.VarType;

public class Scope {
	Scope parent;
	HashMap<String, Var> vars;

	public Scope() {
		vars = new HashMap<>();
	}

	public Scope(Scope parent) {
		this();
		this.parent = parent;
	}

	public void putVar(String name, Var var) {
		if (contains(name))
			throw new CancellationException("Powtorna deklaracja zmiennej: ( Nazwa: " + name  + var + ")");
		vars.put(name, var);
	}

	public boolean contains(String name) {
		return vars.containsKey(name) || (parent != null && parent.contains(name));
	}

	public boolean contains(String name, String type) {
		return contains(name) && vars.get(name).equals(type);
	}

	public Var getVar(String name) {
		contains(name);
		Var var = vars.get(name);
		if (var == null)
			return parent.getVar(name);
		else
			return var;
	}

	public void notContainsWithException(String value) {
		if (!contains(value))
			throw new CancellationException("Nie zdefiniowano zmiennej: " + value);
	}

	public Object setValue(String name, Object value) {
		notContainsWithException(name);
		Scope currScope = this;
		while (currScope.vars.get(name) == null) {
			currScope = currScope.parent;
		}
		currScope.vars.get(name).setValue(getValueType(currScope.vars.get(name).getDataType(), value));
		return value;
	}

	private Object getValueType(VarType dataType, Object value) {
		switch (dataType) {
		case BOOL:
			if (!(value instanceof Boolean))
				throw new CancellationException("Nie mozna zrzutowac " + value.getClass() + " na Boolean");
			return value;
		case DOUBLE:
			if (value instanceof Double)
				return value;
			else if (value instanceof Long) {
				return new BigDecimal((Long) value).longValue();
			} else
				throw new CancellationException("Nie mozna zrzutowac " + value.getClass() + " na Double");
		case LIST:
			if (value instanceof ArrayList)
				return value;
			else {
				ArrayList<Object> arr = new ArrayList<>();
				arr.add(value);
				return arr;
			}
		case LONG:
			if (value instanceof Long)
				return value;
			else if (value instanceof Double) {
				return new BigDecimal((Double) value).longValue();
			} else
				throw new CancellationException("Nie mozna zrzutowac " + value.getClass() + " na Long");
		case STRING:
			return value.toString();
		}
		return null;
	}

	public Object getValue(String name) {
		notContainsWithException(name);
		Scope currScope = this;
		while (currScope != null && currScope.vars.get(name) == null) {
			currScope = currScope.parent;
		}
		return currScope.vars.get(name).getValue();
	}
}
