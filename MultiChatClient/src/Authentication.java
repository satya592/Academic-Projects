import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Authentication {
	Map<String, String> auth;
	public static Authentication instance = null;

	private Authentication() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("auth.txt"));
			StringTokenizer stkn = null;
			String currentLine = null;
			String id = null, pwd = null;
			auth = new HashMap<String, String>();
			while ((currentLine = br.readLine()) != null) {
				stkn = new StringTokenizer(currentLine, " :");
				id = stkn.nextToken();
				pwd = stkn.nextToken();
				auth.put(id, pwd);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean validation(String auth) {
		if (auth != null) {
			StringTokenizer stkn = new StringTokenizer(auth, "=");
			String id = "";
			String pwd = "";
			if (stkn.hasMoreTokens()) {
				id = stkn.nextToken();
			}
			if (stkn.hasMoreTokens()) {
				pwd = stkn.nextToken();
			}
			id = id.trim();
			pwd = pwd.trim();
			String sID = "";
			if (id != "" && pwd != "") {
				if (this.auth.size() != 0) {
					sID = this.auth.get(id);
					if (sID != null && sID.equals(pwd)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	static Authentication getInstance() {
		if (instance == null)
			instance = new Authentication();
		return instance;
	}

}
