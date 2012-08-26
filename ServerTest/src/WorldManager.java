import java.util.ArrayList;

public class WorldManager {
	private ArrayList<Clump> world; //list of all Clumps

	/**
	 * Currently inits a default flat area of a certain size
	 */
	public WorldManager() {
		world = new ArrayList<Clump>(100); // init the list of clumps
		initFlatWorld();
	}

	private void initFlatWorld() {
		int size = 3; //how many clumps in each x and y to go
		for(int i = 0; i< size;i++){
			for(int j = 0; j< size;j++){
				world.add(new Clump(i*Clump.size,j*Clump.size,0,Clump.type.RANDOM)); //create clumps based on clump.type
			}
		}
	}
	
	public ArrayList<Clump> getWorld(){
		return world;
	}
	public Clump getClump(int i){
		return world.get(i);
	}
	
}
