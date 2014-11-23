//package beans;

/**
 * To extract instruction data binop,operation,optional data from the given the
 * int-instruction.
 * 
 * @author Satyam Kotikalapudi
 * */
public class Instruction {
	// byte binop;
	byte binop;
	Operation op;
	int optionalData;

	/**
	 * Gets Binop value
	 * 
	 * @return binop
	 */
	public byte getBinop() {
		return binop;
	}

	/**
	 * Gets operation value
	 * 
	 * @return Operation
	 */
	public Operation getOp() {
		return op;
	}

	/**
	 * Gets optional value
	 * 
	 * @return OptionalData
	 */
	public int getOptionalData() {
		return optionalData;
	}

	/**
	 * Create the Instruction object for the given int instruction
	 * 
	 * @param int instruction
	 */
	public Instruction(int instruction) {
		// binop extraction
		binop = (byte) (instruction >>> 31);
		// operation extraction
		int operation;
		operation = ((instruction >>> 24) & 127);
		// optional data
		optionalData = (instruction & 0x00FFFFFF);// 0x00FFFFFF = 00000000
													// 11111111 11111111
													// 11111111
		if (binop == 0) {
			switch (operation) {
			case 0: // POP(1)
				op = Operation.POP;
				break;
			case 1:// PUSH(2),
				op = Operation.PUSH_CONST;
				break;
			case 2:// PUSH(2),
				op = Operation.PUSH_IP;
				break;
			case 3:// PUSH(2),
				op = Operation.PUSH_SP;
				break;
			case 4: // LOAD(3)
				op = Operation.LOAD;
				break;
			case 5: // STORE(4)
				op = Operation.STORE;
				break;
			case 6: // JMP(5),
				op = Operation.JMP;
				break;
			case 7: // NOT(7)
				op = Operation.NOT;
				break;
			case 8: // PUTC(8),
				op = Operation.PUTC;
				break;
			case 9: // GETC(9),
				op = Operation.GETC;
				break;
			case 10: // HALT(10)
				op = Operation.HALT;
				break;
			default:
				op = Operation.ILLEGAL_OP;
				break;
			}
		} else {// binop==1
			switch (operation) {

			case 0: // ADD(10)
				op = Operation.ADD;
				break;
			case 1:// SUB(11)
				op = Operation.SUB;
				break;
			case 2: // MUL(12)
				op = Operation.MUL;
				break;
			case 3: // DIV(13)
				op = Operation.DIV;
				break;
			case 4: // AND(14),
				op = Operation.AND;
				break;
			case 5: // OR(15)
				op = Operation.OR;
				break;
			case 6: // XOR(16),
				op = Operation.XOR;
				break;
			case 7: // EQ(17),
				op = Operation.EQ;
				break;
			case 8: // LT(18)
				op = Operation.LT;
				break;
			default:
				op = Operation.ILLEGAL_OP;
				break;
			}

		}
	}

}
