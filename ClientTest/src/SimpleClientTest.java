


import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.CartoonEdgeFilter;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.JmeContext;

public class SimpleClientTest extends SimpleApplication implements ActionListener{
	boolean connectToServer = false;
	/* Debug */
	boolean d_wireframe = false;
	
	private Vector<Vector3f> addToWorld;
	private WorldManager world;
	private Node ClumpNode;
	int ind=0;
	
	private BulletAppState bulletAppState;
	private CharacterControl player;
	Geometry mark;
	private boolean left = false, right = false, up = false, down = false, up_a = false, down_a = false;
	private Vector3f walkDirection = new Vector3f();
	
	
	
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
		ClumpNode = new Node("Clumps");
		rootNode.attachChild(ClumpNode);
		addToWorld = new Vector<Vector3f>(10);
		
		//just for outlining boxes for testing
		//FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
		//fpp.addFilter(new CartoonEdgeFilter());
		//viewPort.addProcessor(fpp);
		
		Vector3f temp = viewPort.getCamera().getLeft();
		System.out.println("cam "+temp.getX()+" "+temp.getY()+" "+temp.getZ());
		
		
		//world = new WorldManager(2,Clump.type.RANDOM);
		world = new WorldManager(65);
		
		Client myClient = null;
		initializeClasses();
		this.assetManager.registerLocator("assets/", FileLocator.class);
		viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));		
		flyCam.setMoveSpeed(100);
	
		
		/** Set up Physics */
		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		//bulletAppState.getPhysicsSpace().enableDebug(assetManager);
		viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
		
		setUpKeys();
		initMouse();
		//setUpLight();
		//initFloor();
		initCrossHairs();
		initMark();

		CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
		player = new CharacterControl(capsuleShape, 0.05f);
		player.setJumpSpeed(20);
		player.setFallSpeed(0);
		player.setGravity(30);
		player.setPhysicsLocation(new Vector3f(-10, 10, -10));

		// We attach the scene and the player to the rootNode and the physics
		// space,
		// to make them appear in the game world.

		bulletAppState.getPhysicsSpace().add(player);
		
		
		
		
		while(world.NeedUpdate()){ //add all the clumps and geo to the world
    		
    		Clump i= world.getChanged();
    		if(i==null){
    			System.out.println("Shit this shouldnt happen!");
    			break;
    		}
    		
    		Geometry geom = new Geometry("fl"+i.hashCode(), i.generateMesh());
    		geom.addControl(i);
    		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    		mat.setColor("Color", ColorRGBA.randomColor());
    		geom.setMaterial(mat);
    		geom.setLocalTranslation(i.getPos().getX()*Clump.width, i.getPos().getY()*Clump.width, i.getPos().getZ()*Clump.width);
    		ClumpNode.attachChild(geom);
    		RigidBodyControl geom_phy = new RigidBodyControl(i.getCosShape(), 0.0f);
    		geom.addControl(geom_phy);
    		System.out.println("Adding clump");
    		bulletAppState.getPhysicsSpace().add(geom_phy);
    	}
		
		
		
		if(connectToServer){
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
	private void initMouse() {
		inputManager.addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT),new KeyTrigger(KeyInput.KEY_RETURN)); // trigger 2: left-button click
		inputManager.addListener(actionListener, "Shoot");
	}
	/** Defining the "Shoot" action: Determine what was hit and how to respond. */
	private ActionListener actionListener = new ActionListener() {

		public void onAction(String name, boolean keyPressed, float tpf) {
			if (name.equals("Shoot") && !keyPressed) {
				// 1. Reset results list.
				CollisionResults results = new CollisionResults();
				// 2. Aim the ray from cam loc to cam direction.
				Ray ray = new Ray(cam.getLocation(), cam.getDirection());
				// 3. Collect intersections between Ray and Shootables in results list.
				ClumpNode.collideWith(ray, results);
				// 4. Print the results
				System.out.println("----- Collisions? " + results.size() + "-----");
				for (int i = 0; i < results.size(); i++) {
					// For each hit, we know distance, impact point, name of geometry.
					float dist = results.getCollision(i).getDistance();
					Vector3f pt = results.getCollision(i).getContactPoint();
					String hit = results.getCollision(i).getGeometry().getName();
					System.out.println("* Collision #" + i);
					System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
				}
				// 5. Use the results (we mark the hit object)
				if (results.size() > 0) {
					// The closest collision point is what was truly hit:
					CollisionResult closest = results.getClosestCollision();
					// Let's interact - we mark the hit with a red dot.
					mark.setLocalTranslation(closest.getContactPoint());
					Vector3f hitLoc = closest.getContactPoint();
					System.out.println("hl x:" + hitLoc.x + "    y:" + hitLoc.y + "    z:" + hitLoc.z);
					Vector3f hitGeom = closest.getGeometry().getLocalTranslation();
					System.out.println("WC x:" + hitGeom.x + "    y:" + hitGeom.y + "    z:" + hitGeom.z);
					Vector3f bInd = hitLoc.subtract(hitGeom);
					System.out.println("I  x:" + bInd.x + "    y:" +(bInd.y) + "    z:" + bInd.z);
					//TODO remove block
					Clump hitClump = closest.getGeometry().getControl(Clump.class); //get the clump hit
					hitClump.removeBlock(bInd); //remove block and generate new mesh
					world.changed.add(hitClump); //add to list to be updated
					
					rootNode.attachChild(mark);
					Vector3f play = player.getPhysicsLocation();
					System.out.println("Player -> x:" + Math.round(play.getX()) + "   y:" + Math.round(play.getY()) + "   z:" + Math.round(play.getZ()));
				} else {
					// No hits? Then remove the red mark.
					rootNode.detachChild(mark);
				}
			}
		}
	};
	
	private void setUpKeys() {
		inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
		inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
		inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
		inputManager.addMapping("Up_actual", new KeyTrigger(KeyInput.KEY_Q));
		inputManager.addMapping("Down_actual", new KeyTrigger(KeyInput.KEY_Z));
		inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
		inputManager.addMapping("Toggle Gravity", new KeyTrigger(KeyInput.KEY_G));
		inputManager.addListener(this, "Left");
		inputManager.addListener(this, "Right");
		inputManager.addListener(this, "Up");
		inputManager.addListener(this, "Down");
		inputManager.addListener(this, "Up_actual");
		inputManager.addListener(this, "Down_actual");
		inputManager.addListener(this, "Jump");
		inputManager.addListener(this, "Toggle Gravity");
	}
	/** A red ball that marks the last spot that was "hit" by the "shot". */
	protected void initMark() {
		Sphere sphere = new Sphere(30, 30, 0.2f);
		mark = new Geometry("BOOM!", sphere);
		Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mark_mat.setColor("Color", ColorRGBA.Red);
		mark.setMaterial(mark_mat);
	}

	/** A centered plus sign to help the player aim. */
	protected void initCrossHairs() {
		// guiNode.detachAllChildren();
		guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
		BitmapText ch = new BitmapText(guiFont, false);
		ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
		ch.setText("+"); // crosshairs
		ch.setLocalTranslation( // center
				settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2, settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
		guiNode.attachChild(ch);
	}

	/**
	 * These are our custom actions triggered by key presses. We do not walk
	 * yet, we just keep track of the direction the user pressed.
	 */
	public void onAction(String binding, boolean value, float tpf) {
		if (binding.equals("Left")) {
			left = value;
		} else if (binding.equals("Right")) {
			right = value;
		} else if (binding.equals("Up")) {
			up = value;
		} else if (binding.equals("Down")) {
			down = value;
		}else if (binding.equals("Down_actual")) {
			down_a = value;
		}else if (binding.equals("Up_actual")) {
			up_a = value;
		}else if (binding.equals("Jump")) {
			player.jump();
		} else if (binding.equals("Toggle Gravity")) {
			if(player.getGravity()==0)
			player.setGravity(30);
			else
			player.setGravity(0);
		}

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
        		/*      *********** Disabled for client branch *****************
        		final GameMessage.ClumpMessage clump = (GameMessage.ClumpMessage) m;
        		Vector3f index = new Vector3f(clump.getPos());
        		WorldCube Wtemp = world[(int) clump.getPos().getX()][(int) clump.getPos().getY()][(int) clump.getPos().getZ()] = new WorldCube(clump.getBlocks());
        		Wtemp.generateMesh();
        		addToWorld.add(index);
        		System.out.println("Rec "+clump.getPos().getX()+" "+clump.getPos().getY()+" "+clump.getPos().getZ());
        		enqueue(new Callable<Object>() {

        			public Object call() throws Exception {

        				Node temp = new Node(("Node "+String.valueOf(clump.getId())));
        				fillNode(temp,new Clump(clump.getPos(),clump.getBlocks()));
        				rootNode.attachChild(temp);
        				return null;
        			}
        		});*/
        		System.out.println("Received");
        	}

        }
    }
    @Override
	public void simpleUpdate(float tpf) {
    	
    	Vector3f camDir = cam.getDirection().clone().multLocal(0.6f);
		Vector3f camLeft = cam.getLeft().clone().multLocal(0.4f);
		walkDirection.set(0, 0, 0);
		if (left) {
			walkDirection.addLocal(camLeft);
		}
		if (right) {
			walkDirection.addLocal(camLeft.negate());
		}
		if (up) {
			walkDirection.addLocal(camDir);
		}
		if (down) {
			walkDirection.addLocal(camDir.negate());
		}
		if (up_a) {
			walkDirection.addLocal(camDir.cross(camLeft));
		}
		if (down_a) {
			walkDirection.addLocal(camDir.negate().cross(camLeft));
		}
		player.setWalkDirection(walkDirection);
		cam.setLocation(player.getPhysicsLocation());
    	
    	while(world.NeedUpdate()){ 
    		
    		Clump i= world.getChanged();
    		if(i==null){
    			System.out.println("Shit this shouldnt happen!");
    			break;
    		}
    		
    	}
    	
    }
}
