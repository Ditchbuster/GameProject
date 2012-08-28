import java.util.Arrays;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;


public class GameMessage {
	 @Serializable
	    public static class ChatMessage extends AbstractMessage {

	        private String name;
	        private String message;

	        public ChatMessage() {
	        }

	        public ChatMessage(String name, String message) {
	            setName(name);
	            setMessage(message);
	        }

	        public void setName(String name) {
	            this.name = name;
	        }

	        public String getName() {
	            return name;
	        }

	        public void setMessage(String s) {
	            this.message = s;
	        }

	        public String getMessage() {
	            return message;
	        }

	        public String toString() {
	            return name + ":" + message;
	        }
	    }
	 @Serializable
	    public static class ClumpMessage extends AbstractMessage {
	    	private int id;
	    	private int size;
	    	private int[][][] blocks;
	    	Vector3f pos;
	    	
	    	public ClumpMessage() {
			}
			public ClumpMessage(int id, int size,Vector3f pos, int[][][] blocks) {
				this.id = id;
				this.size = size;
				this.blocks = blocks;
				this.pos = pos;
			}
			public int getSize() {
	    		return size;
	    	}
	    	public void setSize(int size) {
	    		this.size = size;
	    	}
	    	public int getId() {
	    		return id;
	    	}
	    	public void setId(int id) {
	    		this.id = id;
	    	}
	    	public int[][][] getBlocks() {
	    		return blocks;
	    	}
	    	public void setBlocks(int[][][] blocks) {
	    		this.blocks = blocks;
	    	}
	    	public Vector3f getPos() {
				return pos;
			}
			public void setPos(Vector3f pos) {
				this.pos = pos;
			}
			@Override
			public String toString() {
				return "ClumpMessage [id=" + id + ", size=" + size + ", blocks="
						+ Arrays.toString(blocks) + "]";
			}
	    	 
	    }
}
