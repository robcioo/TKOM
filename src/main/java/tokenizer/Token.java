package tokenizer;

import java.util.concurrent.CancellationException;

public class Token {
	private TokenType parentType;
	private TokenType type;
	private TokenPriority priority;
	private long line;


	public TokenType getType() {
		return type;
	}


	public void setType(TokenType type) {
		this.type = type;
	}



	public Token(TokenType parentType,TokenPriority priority, TokenType type, long line) {
		super();
		if(type==null)
			throw new CancellationException("Token constructor: type cannot be null");
		this.priority = priority;
		this.type = type;
		this.line=line;
		this.setParentType(parentType);
	}
	
	
	@Override
	public String toString() {
		return "{Prority: "+priority+" type: "+type.toString()+"}";
	}




	public TokenPriority getPriority() {
		return priority;
	}


	public void setPriority(TokenPriority priority) {
		this.priority = priority;
	}


	public TokenType getParentType() {
		return parentType;
	}


	public void setParentType(TokenType parentType) {
		this.parentType = parentType;
	}
}
