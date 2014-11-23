class Base {
	void fun(int a) {
		System.out.println("I am int");
	}

	void fun(float a) {
		System.out.println("I am float");
	}

	void fun(char a) {
		System.out.println("I am char");
	}
}

public class OverloadingInherit extends Base {
	void fun(long a) {
		System.out.println("I am long");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		OverloadingInherit o1 = new OverloadingInherit();
		o1.fun(0);
		o1.fun(0l);
		o1.fun(0f);
		o1.fun('0');
	}

}
