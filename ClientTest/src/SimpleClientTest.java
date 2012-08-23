import java.io.IOException;

import com.jme3.app.SimpleApplication;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.system.JmeContext;

public class SimpleClientTest extends SimpleApplication{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SimpleClientTest app = new SimpleClientTest();
		app.start(JmeContext.Type.Display);
	}

	@Override
	public void simpleInitApp() {
		// TODO Auto-generated method stub
		Client myClient = null;
		try {
			myClient = Network.connectToServer("localhost", 6143);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myClient.start();
	}

}
