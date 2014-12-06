public class ListenerWrapper implements Runnable {
	static final int REQ = 1;
	static final int DEMON = 2;
	static final int META_REQ = 3;
	static final int SER_REQ = 4;

	int port;
	int type;

	public ListenerWrapper(int port) {
		this.port = port;
		this.type = REQ;
	}

	public ListenerWrapper(int reqPort, int type) {
		this.port = reqPort;
		this.type = type;
	}

	@Override
	public void run() {
		new Listener(port, type);
	}

}
