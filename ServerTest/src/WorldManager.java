import java.util.ArrayList;

public class WorldManager {
	private ArrayList<Clump> world; //list of all Clumps

	/**
	 * Currently inits a default flat area of a certain size
	 */
	public WorldManager() {
		world = new ArrayList<Clump>(100); // init the list of clumps
		initWorld(Clump.type.SOLID);
	}
	public WorldManager(Clump.type t){
		world = new ArrayList<Clump>(100); // init the list of clumps
		initWorld(t);
	}

	private void initWorld(Clump.type t) {
		if(t!=Clump.type.NULL){
			int size = 4; //how many clumps in each x and y to go
			for(int i = 0; i< size;i++){
				for(int j = 0; j< size;j++){
					for(int k =0; k<size;k++){
						world.add(new Clump(i*Clump.size,k*Clump.size,j*Clump.size,t)); //create clumps based on clump.type
					}
				}
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
