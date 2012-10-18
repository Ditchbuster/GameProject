import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;


/**
 * @author CPearson
 *
 */
public class Clump extends AbstractControl{
	static AtomicInteger nextId = new AtomicInteger(); //used for creating a unique id
	private int id;
	static final int size = 9; // how many blocks are along axis ie 3 = 3x3x3
	
	//int type; // type of the block; for now 0= air 1= solid
	float x,y,z; //position data of the 0,0,0 child
	Block[][][] child;
	public static enum type {
		RANDOM,SOLID,AIR,NULL,FLOOR;
	}
	public static final int width = 3; // world unit size of the block
	private static final Vector3f[] vertices = new Vector3f[8];
	{
		vertices[0] = new Vector3f(0, 0, 0);
		vertices[1] = new Vector3f(width, 0, 0);
		vertices[2] = new Vector3f(0, width, 0);
		vertices[3] = new Vector3f(width, width, 0);
		vertices[4] = new Vector3f(0, width, width);
		vertices[5] = new Vector3f(width, width, width);
		vertices[6] = new Vector3f(0, 0, width);
		vertices[7] = new Vector3f(width, 0, width);
	}
	// int[] indexes = { 2, 3, 0, 3, 1, 0, 5, 3, 2, 4, 5, 2, 5, 1, 3, 4, 2, 0, 7, 1, 5, 6, 4, 0, 6, 7, 5, 4, 6, 5, 7, 6, 0, 7, 0, 1 };
	private static final int[] top = {4, 5, 2, 5, 3, 2};
	private static final int[] bot = {7, 6, 0, 7, 0, 1};
	private static final int[] frt = {4, 2, 6, 6, 2, 0};
	private static final int[] bck = {5, 7, 3, 3, 7, 1};
	private static final int[] lft = {3, 1, 0, 3, 0, 2};
	private static final int[] rht = {4, 6, 5, 5, 6, 7};

	Clump CubeTop = null; // TODO: change to function calls in the class that will hold the WorldCubes
	Clump CubeBot = null;
	Clump CubeFrt = null;
	Clump CubeBck = null;
	Clump CubeLft = null;
	Clump CubeRht = null;

	Vector2f[] BlacktexCoord = new Vector2f[4];
	{
		BlacktexCoord[0] = new Vector2f(0, 0);
		BlacktexCoord[1] = new Vector2f(.5f, 0);
		BlacktexCoord[2] = new Vector2f(0, 1);
		BlacktexCoord[3] = new Vector2f(.5f, 1);
	}

	Vector2f[] GreentexCoord = new Vector2f[4];
	{
		GreentexCoord[0] = new Vector2f(.5f, 0);
		GreentexCoord[1] = new Vector2f(1, 0);
		GreentexCoord[2] = new Vector2f(.5f, 1);
		GreentexCoord[3] = new Vector2f(1, 1);
	}

	int[] Ttop = {1, 3, 0, 3, 2, 0};
	int[] Tbot = {2, 0, 1, 2, 1, 3};
	int[] Tfrt = {3, 2, 1, 1, 2, 0};
	int[] Tbck = {2, 0, 3, 3, 0, 1};
	int[] Tlft = {2, 0, 1, 2, 1, 3};
	int[] Trht = {2, 0, 3, 3, 0, 1};

	CustomMesh myMesh = null;
	CompoundCollisionShape geomShape = new CompoundCollisionShape();
	
	@Override
	  public void setSpatial(Spatial spatial) { // init routine
	    super.setSpatial(spatial);
	    
	  }
	/**
	 * Default constructor
	 * @param x
	 * @param y
	 * @param z
	 * @param child
	 */
	public Clump(int x, int y, int z, Block[][][] child) {
		this.id =nextId.incrementAndGet();
		this.x = x;
		this.y = y;
		this.z = z;
		this.child = child;
		
	}

	public Clump(int x,int y, int z) { // create clump at location with all solid
		this.id =nextId.incrementAndGet();
		this.x =x;
		this.y =y;
		this.z =z;
		child = new Block[size][size][size];
		for(int i =0; i< size; i++ ){
			for(int j =0; j< size; j++ ){
				for(int k =0; k< size; k++ ){
					child[i][j][k] = new Block();
				}
			}
			
		}
	}
	public Clump(int x,int y, int z, type t) { // create clump at location with type
		this.id =nextId.incrementAndGet();
		this.x =x;
		this.y =y;
		this.z =z;
		child = new Block[size][size][size];
		Random ranGen = null;
		
		if(t==type.RANDOM){
			ranGen = new Random();
		}
		
		
			for(int i =0; i< size; i++ ){ //must always go through all blocks to init them!! null pointer problems otherwise!
				for(int j =0; j< size; j++ ){
					for(int k =0; k< size; k++ ){
						if(t==type.SOLID){
							child[i][j][k] = new Block();
						}else if (t==type.RANDOM){
							child[i][j][k]=new Block(ranGen.nextInt(2)); // creates 1 or 0
						}else if (t==type.FLOOR){
							if(j==0){
								child[i][j][k] = new Block(1);
							}else{
								child[i][j][k] = new Block(0);
								
							}
								
						}
					}
				}
			}
		
			
		
		
	}
	public Clump(int x,int y, int z, int id) { // used for creating a clump that has been created before
		this.id =id;
		this.x =x;
		this.y =y;
		this.z =z;
		child = null;
	}

	public Clump(int x, int y, int z, int[][][] blocks) {
		this.id =nextId.incrementAndGet();
		this.x =x;
		this.y =y;
		this.z =z;
		child = new Block[size][size][size];
		for(int i =0; i< size; i++ ){
			for(int j =0; j< size; j++ ){
				for(int k =0; k< size; k++ ){
					child[i][j][k] = new Block(blocks[i][j][k]);
				}
			}
			
		}
	}

	public Clump(Vector3f pos, int[][][] blocks) {
		this.id =nextId.incrementAndGet();
		this.x =pos.getX();
		this.y =pos.getY();
		this.z =pos.getZ();
		child = new Block[size][size][size];
		for(int i =0; i< size; i++ ){
			for(int j =0; j< size; j++ ){
				for(int k =0; k< size; k++ ){
					child[i][j][k] = new Block(blocks[i][j][k]);
				}
			}
			
		}
	}

	public Clump() {
		// empty serialization const
	}
	public Vector3f getBlockInd(Vector3f hit) {
		int x=0,y=0,z=0;
		boolean xbo,ybo,zbo; // if on border need more checks
		float temp =hit.x/width;
		temp=(temp-Math.round(temp));
		System.out.println(temp);
		if(temp<0.0001&&temp>-0.0001){
			System.out.println("x Is boarder");
			xbo=true;
		}
		else{
			xbo=false;
			x=(int)hit.x/width;
		}
		temp=(hit.y-Math.round(hit.y))/width;
		System.out.println(temp);
		if(temp<0.0001&&temp>-0.0001){
			System.out.println("y Is boarder");
			ybo=true;
		}
		else{
			ybo=false;
			y=(int)hit.y/width;
		}
		temp=(hit.z-Math.round(hit.z))/width;
		System.out.println(temp);
		if(temp<0.0001&&temp>-0.0001){
			System.out.println("z Is boarder");
			zbo=true;
		}
		else{
			zbo=false;
			z=(int)hit.z/width;
		}
		
		//check borders - y and z are swapped bc of different axis
		if(xbo){
			x=Math.round(hit.x)/width;
			if(x>=size){
				x=size-1;
			}
			if(child[x][z][y].getType()!=0){
				x=x-1;
			}
		}
		if(ybo){
			y=Math.round(hit.y)/width;
			if(y>=size){
				y=size-1;
			}
			if(child[x][z][y].getType()!=0){
				y=y-1;
			}
		}
		if(zbo){
			z=Math.round(hit.z)/width;
			if(z>=size){
				z=size-1;
			}
			if(child[x][z][y].getType()!=0){
				z=z-1;
			}
		}
		System.out.println("X:"+x+"  Y:"+y+"  Z:"+z+" = blocks["+x+"]["+z+"]["+y+"]");
		return(new Vector3f(x,z,y)); // swapped because of different axis
	}
	
	public void removeBlock(Vector3f hitloc){
		int x = (int)hitloc.getX();
		int y = (int)hitloc.getY();
		int z = (int)hitloc.getZ();
		child[x][y][z].setType(1); // for now set != 0
		generateMesh();
		//update other cubes if touching
		if(x==size-1&&CubeBck!=null){
			CubeBck.generateMesh();
		}
		if(y==size-1&&CubeRht!=null){
			CubeRht.generateMesh();
		}
		if(z==size-1&&CubeTop!=null){
			CubeTop.generateMesh();
		}
		if(x==0&&CubeFrt!=null){
			CubeFrt.generateMesh();
		}
		if(y==0&&CubeLft!=null){
			CubeLft.generateMesh();
		}
		if(z==0&&CubeBot!=null){
			CubeBot.generateMesh();
		}
		
	}
	public void generateMesh() {// TODO: bulk transfer the boundary chunks blocks, optimize physics (panels, only for areas around player)
		//myMesh.setDynamic();
		myMesh=new CustomMesh(300);
		Vector3f box_size = new Vector3f(1.5f, 1.5f, 1.5f); // half size of box also used to offset box
		int temptype = 0;
		boolean addedFace = false;  // flag to see if it added a face for the block. if not dont add physics box
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				for (int z = 0; z < size; z++) { // this z is up //TODO this z is not up anymore but no changes yet to code below
					if (child[x][y][z].getType() != 0) {
						addedFace=false;
						//System.out.println("Block: " + x + " " + y + " " + z);
						temptype = 0; // default is to draw
						//geomShape.addChildShape(new BoxCollisionShape(box_size), box_size.add(x * width, z * width, y * width)); // adding physics shape - change to
						// adding panels
						if (z + 1 == size) { // see if i need to check next cube
							if (CubeTop == null) { // if none there
								temptype = 0;// draw
							} else { // else see if cube there
								temptype = CubeTop.getBlock(x, y, 0).getType();

							}
						} else if (child[x][y][z + 1].getType() == 0) {// or if not on boundary check next cube;
							temptype = 0;
						} else {
							temptype = 1;// dont draw
						}
						if (temptype == 0) {
							for (int i = 0; i < 6; i++) {
								myMesh.setTexCoord(BlacktexCoord[Ttop[i]]);
								myMesh.addVertex(vertices[top[i]].add(x * width, z * width, y * width));
								addedFace=true;
							}
							/*
							 * Plane tempP = new Plane();
							 * tempP.setPlanePoints(vertices[top[0]].add(x * width, z * width, y * width), vertices[top[1]].add(x * width, z * width, y * width),
							 * vertices[top[2]].add(x * width, z * width, y * width));
							 * geomShape.addChildShape(new PlaneCollisionShape(tempP), vertices[top[0]].add(x * width, z * width, y * width));
							 */
						}
						if (z == 0) { // see if i need to check next cube
							if (CubeBot == null) { // if none there
								temptype = 0;
							} else { // else see if cube there
								temptype = CubeBot.getBlock(x, y, size - 1).getType();
							}
						} else if (child[x][y][z - 1].getType() == 0) {// or if not on boundary check next cube;
							temptype = 0;
						} else {
							temptype = 1;
						}
						if (temptype == 0) {
							for (int i = 0; i < 6; i++) {
								myMesh.setTexCoord(BlacktexCoord[Tbot[i]]);
								myMesh.addVertex(vertices[bot[i]].add(x * width, z * width, y * width));
								addedFace=true;
							}
						}

						if (x == 0) { // see if i need to check next cube
							if (CubeFrt == null) { // if none there
								temptype = 0;
							} else { // else see if cube there
								temptype = CubeFrt.getBlock(size - 1, y, z).getType();
							}
						} else if (child[x - 1][y][z].getType() == 0) {// or if not on boundary check next cube;
							temptype = 0;
						} else {
							temptype = 1;
						}
						if (temptype == 0) {
							for (int i = 0; i < 6; i++) {
								myMesh.setTexCoord(BlacktexCoord[Tfrt[i]]);
								myMesh.addVertex(vertices[frt[i]].add(x * width, z * width, y * width));
								addedFace=true;
							}
						}

						if (x + 1 == size) { // see if i need to check next cube
							if (CubeBck == null) { // if none there
								temptype = 0;
							} else { // else see if cube there
								temptype = CubeBck.getBlock(0, y, z).getType();
							}
						} else if (child[x + 1][y][z].getType() == 0) {// or if not on boundary check next cube;
							temptype = 0;
						} else {
							temptype = 1;
						}
						if (temptype == 0) {
							for (int i = 0; i < 6; i++) {
								myMesh.setTexCoord(GreentexCoord[Tbck[i]]);
								myMesh.addVertex(vertices[bck[i]].add(x * width, z * width, y * width));
								addedFace=true;
							}
						}

						if (y + 1 == size) { // see if i need to check next cube
							if (CubeRht == null) { // if none there
								temptype = 0;
							} else { // else see if cube there
								temptype = CubeRht.getBlock(x, 0, z).getType();
							}
						} else if (child[x][y + 1][z].getType() == 0) {// or if not on boundary check next cube;
							temptype = 0;
						} else {
							temptype = 1;
						}
						if (temptype == 0) {
							for (int i = 0; i < 6; i++) {
								myMesh.setTexCoord(GreentexCoord[Trht[i]]);
								myMesh.addVertex(vertices[rht[i]].add(x * width, z * width, y * width));
								addedFace=true;
							}
						}

						if (y == 0) { // see if i need to check next cube
							if (CubeLft == null) { // if none there
								temptype = 0;
							} else { // else see if cube there
								temptype = CubeLft.getBlock(x, size - 1, z).getType();
							}
						} else if (child[x][y - 1][z].getType() == 0) {// or if not on boundary check next cube;
							temptype = 0;
						} else {
							temptype = 1;
						}
						if (temptype == 0) {
							for (int i = 0; i < 6; i++) {
								myMesh.setTexCoord(GreentexCoord[Tlft[i]]);
								myMesh.addVertex(vertices[lft[i]].add(x * width, z * width, y * width));
								addedFace=true;
							}
						}
						if(addedFace){ // if a face is visible also add in physics
							geomShape.addChildShape(new BoxCollisionShape(box_size), box_size.add(x * width, z * width, y * width));
						}
					}
				}
			}
		}
		myMesh.finish();
		myMesh.setStatic();
		

	}
	public int getId() {
		return id;
	}
	
	public Block getBlock(int x, int y, int z){
		return child[x][y][z];
	}

	public Vector3f getPos() {
		
		return new Vector3f(x,y,z);
	}
	public CustomMesh getMesh() {

		return (myMesh);
	}
	public CompoundCollisionShape getCosShape() {

		return (geomShape);
	}

	@Override
	public Control cloneForSpatial(Spatial arg0) {
		final Control myC = new Clump();
		// TODO Auto-generated method stub
		return myC;
	}
	@Override
	protected void controlRender(RenderManager arg0, ViewPort arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void controlUpdate(float tpf) {
		if(spatial != null) {
		      // spatial.rotate(tpf,tpf,tpf); // example behaviour
		    }
	}
}

