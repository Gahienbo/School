package ija.ija2019.homework2.maps;

public class Coordinate  {
	private int x;
	private int y;
	
	public static Coordinate create(int x,int y) {
		Coordinate pom;
		if ((x>0) & (y>0)) {
			pom = new Coordinate(x,y);
			return pom;
		}
		else {
			pom = null;
			return pom;
		}
	}
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
    public int getX() {
    	return this.x;
    }
    public int getY() {
    	return this.y;
    }
    
    @Override
    public boolean equals(Object objectToCompare) { 
    	if (objectToCompare == this) {
    		return true;
    	}
    	if (!(objectToCompare instanceof Coordinate)) {
    		return false;
    	}
    	Coordinate recast = (Coordinate) objectToCompare;
    	
    	return x == recast.x && y == recast.y;
    }
}