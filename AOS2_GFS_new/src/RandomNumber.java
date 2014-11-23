import java.util.Random;

public class RandomNumber {

	public static int randomInt(int min, int max) {
		Random rand = new Random();
		return (int) (min + (max - min + 1) * rand.nextDouble());
	}

	public static float randomFloat(int min, int max, float fraction) {
		return (RandomNumber.randomInt(min, max)) * fraction;
	}

	public static int randomIntWithException(int min, int max, int except) {
		Random rand = new Random();
		int randNumber = (int) (min + (max - min) * rand.nextDouble());
		if (randNumber < except)
			return randNumber;
		else
			return randNumber + 1;
	}

	public static int randomIntExcept(int except) {
		Random rand = new Random();
		int randNumber = (int) (1 + (14) * rand.nextDouble());
		if (randNumber < except)
			return randNumber;
		else
			return randNumber + 1;
	}

	public static void not_main(String[] args) {
		while (true) {
			float rand = RandomNumber.randomFloat(0, 100, 0.01f);
			System.out.println("RandomNumber.0-1:" + rand);
			rand = RandomNumber.randomFloat(25, 100, 0.01f);
			System.out.println("RandomNumber.0.25-1:" + rand);

			int rand1 = RandomNumber.randomIntWithException(1, 15, 7);
			System.out.println("RandomNumber.-7:" + rand1);
			if (rand1 == 7)
				break;
		}
	}

}
