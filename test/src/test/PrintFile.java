package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PrintFile {
	public static int hex2decimal(String s) {
		String digits = "0123456789ABCDEF";
		s = s.toUpperCase();
		int val = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			int d = digits.indexOf(c);
			val = 16 * val + d;
		}
		return val;
	}

	public static void main(String[] args) {

		// TODO Auto-generated method stub
		try {
			BufferedReader br = new BufferedReader(new FileReader("task2.bin"));
			String line = null;
			int count = 0;
			while ((line = br.readLine()) != null) {
				// process the line.
				System.out.print(line + "=");
				// line = "FFFFFFFF";
				System.out.println(hex2decimal(line));
				count++;
				// System.out.println(Integer.parseInt(line, 16));
			}
			System.out.println("count=" + count);
			br.close();

			// br = new BufferedReader(new FileReader("in.txt"));
			// line = null;
			//
			// while ((line = br.readLine()) != null) {
			// // process the line.
			// System.out.print(line);
			// // line = "FFFFFFFF";
			// // System.out.println(hex2decimal(line));
			// // System.out.println(Integer.parseInt(line, 16));
			// }
			//
			// br.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
