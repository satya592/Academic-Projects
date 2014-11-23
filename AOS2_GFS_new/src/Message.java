import java.io.Serializable;

public class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	static final int BEATS = 0;
	static final int READ = 1;
	static final int WRITE = 2;
	static final int APPEND = 3;
	static final int CREATE = 4;
	// static final int HB_SER = 5;
	// static final int HB_REG = 6;
	// static final int HB_ACK = 7;

	static final int STATUS_REQ = 8;
	static final int STATUS_FAIL = 9;
	static final int STATUS_SUCCESS = 10;

	int type;
	int status;
	String fileName;
	int chunkNo;
	int offSet;
	int len;

	String data;

	public Message(int type, int status, String fileName, int chunkNo,
			int offSet, int len, String data) {
		super();
		this.type = type;
		this.status = status;
		this.fileName = fileName;
		this.chunkNo = chunkNo;
		this.offSet = offSet;
		this.len = len;
		this.data = data;
	}

	public String toString() {
		StringBuilder msgtext = new StringBuilder();

		switch (status) {
		case STATUS_REQ:
			msgtext.append("REQ|");
			break;
		case STATUS_FAIL:
			msgtext.append("FAIL|");
			break;
		case STATUS_SUCCESS:
			msgtext.append("PASS|");
			break;
		default:
			msgtext.append("UNK|");
		}

		switch (type) {
		case READ:
			msgtext.append("Read");
			msgtext.append("|" + fileName + "|" + offSet + "|" + len + "|-->"
					+ data);
			break;
		case BEATS:
			msgtext.append("Beats");
			break;
		case WRITE:
			msgtext.append("Write");
			msgtext.append("|" + fileName + "|-->" + data);
			break;
		case CREATE:
			msgtext.append("Create");
			msgtext.append("|" + fileName + "|-->" + data);
			break;
		case APPEND:
			msgtext.append("Append");
			msgtext.append("|" + fileName + "|-->" + data);
			break;
		default:
			msgtext.append("unknown");
		}

		return msgtext.toString();
	}
}