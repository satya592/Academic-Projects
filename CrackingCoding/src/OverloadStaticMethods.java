class Base1 {
	static void fun(int a) {
		System.out.println("I am int");
	}

	static void fun(float a) {
		System.out.println("I am float");
	}

	static void fun(char a) {
		System.out.println("I am char");
	}

	static void fun(long a) {
		System.out.println("I am long base");
	}
}

public class OverloadStaticMethods extends Base1 {
	static void fun(long a) {
		System.out.println("I am long");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		OverloadStaticMethods o1 = new OverloadStaticMethods();
		OverloadStaticMethods.fun(0);
		OverloadStaticMethods.fun(0l);
		OverloadStaticMethods.fun(0f);
		OverloadStaticMethods.fun('0');
	}

}
