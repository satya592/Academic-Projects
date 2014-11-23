import java.util.Scanner;

public class GreyCode {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner input = new Scanner(System.in);

		byte a = input.nextByte();
		byte b = input.nextByte();

		if (a < 0)
			a += 127;
		if (b < 0)
			b += 127;

		System.out.println(a + ":" + b);
		byte x = (byte) (a ^ b);
		System.out.println(x);
		System.out.println(x & (x - 1));
		if ((x & (x - 1)) == 0)
			System.out.println("True");
		else
			System.out.println("false");

		if (x < 0)
			x += 256;

		input.close();
	}

}
