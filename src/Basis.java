
public enum Basis {
	ORTHOGONAL,
	DIAGONAL;
	
	public static Basis random() {
		int i = (int)(Math.random()*2);
		if(i==0)
			return Basis.ORTHOGONAL;
		else
			return Basis.DIAGONAL;
	}
}
