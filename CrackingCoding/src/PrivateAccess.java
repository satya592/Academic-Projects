public class PrivateAccess {
	private int a;
	private int b;

	public static void main(String[] args) {
		PrivateAccess pa = new PrivateAccess();
		pa.a = 1;
		pa.b = 2;
		System.out.println(pa.a);
		System.out.println(pa.b);

		AnotherPrivate ap = new AnotherPrivate();
		// ap.c = 1;
		// System.out.println(ap.c);

	}

}

class AnotherPrivate {
	private int c;
}