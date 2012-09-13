import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializable;
import com.jme3.network.serializing.Serializer;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.CartoonEdgeFilter;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.JmeContext;

public class SimpleClientTest extends SimpleApplication{
	/* Debug */
	boolean d_wireframe = false;
	
	
	public Clump [][] twodworld;
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
		
		//just for outlining boxes for testing
		FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
		fpp.addFilter(new CartoonEdgeFilter());
		viewPort.addProcessor(fpp);
		Vector3f temp = viewPort.getCamera().getLeft();
		System.out.println("cam "+temp.getX()+" "+temp.getY()+" "+temp.getZ());
		// TODO get settings from server so not hard coded
		/*int size = 3;
		this.twodworld = new Clump[size][size];
		for (int i=0;i<size;i++){
			for (int j=0;j<size;j++){
				twodworld[i][j]=new Clump(i,j,0);
			}
		}*/
		
		
		Client myClient = null;
		initializeClasses();
		this.assetManager.registerLocator("assets/", FileLocator.class);
		viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));		
		flyCam.setMoveSpeed(50);
	
		
	
		try {
			myClient = Network.connectToServer("localhost", 6143);
			myClient.addMessageListener(new ChatHandler(), GameMessage.ChatMessage.class);
			myClient.addMessageListener(new ClumpHandler(), GameMessage.ClumpMessage.class);
			myClient.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void destroy(){
		super.destroy();
		System.exit(0);
	}
	public void initFloor() {
		//RigidBodyControl floor_phy;
		Box floor;
		Material floor_mat;
		floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		floor_mat.setColor("Color", ColorRGBA.DarkGray);
		floor = new Box(Vector3f.ZERO, 100f, 0.1f, 50f);
		// floor.scaleTextureCoordinates(new Vector2f(3, 6));
		Geometry floor_geo = new Geometry("Floor", floor);
		floor_geo.setMaterial(floor_mat);
		floor_geo.setLocalTranslation(0, -0.1f, 0);
		this.rootNode.attachChild(floor_geo);
		/* Make the floor physical with mass 0.0f! */
		//floor_phy = new RigidBodyControl(0.0f);
		//floor_geo.addControl(floor_phy);
		//bulletAppState.getPhysicsSpace().add(floor_phy);
		
	}
	public void fillNode(Node parent, Clump in){
		Box b = new Box(.5f, .5f, .5f);
		Geometry[][][] geom = new Geometry[Clump.size][Clump.size][Clump.size];
		Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.randomColor());
		if(d_wireframe){
		mat.getAdditionalRenderState().setWireframe(true);}
		for(int i =0; i<Clump.size;i++){
			for(int j =0; j<Clump.size;j++){
				for(int k =0; k<Clump.size;k++){
					if(in.getBlock(i, j, k).getType()==1){
					geom[i][j][k]=new Geometry("Box "+ String.valueOf(i)+" "+String.valueOf(j)+" "+String.valueOf(k),b);
					geom[i][j][k].setMaterial(mat);
					geom[i][j][k].setLocalTranslation(i, j, k);
					parent.attachChild(geom[i][j][k]);
					}
				}
			}
		}
		parent.setLocalTranslation(in.getPos()); // set the node offset in the world
	}
	
	
	/* ****************************************Server Shit***************************************/
	
	public static void initializeClasses() {
        // Doing it here means that the client code only needs to
        // call our initialize. 
        Serializer.registerClass(GameMessage.ChatMessage.class);
        Serializer.registerClass(GameMessage.ClumpMessage.class);
    }
 
    private class ChatHandler implements MessageListener<Client> {

        public void messageReceived(Client source, Message m) {
        	if (m instanceof GameMessage.ChatMessage) {
        		GameMessage.ChatMessage chat = (GameMessage.ChatMessage) m;

            System.out.println("Received:" + chat);
        	}
           
        }
    }
    private class ClumpHandler implements MessageListener<Client> {

        public void messageReceived(Client source, Message m) {
        	if (m instanceof GameMessage.ClumpMessage) {
        		final GameMessage.ClumpMessage clump = (GameMessage.ClumpMessage) m;
        		System.out.println("Rec "+clump.getPos().getX()+" "+clump.getPos().getY()+" "+clump.getPos().getZ());
        		enqueue(new Callable<Object>() {

        			public Object call() throws Exception {

        				Node temp = new Node(("Node "+String.valueOf(clump.getId())));
        				fillNode(temp,new Clump(clump.getPos(),clump.getBlocks()));
        				rootNode.attachChild(temp);
        				return null;
        			}
        		});
        		System.out.println("Received:" + clump);
        	}

        }
    }

}
