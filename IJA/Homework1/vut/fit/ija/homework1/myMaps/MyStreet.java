package vut.fit.ija.homework1.myMaps;
import java.util.ArrayList;
import java.util.List;

import vut.fit.ija.homework1.maps.Coordinate;
import vut.fit.ija.homework1.maps.Stop;
import vut.fit.ija.homework1.maps.Street;

public class MyStreet implements Street{
	
	private String streetName;
	private Coordinate streetCord1;
	private Coordinate streetCord2;
	private List<Coordinate> streetCordList;
	private List<Stop> streetStopList;
	
	public MyStreet(String name,Coordinate cord1,Coordinate cord2) {
		this.streetName = name;
		this.streetCord1 = cord1;
		this.streetCord2 = cord2;
		this.streetCordList = new ArrayList<Coordinate>();
		this.streetCordList.add(cord1);
		this.streetCordList.add(cord2);
		this.streetStopList = new ArrayList<Stop>();
	}
    public String getId() {
    	return streetName;
    }
    public List<Coordinate> getCoordinates() {
    	return streetCordList;
    }
    public List<Stop> getStops() {
    	return streetStopList;
    }
    public void addStop(Stop stop) {
    	stop.setStreet(MyStreet.this);
    	streetStopList.add(stop);
    }


}
