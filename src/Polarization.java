
public enum Polarization {
	NONE,
	HORIZONTAL,
	VERTICAL,
	ANTISLASH,
	SLASH;
	
	public static Polarization random() {
		int i = (int)(Math.random()*4);
		if(i==0)
			return Polarization.HORIZONTAL;
		if(i==1)
			return Polarization.VERTICAL;
		if(i==2)
			return Polarization.SLASH;
		else
			return Polarization.ANTISLASH;
	}
}
