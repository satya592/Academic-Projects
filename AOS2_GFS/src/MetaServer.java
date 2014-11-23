public class MetaServer {
	Node node;

	MetaServer(Node node) {
		this.node = node;

		while (!node.systemUp) {
			System.out.println("MetaServer: System in not up.."
					+ node.inputObjects_Send.size() + "<"
					+ (node.clientcount + 1));
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	void run() {
		Message inmsg;
		while (true) {
			while (node.inQ.size() != 0) {
				synchronized (node.inQ) {
					inmsg = node.inQ.poll();
				}
				takeAction(inmsg);
			}
		}
	}

	boolean takeAction(Message iMsg) {
		switch (iMsg.type) {
		case Message.BEATS:

		}
		return false;
	}
}
