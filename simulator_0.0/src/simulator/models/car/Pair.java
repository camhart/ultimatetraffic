package simulator.models.car;

public final class Pair implements Comparable<Pair>{
	
	private double firstElement;
	private double secondElement;
	
	public Pair(double first, double second) {
		firstElement  = first;
		secondElement = second;
	}

	public double getFirst(){
		return firstElement;
	}
	
	public double getSecond(){
		return secondElement;
	}
	
	@Override
	public boolean equals(Object o){
		if(o == null)
			return false;
		if (this == o) 
			return true;
		if (!(o instanceof Pair)) 
			return false;
		
		Pair p = (Pair)o;
		return (p.getFirst() == firstElement && p.getSecond() == secondElement);
	}

	@Override
	public int compareTo(Pair p) {
		if(this.equals((Object)p))
			return 0;
		else if(this.firstElement > p.getFirst())
			return 1;
		else if(this.firstElement < p.getFirst())
			return -1;
		else
			if(this.secondElement > p.getSecond())
				return 1;
			else
				return -1;
	}
	
	@Override
	public int hashCode(){
		return (int)(13*(69*this.firstElement + 17*this.secondElement)) + super.hashCode();
	}
	
	@Override
	public String toString(){
		return "<" + firstElement + ", " + secondElement + ">";
	}
}
