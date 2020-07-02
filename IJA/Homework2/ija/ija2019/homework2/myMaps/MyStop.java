package ija.ija2019.homework2.myMaps;

import ija.ija2019.homework2.maps.Coordinate;
import ija.ija2019.homework2.maps.Stop;
import ija.ija2019.homework2.maps.Street;

public class MyStop implements Stop{
	private String stopName;
	private Coordinate stopCord;
	private Street stopLocation;
	
	public MyStop(String stopName, Coordinate stopCord) {
		this.stopName = stopName;
		this.stopCord = stopCord;
	}
	public MyStop (String stopName) {
		this(stopName,null);
	}
    public String getId() {
    	return this.stopName;
    }
    public Coordinate getCoordinate() {
    	if (stopCord != null) {
        	return this.stopCord;
    	}
    	else {
    		return null;
    	}
    }
    public void setStreet(Street s) {
    	this.stopLocation = s;
    }
    public Street getStreet() {
    	return this.stopLocation;
    }
    @Override
    public boolean equals(Object objectToCompare) { 
    	if (objectToCompare == this) {
    		return true;
    	}
    	if (!(objectToCompare instanceof MyStop)) {
    		return false;
    	}
    	MyStop recast = (MyStop) objectToCompare;
    	
    	return stopName == recast.stopName;
    }
    @Override
    public String toString() { 
        return String.format("stop("+this.getId()+")"); 
    } 

}
