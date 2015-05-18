
public class Filter {
	private Basis basis;
	
	public Filter(Basis basis) {
		this.setBasis(basis);
	}

	public Basis getBasis() {
		return basis;
	}

	public void setBasis(Basis basis) {
		this.basis = basis;
	}
	
	public String toString() {
		return this.getBasis().toString();
	}
	
	public boolean equals(Filter f)
	{
		if(this.basis == f.basis)
			return true;
		return false;
	}
	
	// Returns the polarization of the read photon and changes it if read with the wrong filter
	public Polarization readPolarPhoton(Photon photon) {
		if(this.basis == Basis.ORTHOGONAL) {
			if(photon.getPolarization()==Polarization.VERTICAL || photon.getPolarization()==Polarization.HORIZONTAL) {
				return photon.getPolarization();
			}
			else {
				int rand = (int)(Math.random()*2);
				if(rand==0) {
					photon.setPolarization(Polarization.VERTICAL);
					return Polarization.VERTICAL;
				}
				else {
					photon.setPolarization(Polarization.HORIZONTAL);
					return Polarization.HORIZONTAL;
				}
			}
		}
		else {
			if(photon.getPolarization()==Polarization.SLASH || photon.getPolarization()==Polarization.ANTISLASH) {
				return photon.getPolarization();
			}
			else {
				int rand = (int)(Math.random()*2);
				if(rand==0) {
					photon.setPolarization(Polarization.SLASH);
					return Polarization.SLASH;
				}
				else {
					photon.setPolarization(Polarization.ANTISLASH);
					return Polarization.ANTISLASH;
				}
			}
		}
	}
}
