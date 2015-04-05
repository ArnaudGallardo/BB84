
public enum Polarization {
	NONE,
	HORIZONTAL,
	VERTICAL,
	ANTISLASH,
	SLASH;
	
	public static Polarization itopolar(int i) {
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
