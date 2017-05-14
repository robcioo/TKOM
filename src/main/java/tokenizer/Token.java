package tokenizer;

public class Token {
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


	private String value;
	private TokenType type;
	public Token(String value, TokenType type) {
		super();
		this.value = value;
		this.type = type;
	}
	
	
	@Override
	public String toString() {
		return "{value: "+value+" type: "+type.toString()+"}";
	}
}
