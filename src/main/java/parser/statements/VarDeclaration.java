package parser.statements;

import java.util.ArrayList;
import java.util.concurrent.CancellationException;

import parser.Statement;
import parser.Var;
import parser.VarType;
import semantics.Scope;
import tokenizer.TokenType;

public class VarDeclaration implements Statement {
	private TokenType dataType;
	private String name;

	public VarDeclaration(TokenType type, String name) {
		this.setDataType(type);
		this.setName(name);
	}

	@Override
	public Object execute(Scope scope, ArrayList<Object> args) {
		scope.putVar(name, new Var(getDataType(),null));
		return null;
	}

	public VarType getDataType() {
		switch (dataType) {
		case BOOL:
			return VarType.BOOL;
		case DOUBLE:
			return VarType.DOUBLE;
		case LIST:
			return VarType.LIST;
		case LONG:
			return VarType.LONG;
		case STRING:
			return VarType.STRING;
		default:
			throw new CancellationException("Niepoprawny typ danych: " + dataType);
		}
	}

	public void setDataType(TokenType dataType) {
		this.dataType = dataType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
