package semantics;

import java.util.concurrent.CancellationException;

public class Comparator {

	public static int compare(Object arg1, Object arg2) {
		if ((arg1 instanceof Number) && (arg2 instanceof Number)) {
			return Double.compare(new Double(arg1.toString()), new Double(arg2.toString()));
		} else if (arg1 instanceof Boolean && arg2 instanceof Boolean) {
			return Boolean.compare((Boolean) arg1, (Boolean) arg2);
		} else
			throw new CancellationException("Nie mozna porownac obiektow klas " + arg1.getClass() + " i " + arg2.getClass());
	}

}
