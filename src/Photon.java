
public class Photon {
	private Polarization polarization;
	
	public Photon() {
		this.setPolarization(Polarization.NONE);
	}

	public Polarization getPolarization() {
		return polarization;
	}

	public void setPolarization(Polarization polarization) {
		this.polarization = polarization;
	}
	
	public String toString() {
		StringBuffer tmp = new StringBuffer();
		if(this.getPolarization()==Polarization.NONE)
			tmp.append("Unpolarized photon");
		else if(this.getPolarization()==Polarization.HORIZONTAL)
			tmp.append("Horizontaly polarized photon");
		else if(this.getPolarization()==Polarization.VERTICAL)
			tmp.append("Verticaly polarized photon");
		else if(this.getPolarization()==Polarization.SLASH)
			tmp.append("Slash polarized photon");
		else
			tmp.append("Antislash polarized photon");
		return tmp.toString();
	}	
}
