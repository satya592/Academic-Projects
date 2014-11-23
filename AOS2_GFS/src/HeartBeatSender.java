import java.io.ObjectOutputStream;

public class HeartBeatSender implements Runnable {
	ObjectOutputStream out;
	String sender;
	String receiver;

	/**
	 * ObjectOutputStream out, String sender, String receiver
	 **/
	HeartBeatSender(ObjectOutputStream out, String sender, String receiver) {
		this.out = out;
		this.sender = sender;
		this.receiver = receiver;
	}

	@Override
	public void run() {
		Message hbMsg = new Message(Message.BEATS, sender, receiver, null);
		while (true) {
			try {
				Thread.sleep(5000);
				out.writeObject(hbMsg);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
