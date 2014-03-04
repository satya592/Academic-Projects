public class Resource implements Cloneable {
	public Integer Rno;
	public Integer RTime;

	@Override
	public Object clone() {
		try {
			Resource res = (Resource) super.clone();
			// res.Rno = (Integer) Rno.clone();
			return res;
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	public Resource(int Rno, int RTime) {
		this.Rno = Rno;
		this.RTime = RTime;
		// this.Displayln();
	}

	public Resource() {
		this(-1, -1);
	}

	public void Displayln() {
		System.out.println("[R" + Rno + "," + RTime + "]");
	}

	public void Display() {
		System.out.print("[R" + Rno + "," + RTime + "]");
	}

	public String getResource() {
		return ("[R" + Rno + "," + RTime + "]");
	}

}
