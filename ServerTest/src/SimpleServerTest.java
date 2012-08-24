import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.jme3.network.Network;
import com.jme3.network.Server;


public class SimpleServerTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
		
		 public static void main(String[] args) throws IOException
		   {
		       final Server Server = Network.createServer(6143);
		       Runnable main = new Runnable()
		       {
		           @Override
		           public void run()
		           {
		              Server.start();
		 
		              BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		              String line = null;
		              try {
						while( (line = in.readLine()) != null ) {
						      if( "exit".equals(line) ) {
						    	  System.out.println("Exiting");
						          break;
						      }
						  }
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		           }
		       };
		       new Thread(main).start();
		   }
	}

	


