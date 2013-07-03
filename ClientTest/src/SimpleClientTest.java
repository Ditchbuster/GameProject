


import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.JmeContext;

public class SimpleClientTest extends SimpleApplication {
	GameAppState myGame;
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		SimpleClientTest app = new SimpleClientTest();
		
		app.start(JmeContext.Type.Display);
	}

	@Override
	public void simpleInitApp() {
		myGame = new GameAppState();
		stateManager.attach(myGame);
		
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
	
	
	
	
    @Override
	public void simpleUpdate(float tpf) {
    	
    	
    	
    	
    }
    
   //get and set for exposing to appstates 
   public  FlyByCamera getFlyCam(){ 
    	return flyCam;
    }
}
