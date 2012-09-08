import java.util.ArrayList;
import java.util.Random;

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
	public WorldManager(int size){
		world = new ArrayList<Clump>(size*size);
		algoGen(size);
	}

	public void initWorld(Clump.type t) { // TODO change back to private once testing done
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
	/**
	 * Generate a world with a more fluid look
	 * @param size is the length of one size of the world needs to be power of 2 +1
	 */
	public void algoGen(int size){
		int[][] wH = new int[size][size];
		Random ranGen = new Random();
		/*for(int i=0;i<size;i++){
			for(int j =0;j<size;j++){

			}
		}*/

		int total = 0;
		total += wH[0][0] = ranGen.nextInt(50);
		total += wH[0][size-1] = ranGen.nextInt(50);
		total += wH[size-1][0] = ranGen.nextInt(50);
		total += wH[size-1][size-1] = ranGen.nextInt(50);
		wH[(size-1)/2][(size-1)/2]=total/4;


		int side = (size-1)/2;
		
		while(side>0){
			boolean joffset=true;
			int j = side;
			int i = 0;
			while(i<size){
				while(j<size){
					int added=0;
					total=0;
					if(i+side<size){//no minus one for inculsion of edge
						total+=wH[i+side][j]; //south
						added++;
					}
					if(j+side<size){//ditto
						total+=wH[i][j+side]; //east
						added++;
					}
					if(!(j-side<0)){
						total+=wH[i][j-side]; //west
						added++;
					}
					if(!(i-side<0)){
						total+=wH[i-side][j]; //north
						added++;
					}
					wH[i][j]=total/added; //average of the four points
					System.out.println(i+" "+j);
					j+=2*side; //add twice the side to get to next midpoint
				}
				i+=side;
				if(joffset){
					j=0;
					joffset=false;
				}
				else{
					j=side;
					joffset=true;
				}
			}
			if(side!=1){
				side=side/2;
				for(i=side;i<size;i+=side*2){
					for(j=side;j<size;j+=side*2){
						wH[i][j]=(wH[i+side][j+side]+wH[i-side][j+side]+wH[i-side][j-side]+wH[i+side][j-side])/4;
						System.out.println(i+":"+j);
					}
				}
			}else{
				side=0;
			}

		}
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				System.out.print(wH[i][j]+" ");
			}
			System.out.println(":");
		}
	}

	public ArrayList<Clump> getWorld(){
		return world;
	}
	public Clump getClump(int i){
		return world.get(i);
	}

}
