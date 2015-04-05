
public enum Basis {
	HORTOGONAL,
	DIAGONAL;
	
	public static Basis itobasis(int i) {
		if(i==0)
			return Basis.HORTOGONAL;
		else
			return Basis.DIAGONAL;
	}
}
