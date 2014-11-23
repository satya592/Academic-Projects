public class Predictor implements Runnable {
	Node node;

	Predictor(Node node) {
		this.node = node;
	}

	@Override
	public void run() {
		try {
			while (!node.terminated) {
				if (node.active) {

					float rand = RandomNumber.randomFloat(0, 100, 0.01f);
					Message msg = null;
					if (rand < 0.1f || node.totalComputationMessages >= 25) {// Idle
						node.active = false;
						long acks = 0;
						node.recordMsg(msg, "From Active to Idle");
						synchronized (node.pendingACK) {

							for (String key : node.neighboursNames) {
								if (key.equals(node.parent))
									acks = node.pendingACK.get(key) - 1;
								else
									acks = node.pendingACK.get(key);

								while (acks-- > 0) {
									msg = new Message(Message.ACK,
											node.processID, key);
									node.recordMsg(msg, "");
									node.outQ.add(msg);
								}

								if (key.equals(node.parent))
									node.pendingACK.put(key, 1l);
								else
									node.pendingACK.put(key, 0l);
							}
						}

						if (node.isReadyToLeave()) {

							System.out.println("This should be one(Predictor):"
									+ node.pendingACK.get(node.parent));
							node.pendingACK.put(node.parent, 0l);
							msg = new Message(Message.ACK, node.processID,
									node.parent);
							node.recordMsg(msg,
									"Sending ACK to parent and detaching from tree "
											+ node.parent);
							node.outQ.add(msg);

							msg = new Message(Message.LEV, node.processID,
									node.parent);
							node.outQ.add(msg);
							node.parent = null;
						}

						if (node.initiator) {
							if (node.firstTime) {
								node.active = true;
								continue;
							} else if (node.isTerminated()) {
								synchronized (node.outQ) {
									msg = new Message(Message.TERMINATE);
									node.recordMsg(null, "Termination");
									node.outQ.add(msg);
								}
							}
						}
						// ACTIVE
					} else {
						node.firstTime = false;
						int rand1 = RandomNumber.randomInt(0,
								node.otherPeers - 1);
						node.pendingAcknowledgement++;
						node.totalComputationMessages++;
						msg = new Message(Message.MSG, node.processID,
								node.neighboursNames[rand1]);
						node.recordMsg(msg, "");
						node.outQ.add(msg);
					}

					Thread.sleep((long) RandomNumber.randomFloat(250, 1000, 1));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
