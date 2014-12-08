import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Client {

	static void log(String msg) {
		// System.out.println(msg);
	}

	public static MetaMessage getMetaMessage(String msg[]) {
		// new MetaMessage(
		// Message.READ, Message.STATUS_REQ, "", 0, 10,
		// null, ""))
		if (msg == null || !(msg.length >= 3))
			return null;
		else {
			MetaMessage metaMsg = new MetaMessage(0, 0, null, 0, 0, null, null,
					null, null, null);
			String fname = msg[1];
			metaMsg.fileName = fname;

			if (msg[0].equals("r") && msg.length == 4) {
				// r|file1.txt|8000|50
				int offSet = Integer.valueOf(msg[2]);
				int len = Integer.valueOf(msg[3]);

				metaMsg.type = MetaMessage.READ;
				metaMsg.offSet = offSet;
				metaMsg.len = len;
				metaMsg.status = MetaMessage.STATUS_REQ;

			} else if (msg[0].equals("w") && msg.length == 3) {
				// w|file1.txt|
				metaMsg.type = MetaMessage.WRITE;
				metaMsg.status = MetaMessage.STATUS_REQ;
				metaMsg.len = msg[2].length();
			} else if (msg[0].equals("a") && msg.length == 3) {
				// a|file1.txt|
				metaMsg.type = MetaMessage.APPEND;
				metaMsg.status = MetaMessage.STATUS_REQ;
				metaMsg.len = msg[2].length();
			} else {
				return null;
			}

			return metaMsg;
		}
	}

	static boolean takeAction(MetaMessage msg) {
		if (msg.status == MetaMessage.STATUS_FAIL)
			return false;
		switch (msg.type) {
		case MetaMessage.APPEND:
		case MetaMessage.WRITE:
		case MetaMessage.READ:
		}
		return true;
	}

	public static void main(String[] args) {
		String fileName = null;
		if (args.length != 1) {
			log("Please provide the file");
			System.exit(1);
		}
		fileName = args[0];
		BufferedReader br = null;
		try {
			String serverName = Config.getValue("metaserver");
			String portNo = Config.getValue("meta_req_port");

			File myFile = new File(fileName);
			if (!myFile.exists()) {
				log("Error: File does not exits");
				System.exit(1);
			}

			br = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = br.readLine()) != null) {

				String[] param = line.split("\\|");

				// for (String str : param) {
				// System.out.print(str);
				// System.out.print(" | ");
				// }

				MetaMessage metamsg = getMetaMessage(param);

				int count = 0;

				while (count < 3) {
					MetaMessage statusMsg = Sender.messageToMetaServer(
							serverName, portNo, metamsg);

					if (statusMsg != null)
						log(statusMsg.toString());

					if (statusMsg.status == MetaMessage.STATUS_SUCCESS) {
						log("Success");
						String data = null;
						if (statusMsg.type != MetaMessage.READ)
							data = param[2];

						Message req_msg = new Message(statusMsg.type,
								Message.STATUS_REQ, statusMsg.fileName,
								statusMsg.server1, statusMsg.server2, statusMsg.chunkNo, statusMsg.offSet,
								statusMsg.len, data);

						Message reply_msg = Sender.messageToFileServer(
								statusMsg.masterServer,
								Config.getValue(statusMsg.masterServer), req_msg);

						if (reply_msg != null
								&& reply_msg.status == Message.STATUS_SUCCESS) {
							log(reply_msg.toString());
							System.out.println(reply_msg.toString());
							break;
						} else {
							if (reply_msg != null) {
								log(reply_msg.toString());
								System.out.println(reply_msg.toString());
							}
							// count++;
							// continue;
						}

						// if (reply_msg.status == Message.STATUS_SUCCESS) {
						// log("Successfully done");
						// break;
						// }
					} else {
						if (statusMsg != null)
							System.out.println(statusMsg.data);
					}

					count++;
					log("Failed: Retry " + count);
					System.out.println(metamsg.toString() + "\nFailed: Retry "
							+ count);
					Thread.sleep(2000);
				}
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
