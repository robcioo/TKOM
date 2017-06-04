package parser;

public class Var {
	private VarType dataType;
	private Object value;

	public Var(VarType dataType,Object value) {
		super();
		this.value = value;
		this.dataType=dataType;
	}

	public VarType getDataType() {
		return dataType;
	}

	public void setDataType(VarType dataType) {
		this.dataType = dataType;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return (dataType!=null?dataType.toString():"")+" "+(value!=null?value.toString():"null");
	}
}
