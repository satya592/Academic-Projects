//package beans;

/**
 * List of valid operation codes
 * 
 * @author Satyam Kotikalapudi
 */
public enum Operation {
	// BINOP 0
	POP(0), PUSH_CONST(1), PUSH_IP(2), PUSH_SP(3), LOAD(4), STORE(5), JMP(6), NOT(
			7), PUTC(8), GETC(9), HALT(10),
	// BINOP 1
	ADD(10), SUB(11), MUL(12), DIV(13), AND(14), OR(15), XOR(16), EQ(17), LT(18), ILLEGAL_OP(
			19);

	private int value;

	private Operation(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
