package vut.fit.ija.homework1.myMaps;

import vut.fit.ija.homework1.maps.Coordinate;
import vut.fit.ija.homework1.maps.Stop;
import vut.fit.ija.homework1.maps.Street;

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
    	return stopName;
    }
    public Coordinate getCoordinate() {
    	if (stopCord != null) {
        	return stopCord;
    	}
    	else {
    		return null;
    	}
    }
    public void setStreet(Street s) {
    	stopLocation = s;
    }
    public Street getStreet() {
    	return stopLocation;
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


}
