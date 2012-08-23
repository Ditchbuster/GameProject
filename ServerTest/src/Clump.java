
/**
 * @author CPearson
 *
 */
public class Clump {
	static int size = 3; // how many blocks are along axis ie 3 = 3x3x3
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
		this.x = x;
		this.y = y;
		this.z = z;
		this.child = child;
	}

	public Clump(int x,int y, int z) { // create clump at location with all solid
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
	
	
	
	
	
	
}
