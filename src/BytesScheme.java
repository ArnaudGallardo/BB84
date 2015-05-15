
public class BytesScheme extends AbstractScheme{
	private byte[] bytes;
	
	private static int FIBER = 3; // Percentage of errors induced by the noise in optic fiber
	
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
	
	public BytesScheme(int size) {
		super(size);
		bytes = new byte[size];
		for(int i=0;i<size;i++) {
			bytes[i] = (byte)(Math.random()*2);
		}
	}
	
	public BytesScheme(byte[] b) {
		super(b.length);
		bytes = new byte[b.length];
		for(int i=0;i<b.length;i++) {
			bytes[i] = b[i];
		}
	}
	
	public BytesScheme(int size, PhotonScheme ph, FilterScheme po) {
		super(size);
		bytes = new byte[size];
		for(int i=0;i<size;i++) {
			bytes[i]=unpolarize(ph.getPhoton(i), po.getFilter(i));
			
		}
	}

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
	//A MODIFIER, bs == index donc ...
	public boolean eveDetected(BytesScheme bs,int[] index,int percentOfKey) {
		int nbOfVerification = (percentOfKey*index.length)/100;
		int[] checked = new int[nbOfVerification];
		if(nbOfVerification<index.length) {
			for(int i=0;i<nbOfVerification;i++) {
				int random = (int)(Math.random()*index.length);
				while(isInArray(random,checked)) {
					random = (int)(Math.random()*index.length);
				}
				checked[i] = random;
				int tmp = index[random];
				index[random] = -1; //ATTENTION
				if(this.getByte(tmp)!=bs.getByte(tmp))
					return true;
			}
			return false;
		}
		return true;
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
