import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.math.Vector3f;
import com.jme3.network.*;
import com.jme3.network.serializing.Serializable;
import com.jme3.network.serializing.Serializer;





public class SimpleServerTest {
	
	//static WorldManager world = new WorldManager(Clump.type.RANDOM);
	static WorldManager world = new WorldManager(257);
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
		
	public static void main(String[] args) throws IOException
	{
		world.initWorld(Clump.type.RANDOM);
		Serializer.registerClass( GameMessage.ChatMessage.class);
		Serializer.registerClass( GameMessage.ClumpMessage.class);
		final Server Server = Network.createServer(6143);
		conHandler conH = new conHandler();
		Server.addConnectionListener(conH);
		Runnable main = new Runnable()
		{
			@Override
			public void run()
			{
				Server.start();
				ChatHandler handler = new ChatHandler();
		        Server.addMessageListener(handler, GameMessage.ChatMessage.class);
		        
		        
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				String line = null;
				try {
					while( (line = in.readLine()) != null ) {
						if( "exit".equals(line) ) {
							System.out.println("Exiting");
							break;
						}
						else if( "make".equals(line) ) {

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
	
	private static class conHandler implements ConnectionListener {

		@Override
		public void connectionAdded(Server arg0, HostedConnection arg1) {
			// TODO Optimize how to send the blocks or clumps to the client
			System.out.println("Connection added!");
			Clump temp = world.getClump(0);
			int[][][] blocks= new int[Clump.size][Clump.size][Clump.size];
			 Logger.getLogger(SimpleServerTest.class.getName()).log(Level.INFO, "Starting world sending");
			 for(Iterator<Clump> iClump = world.getWorld().iterator();iClump.hasNext();){
				 temp = iClump.next();
				 for(int i=0; i<Clump.size;i++){
					 for(int j =0; j<Clump.size;j++){
						 for(int k =0; k<Clump.size;k++){
							 blocks[i][j][k] = temp.getBlock(i, j, k).getType();

						 }
					 }
				 }
				 GameMessage.ClumpMessage clumpmess = new  GameMessage.ClumpMessage(temp.getId(),Clump.size,temp.getPos(),blocks);
				 arg1.send(clumpmess);
			 }
			 Logger.getLogger(SimpleServerTest.class.getName()).log(Level.INFO, "Finished finished sending");
		}

		@Override
		public void connectionRemoved(Server arg0, HostedConnection arg1) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private static class ChatHandler implements MessageListener<HostedConnection> {

        public ChatHandler() {
        }

        public void messageReceived(HostedConnection source, Message m) {
            if (m instanceof GameMessage.ChatMessage) {
                // Keep track of the name just in case we 
                // want to know it for some other reason later and it's
                // a good example of session data
                source.setAttribute("name", ((GameMessage.ChatMessage) m).getName());

                System.out.println("Broadcasting:" + m + "  reliable:" + m.isReliable());

                // Just rebroadcast... the reliable flag will stay the
                // same so if it came in on UDP it will go out on that too
                source.getServer().broadcast(m);
            } else {
                System.err.println("Received odd message:" + m);
            }
        }
    }
	
	
	}


	


