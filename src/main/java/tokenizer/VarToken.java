package tokenizer;

public class VarToken extends Token {
	private String value;

	public VarToken(String value, TokenType parentType,TokenPriority priority, TokenType type, long line) {
		super(parentType,priority, type, line);
		this.setValue(value);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
