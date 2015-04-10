// Fraction.java by Lisa K. Fazio

package utils;

public class Fraction implements Comparable<Fraction>{
	public int num;
	public int den;
	
	public Fraction(int num, int den) {
		this.num = num;
		this.den = den;
	}
	
	/**
	 * @return a floating point number representing this fraction
	 */
	public float getFloat() {
		return (float)num/(float)den;
	}
	
	@Override
	public int compareTo(Fraction f) {
		// TODO Auto-generated method stub
		//return - if value passed in is greater
		// return + is current is greater
		if (this.getFloat() > f.getFloat()){
			return 1;
		} else if(this.getFloat() < f.getFloat()){
			return -1;
		}
		return 0;
	}
	
	@Override
    public String toString() {
        return num + "/" + den;
    }
}
