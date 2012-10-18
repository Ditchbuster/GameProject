
public class Block {
	int type;

	public Block(int type) {
		this.type = type;
	}
	public Block(){
		type=1; //solid TODO define types;
	}
	
	public int getType(){
		return type;
	}
	public void setType(int i) {
		type = i;
	}
}
