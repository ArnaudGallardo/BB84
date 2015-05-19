
public class PhotonScheme extends AbstractScheme{
	private Photon[] photons;
	
	// Given a bit value and a filter, returns a new correctly polarized photon
	private static Photon polarize(byte b, Filter f) {
		Photon tmp = new Photon();
		if(b==0) {
			if(f.getBasis() == Basis.ORTHOGONAL)
				tmp.setPolarization(Polarization.VERTICAL);
			else
				tmp.setPolarization(Polarization.SLASH);
		}
		else {
			if(f.getBasis() == Basis.ORTHOGONAL)
				tmp.setPolarization(Polarization.HORIZONTAL);
			else
				tmp.setPolarization(Polarization.ANTISLASH);
		}
		return tmp;
	}
	
	public PhotonScheme(int size, BytesScheme b, FilterScheme p) {
		super(size);
		photons = new Photon[size];
		for(int i=0;i<size;i++) {
			photons[i] = polarize(b.getByte(i),p.getFilter(i));
		}
	}

	public Photon getPhoton(int i) {
		return photons[i];
	}
		
	@Override
	public boolean equals(Scheme s) {
		// TODO Auto-generated method stub
		if(super.equals(s)) {
			if(s instanceof PhotonScheme) {
				PhotonScheme pS = (PhotonScheme) s;
				for(int i=0;i<pS.getSize();i++) {
					if(pS.getPhoton(i)!=this.getPhoton(i))
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
		tmp.append("Photons : ");
		for(int i=0;i<this.getSize()-1;i++) {
			tmp.append(this.getPhoton(i));
			tmp.append(", ");
		}
		tmp.append(this.getPhoton(this.getSize()-1));
		return tmp.toString();
	}
}
