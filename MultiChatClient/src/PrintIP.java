import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;


public class PrintIP {
public static void main(String[] args) throws Exception{
	Enumeration<?> e=NetworkInterface.getNetworkInterfaces();
	while(e.hasMoreElements())
    {
        NetworkInterface n=(NetworkInterface) e.nextElement();
        Enumeration<?> ee = n.getInetAddresses();
        while(ee.hasMoreElements())
        {
            InetAddress i= (InetAddress) ee.nextElement();
            System.out.println(i.getHostAddress());
        }
    }
}
}
