import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

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
		total += wH[0][0] = ranGen.nextInt(250);
		total += wH[0][size-1] = ranGen.nextInt(250);
		total += wH[size-1][0] = ranGen.nextInt(250);
		total += wH[size-1][size-1] = ranGen.nextInt(250);
		wH[(size-1)/2][(size-1)/2]=total/4;


		int side = (size-1)/2;
		int H = 250;
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
					total=(total/added)+ranGen.nextInt(2*H)-H; //average of the four points
					if(total>250) total=250;
					if(total<0) total=0;
					wH[i][j]=total;
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
				H=H/2;
				if(H==0){
					H=1;
				}
				System.out.println("H:"+H);
				for(i=side;i<size;i+=side*2){
					for(j=side;j<size;j+=side*2){
						total=((wH[i+side][j+side]+wH[i-side][j+side]+wH[i-side][j-side]+wH[i+side][j-side])/4)+ranGen.nextInt(2*H)-H;
						if(total>250) total=250;
						if(total<0) total=0;
						wH[i][j]=total;
						System.out.println(i+":"+j);
					}
				}
			}else{
				side=0;
			}

		}
		BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				System.out.print(wH[i][j]+" ");
				int temp = wH[i][j];
				if(temp<0)temp=0;
				if(temp>255)temp=255;
				bi.setRGB(i, j, temp+temp*256+temp*256*256);
			}
			
			System.out.println(":");
		}
		try {
			ImageIO.write(bi, "PNG", new File("C:\\cpearson\\yourImageName.PNG"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<Clump> getWorld(){
		return world;
	}
	public Clump getClump(int i){
		return world.get(i);
	}

}
