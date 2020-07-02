package vut.fit.ija.homework1.myMaps;
import vut.fit.ija.homework1.maps.Coordinate;

public class MyCoordinate implements Coordinate {
	private int x;
	private int y;
	
	public static Coordinate create(int x,int y) {
		Coordinate pom;
		if ((x>0) & (y>0)) {
			pom = new MyCoordinate(x,y);
			return pom;
		}
		else {
			pom = null;
			return pom;
		}
	}
	public MyCoordinate(int x, int y) {
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
    	if (!(objectToCompare instanceof MyCoordinate)) {
    		return false;
    	}
    	MyCoordinate recast = (MyCoordinate) objectToCompare;
    	
    	return x == recast.x && y == recast.y;
    }
}
