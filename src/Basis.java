
public enum Basis {
	HORTOGONAL,
	DIAGONAL;
	
	public static Basis random() {
		int i = (int)(Math.random()*2);
		if(i==0)
			return Basis.HORTOGONAL;
		else
			return Basis.DIAGONAL;
	}
}
