package tokenizer;

public class Token {
	private String value;
	private TokenType type;
	private long line;
	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public TokenType getType() {
		return type;
	}


	public void setType(TokenType type) {
		this.type = type;
	}



	public Token(String value, TokenType type, long line) {
		super();
		this.value = value;
		this.type = type;
		this.line=line;
	}
	
	
	@Override
	public String toString() {
		return "{value: "+value+" type: "+type.toString()+"}";
	}


	public long getLine() {
		return line;
	}
}
