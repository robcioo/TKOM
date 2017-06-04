package semantics;

import java.util.HashMap;
import java.util.concurrent.CancellationException;

import parser.Var;

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
			throw new CancellationException("Powtorna deklaracja zmiennej: (" + var + " " + name + ")");
		vars.put(name, var);
	}

	public boolean contains(String name) {
		return vars.containsKey(name) || (parent != null && parent.contains(name));
	}

	public boolean contains(String name, String type) {
		return contains(name) && vars.get(name).equals(type);
	}

	public Var getType(String name) {
		contains(name);
		Var var = vars.get(name);
		if (var == null)
			return parent.getType(name);
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
		currScope.vars.get(name).setValue(value);
		return value;
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
