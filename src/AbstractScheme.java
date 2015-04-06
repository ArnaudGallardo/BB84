
public abstract class AbstractScheme implements Scheme {
	private int size;
	
	public AbstractScheme(int size) {
		this.size = size;
	}
	
	public int getSize() {
		return size;
	}
	
	public String toString() {
		return "Scheme : ";
	}
	
	public boolean equals(Scheme s) {
		if(s.getSize()==this.getSize())
			return true;
		return false;
	}
}
