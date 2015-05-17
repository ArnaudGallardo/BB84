
public class BytesScheme extends AbstractScheme{
	private byte[] bytes;
	
	private static int FIBER = 3; // Percentage of errors induced by the noise in optic fiber
	
	// Given a photon and a filter, returns the bit value (accurate if the filter is the right one, randomly otherwise) of a photon
	private static byte unpolarize(Photon p, Filter f) {
		Polarization po = f.readPolarPhoton(p);
		byte tmp;
		if(f.getBasis() == Basis.HORTOGONAL) {
			if(po == Polarization.VERTICAL)
				tmp = 0;
			else
				tmp = 1;
		}
		else {
			if(po == Polarization.SLASH)
				tmp = 0;
			else
				tmp = 1;
		}
		
		// Errors induced by the noise in the fiber
		int random = (int) (Math.random() * 100);
		if (random < FIBER)
		{
			tmp = (byte) (Math.random() * 2);
		}
		return tmp;
	}
	
	// Creates a byte scheme of a given size filled with bytes that are randomly initialized
	public BytesScheme(int size) {
		super(size);
		bytes = new byte[size];
		for(int i=0;i<size;i++) {
			bytes[i] = (byte)(Math.random()*2);
		}
	}
	
	// Creates a byte scheme given an array of bytes
	public BytesScheme(byte[] b) {
		super(b.length);
		bytes = new byte[b.length];
		for(int i=0;i<b.length;i++) {
			bytes[i] = b[i];
		}
	}
	
	// Creates of byte scheme given a size, a photon scheme and a filter scheme (reading of a photon p with a filter f)
	public BytesScheme(int size, PhotonScheme ph, FilterScheme po) {
		super(size);
		bytes = new byte[size];
		for(int i=0;i<size;i++) {
			bytes[i]=unpolarize(ph.getPhoton(i), po.getFilter(i));
			
		}
	}

	// Return the bytes of a byte scheme into an array of bytes
	public byte[] getBytes() {
		byte[] tmp = new byte[this.bytes.length];
		for(int i=0;i<this.bytes.length;i++){
			tmp[i]=this.bytes[i];
		}
		return tmp;
	}
	
	public byte getByte(int i) {
		return bytes[i];
	}
	
	public void setByte(int i, byte b) {
		bytes[i] = b;
	}
	
	@Override
	public boolean equals(Scheme s) {
		// TODO Auto-generated method stub
		if(super.equals(s)) {
			if(s instanceof BytesScheme) {
				BytesScheme pS = (BytesScheme) s;
				for(int i=0;i<pS.getSize();i++) {
					if(pS.getByte(i)!=this.getByte(i))
						return false;
				}
				return true;
			}
			return false;
		}
		return false;
	}
	
	public String toString() {
		StringBuffer tmp = new StringBuffer();
		tmp.append(super.toString());
		tmp.append("Bytes : ");
		for(int i=0;i<this.getSize()-1;i++) {
			tmp.append(this.getByte(i));
			tmp.append(", ");
		}
		tmp.append(this.getByte(this.getSize()-1));
		return tmp.toString();
	}
	
	
//	//A MODIFIER, bs == index donc ...
//	public boolean eveDetected(BytesScheme bs,int[] index,int percentOfKey) {
//		int nbOfVerification = (percentOfKey*index.length)/100;
//		int[] checked = new int[nbOfVerification];
//		if(nbOfVerification<index.length) {
//			for(int i=0;i<nbOfVerification;i++) {
//				int random = (int)(Math.random()*index.length);
//				while(isInArray(random,checked)) {
//					random = (int)(Math.random()*index.length);
//				}
//				checked[i] = random;
//				int tmp = index[random];
//				index[random] = -1; //ATTENTION
//				if(this.getByte(tmp)!=bs.getByte(tmp))
//					return true;
//			}
//			return false;
//		}
//		return true;
//	}
	
	
	// The object this method applies to is Alice's original bit key
	// Given an array of Bob's bit measurement with the 
	public boolean detection(int[] arrayWithDiscards, int nbSacrificed)
	{
		assert(this.getSize() == arrayWithDiscards.length); // Asserts that the byte scheme and the array are of same length
		// Otherwise, method inadequate
		
		int cpt = 0; // Counter of sacrificed photons (= verifications)
		boolean detected = false; // Is Eve detected?
		
		while(nbSacrificed > 0 && cpt <= nbSacrificed && !detected && stillContainsBits(arrayWithDiscards))
		{
			int i = (int) (Math.random() * arrayWithDiscards.length); // Generates a random index in arrayWithDiscards
			while(arrayWithDiscards[i] == -1) // While -1, that's to say while the randomly generated 
				// index corresponds to an already discarded bit measurement (during the filter exchange or later)
			{
				i = (int) (Math.random() * arrayWithDiscards.length);
			}
			
			if(arrayWithDiscards[i] == this.getByte(i))
			{
				cpt++;
				arrayWithDiscards[i] = -1;
			}
			else 
				detected = true;
		}
		return detected;
	}
	
	public static boolean stillContainsBits(int[] arrayWithDiscards)
	{
		for(int i = 0; i < arrayWithDiscards.length; i++)
		{
			if(arrayWithDiscards[i] != -1)
				return true;
		}
		return false;
	}
	
	// Returns an integer array containing Bob's bit measurements (0 or 1) at the indexes where Bob used the same filter as Alice
	// Or -1 at the indexes where they didn't use the same filters
	public  int[] arrayWithDiscards(FilterScheme fs1, FilterScheme fs2)
		{
			assert(fs1.getSize() == fs2.getSize());
			int result[] = new int[fs1.getSize()];
			for(int i = 0; i < result.length; i++)
			{
				if(fs1.getFilter(i).equals(fs2.getFilter(i)))
				{
					if(this.getByte(i) == 1)
						result[i] = 1;
					else
						result[i] = 0;
				}
				else
					result[i] = -1;
			}
			return result;
		}
	
		
	public byte[] cleanKeyWithIndex(int[] index) {
		int nb = 0;
		for(int i=0;i<index.length;i++) {
			if (index[i]==-1)
				nb++;
		}
		byte[] result = new byte[this.getSize()-nb];
		int tmp = 0;
		for(int j=0;j<this.getSize();j++) {
			if(index[j]!=-1) {
				result[j-tmp] = this.getByte(j);
			}
			else
				tmp++;
		}
		return result;
	}
	
	private boolean isInArray(int i, int[] array) {
		for(int j=0;j<array.length;j++) {
			if (i==array[j])
				return true;
		}
		return false;
	}
	
	public BytesScheme getFinalKey(FilterScheme fs1, FilterScheme fs2) {
		int[] indexTab = fs1.indexOfIden(fs2);
		BytesScheme result = new BytesScheme(indexTab.length);
		for(int i=0;i<result.getSize();i++) {
			result.setByte(i, this.getByte(indexTab[i]));
		}
		return result;
	}
}
