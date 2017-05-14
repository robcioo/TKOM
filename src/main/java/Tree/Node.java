package Tree;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {
	private ArrayList<Node<T>> children = null;
	private Node<T> parent = null;
	private T data = null;

	public Node(T data) {
		this.data = data;
		children=new ArrayList<>();
	}

	public Node(T data, Node<T> parent) {
		this.data = data;
		this.parent = parent;
	}

	public ArrayList<Node<T>> getChildren() {
		return children;
	}

	private void setParent(Node<T> parent) {
		this.parent = parent;
	}

	public Node<T> addLChild(T data) {
		Node<T> child = new Node<T>(data);
		child.setParent(this);
		children.add(child);
		return child;
	}
	public Node<T> addLChild(Node<T> data) {
		data.setParent(this);
		children.add(data);
		return data;
	}

	public T getData() {
		return this.data;
	}

	@Override
	public String toString() {

		return super.toString();
	}

	public String toString(int i) {
		String s = data.toString() + "#### " + i + " ###\n";
		for (Node<T> left : children)
			s += left.toString(i + 1);
		return s + "\n";
	}
}