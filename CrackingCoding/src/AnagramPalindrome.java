/**
1) Given a string, check if there exists some anagram of the string which is a palindrome.
Function Signature: bool anagramPalindrome(string word)

Sample Testcases:
a) anagramPalindrome("rotate") returns false, no anagram of "rotate" is a palindrome
b) anagramPalindrome("hanna") returns true, since using letters from "hanna", we can form the palindrome "nahan"

 */

/**
 * @author satyamkotikalapudi
 *
 */
public class AnagramPalindrome {

	public static boolean anagramPalindrome(String word) {
		boolean found = true;
		boolean oddChar = false;
		char[] wordzChars = word.toCharArray();
		int[] ASCIICOUNT = new int[256];
		for (int i = 0; i < wordzChars.length; i++)
			ASCIICOUNT[wordzChars[i]]++;
		for (int i = 0; i < ASCIICOUNT.length; i++)
			if (ASCIICOUNT[i] % 2 == 1)
				if (oddChar)
					return false;
				else
					oddChar = true;
		return found;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String input = "hihibyeeyb";
		boolean found = anagramPalindrome(input);
		if (found)
			System.out.println("anagramPalindrome found");
		else
			System.out.println("anagramPalindrome not found");
	}

}
