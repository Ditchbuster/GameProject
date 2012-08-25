import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author CPearson
 *
 */
public class Clump {
	static AtomicInteger nextId = new AtomicInteger(); //used for creating a unique id
	private int id;
	static final int size = 3; // how many blocks are along axis ie 3 = 3x3x3
	//int type; // type of the block; for now 0= air 1= solid
	int x,y,z; //position data of the 0,0,0 child
	Block[][][] child;
	
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

	public int getId() {
		return id;
	}
	
	public Block getBlock(int x, int y, int z){
		return child[x][y][z];
	}

	
}
