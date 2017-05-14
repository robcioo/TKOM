package Tree;

public class Tree<T>  extends Node<T>{

	public Tree(T data) {
		super(data);
	}
	
	@Override
	public String toString() {
		return super.toString(0);
	}

}
