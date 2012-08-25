import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.jme3.network.*;
import com.jme3.network.serializing.Serializable;
import com.jme3.network.serializing.Serializer;



public class SimpleServerTest {
	
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
		
	public static void main(String[] args) throws IOException
	{
		Serializer.registerClass(ChatMessage.class);
		final Server Server = Network.createServer(6143);
		Runnable main = new Runnable()
		{
			@Override
			public void run()
			{
				Server.start();
				ChatHandler handler = new ChatHandler();
		        Server.addMessageListener(handler, ChatMessage.class);
		        
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
		WorldManager world = new WorldManager();
		new Thread(main).start();


	}
	
	private static class ChatHandler implements MessageListener<HostedConnection> {

        public ChatHandler() {
        }

        public void messageReceived(HostedConnection source, Message m) {
            if (m instanceof ChatMessage) {
                // Keep track of the name just in case we 
                // want to know it for some other reason later and it's
                // a good example of session data
                source.setAttribute("name", ((ChatMessage) m).getName());

                System.out.println("Broadcasting:" + m + "  reliable:" + m.isReliable());

                // Just rebroadcast... the reliable flag will stay the
                // same so if it came in on UDP it will go out on that too
                source.getServer().broadcast(m);
            } else {
                System.err.println("Received odd message:" + m);
            }
        }
    }
	
	 @Serializable
	    public static class ChatMessage extends AbstractMessage {

	        private String name;
	        private String message;

	        public ChatMessage() {
	        }

	        public ChatMessage(String name, String message) {
	            setName(name);
	            setMessage(message);
	        }

	        public void setName(String name) {
	            this.name = name;
	        }

	        public String getName() {
	            return name;
	        }

	        public void setMessage(String s) {
	            this.message = s;
	        }

	        public String getMessage() {
	            return message;
	        }

	        public String toString() {
	            return name + ":" + message;
	        }
	    }
	}


	


