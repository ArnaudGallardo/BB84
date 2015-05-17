
public class FilterScheme extends AbstractScheme{
	private Filter[] filters;
	
	public FilterScheme(int size) {
		super(size);
		filters = new Filter[size];
		for(int i=0;i<size;i++) {
			filters[i] = new Filter(Basis.random());
		}
	}

	public Filter getFilter(int i) {
		return filters[i];
	}
	
	@Override
	public boolean equals(Scheme s) {
		// TODO Auto-generated method stub
		if(super.equals(s)) {
			if(s instanceof FilterScheme) {
				FilterScheme pS = (FilterScheme) s;
				for(int i=0;i<pS.getSize();i++) {
					if(pS.getFilter(i)!=this.getFilter(i))
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
		tmp.append("Polarization Basis : ");
		for(int i=0;i<this.getSize()-1;i++) {
			tmp.append(this.getFilter(i));
			tmp.append(", ");
		}
		tmp.append(this.getFilter(this.getSize()-1));
		return tmp.toString();
	}
	
	// Returns an array of int containing the indexes of the filters that have been used by both Alice and Bob at the same time
	public int[] indexOfIden(FilterScheme p) { //PB taille ?
		int[] tmp = new int[p.getSize()];
		int j = 0;
		for(int i=0;i<p.getSize();i++) {
			if(p.getFilter(i).getBasis()==this.getFilter(i).getBasis()) {
				tmp[j]=i;
				j++;
			}
		}
		int[] result = new int[j];
		for(int k=0;k<j;k++) {
			result[k]=tmp[k];
		}
		return result;
	}
	
	
	// Returns the number of times when Alice and Bob used the same filter at the same time
	public int numberIden(FilterScheme fp)
	{
		assert(this.getSize() == fp.getSize());
		int nb = 0;
		for(int i = 0; i < fp.getSize(); i++)
		{
			if(this.getFilter(i).equals(fp.getFilter(i)))
				nb++;
		}
		return nb;
	}
}
