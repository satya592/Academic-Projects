public class Server {
	Node node;

	public Server(Node node) {
		this.node = node;
		while (!node.systemUp) {
			System.out.println("Server: System in not up.."
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
			}
		}
	}

}
