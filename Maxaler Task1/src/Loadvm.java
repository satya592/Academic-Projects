//package edu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Loads the vm data from the binary file and executes the valid instructions
 * 
 * @author Satyam
 * 
 */
/*
 * Overwrite this file with:
 * 
 * Intruction.java is for extracting instruction data binop,operation,optional
 * data from the given the int-instruction. Operation.java is list of valid
 * operation codes Loadvm is for loading the vm data from the binary file and
 * executes the valid instructions Details to build/run the VM. Pass the binary
 * file as commandline argument and make sure the binary file is in same
 * directory of execution. Designed efficiently to accommodate any new
 * operations and provided java docs for each file and the methods defined in
 * them Note you must not make sub-directories or include binary files in this
 * directory.
 */
public class Loadvm {
	int dataSize;// = wrapped.getInt();
	int dataImage;
	int[] data;
	int ip = 0;
	int sp = 0;
	final String FILE_NAME;

	/**
	 * Assign the filename
	 */
	Loadvm(String fname) throws IOException {
		FILE_NAME = fname;
	}

	/**
	 * 
	 * part 1- Load the data-images from the binary file on to data array. part
	 * 2- Execute the instructions in the data array
	 * 
	 * @param filename
	 *            name of the file as command line argument
	 */
	public static void main(String[] args) throws IOException {

		Loadvm binary = new Loadvm(args[0]);
		// part 1 - Load data
		binary.loadData();
		// part 2 - Interpret data
		binary.interpreter();
	}

	/**
	 * Interpret each command and execute it.
	 * 
	 * @param void
	 */
	void interpreter() {
		int currentInst = 0;
		Instruction currentInstruction = null;
		sp = this.dataSize;
		while (true) {
			currentInst = data[ip];
			ip++;
			currentInstruction = new Instruction(currentInst);
			this.decodeInstruction(currentInstruction);
		}
	}

	/**
	 * Decode the instruction and execute it.
	 * 
	 * @param instruction
	 *            Instruction to be executed
	 */
	void decodeInstruction(Instruction instruction) {
		int op1 = 0, op2 = 0;
		if (instruction.getBinop() == 1) {
			op2 = g();
			op1 = g();
		}
		// System.out.println(instruction.getOp());
		switch (instruction.getOp()) {
		case POP: // POP(1)
			sp++;
			break;
		case PUSH_CONST:// PUSH(2),
			f(instruction.getOptionalData());
			break;
		case PUSH_IP:// PUSH(2),
			f(ip);
			break;
		case PUSH_SP:// PUSH(2),
			f(sp);
			break;
		case LOAD: // LOAD(3)
			int addr = g();
			f(data[addr]);
			break;
		case STORE: // STORE(4)
			int st_data = g();
			int addr1 = g();
			data[addr1] = st_data;
			break;
		case JMP: // JMP(5),
			int cnd = g();
			int addr2 = g();
			if (cnd != 0)
				ip = addr2;
			break;
		case NOT: // NOT(6)
			if (g() == 0)
				f(1);
			else
				f(0);
			break;
		case PUTC: // PUTC(7),
			byte b = (byte) (g() & 0XFF);
			System.out.print((char) b);
			break;
		case GETC: // GETC(8),
			try {
				byte x;
				x = (byte) System.in.read();
				int x32 = x;
				f(x32 & 0xff);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case HALT: // HALT(9)
			System.exit(0);
			break;
		case ADD: // ADD(10)
			f(op1 + op2);
			break;
		case SUB:// SUB(11)
			f(op1 - op2);
			break;
		case MUL: // MUL(12)
			f(op1 * op2);
			break;
		case DIV: // DIV(13)
			f(op1 / op2);
			break;
		case AND: // AND(14),
			f(op1 & op2);
			break;
		case OR: // OR(15)
			f(op1 | op2);
			break;
		case XOR: // XOR(16),
			f(op1 ^ op2);
			break;
		case EQ: // EQ(17),
			if (op1 == op2)
				f(1);
			else
				f(0);
			break;
		case LT: // LT(18)
			if (op1 < op2)
				f(1);
			else
				f(0);
			break;
		default:
			System.out.println("Illegal opeation encountered :: ");
			System.exit(1);
			break;

		}
	}

	/**
	 * Push the data on to the stack.
	 * 
	 * @param v
	 *            value to be inserted on
	 */
	void f(int v) {
		sp--;
		data[(sp)] = (v);
	}

	/**
	 * Pop the data on to the stack.
	 * 
	 * @param void
	 * @return value on top the stack is returned
	 */
	int g() {
		int v = data[(sp)];
		sp++;
		return v;
	}

	/**
	 * Load the data-images from the binary file on to data array.
	 * 
	 * @param void
	 * @return integer data array
	 */
	int[] loadData() {

		try {
			BufferedReader br = new BufferedReader(new FileReader(
					this.FILE_NAME));
			String line = null;
			line = br.readLine();
			if (line != null) {
				this.dataSize = hex2decimal(line);
			}
			line = br.readLine();
			if (line != null) {
				this.dataImage = hex2decimal(line);
			}
			data = new int[dataSize];
			for (int i = 0; (line = br.readLine()) != null && i < dataImage; i++) {
				// process the line.
				// System.out.print(line);
				data[i] = hex2decimal(line);
				// System.out.println("=" + data[i]);

			}

			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return data;

	}

	/**
	 * Convert hexadecimal string to decimal int
	 * 
	 * @param s
	 *            hexadecimal string
	 * @return decimal int
	 */
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
}
