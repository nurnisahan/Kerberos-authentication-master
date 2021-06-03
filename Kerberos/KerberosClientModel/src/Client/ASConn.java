package Client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ASConn extends SocketConn {
	
	public ASConn(){
		try {
			socket = new Socket("192.168.204.1", 7777);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
