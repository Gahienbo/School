package ija.ija2019.homework2.myMaps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ija.ija2019.homework2.maps.Coordinate;
import ija.ija2019.homework2.maps.Stop;
import ija.ija2019.homework2.maps.Street;

public class MyStreet implements Street{
	
	private String streetName;
	private Coordinate[] streetCordArray;
	private List<Coordinate> streetCordList;
	private List<Stop> streetStopList;
	private Coordinate firstCord;
	private Coordinate lastCord;
	
	public MyStreet(String name,Coordinate... cord) {
		this.streetName = name;
		this.streetCordArray = cord;
		this.firstCord = cord[0];
		this.lastCord = cord[cord.length-1];
		this.streetStopList = new ArrayList<Stop>();
		this.streetCordList = new ArrayList<>(Arrays.asList(streetCordArray));
	}
	public boolean follows(Street s) {
		List<Coordinate> sCordArray = s.getCoordinates();
		for (int i = 0; i<this.streetCordArray.length; i++) {
			for(int j = 0; j<sCordArray.size(); j++) {
				if(this.streetCordArray[i].getX() == sCordArray.get(j).getX() && this.streetCordArray[i].getY() == sCordArray.get(j).getY()) {
					return true;
				}	
			}
		}
		return false;
	}
	public boolean addStop(Stop s) {
		for(int i = 0;i < streetCordArray.length-1; i++) {
			if(s.getCoordinate().getX() == this.streetCordArray[i].getX()) {
				if(s.getCoordinate().getY() > this.streetCordArray[i].getY() && s.getCoordinate().getY() < this.streetCordArray[i+1].getY()) {
					this.streetStopList.add(s);
					s.setStreet(this);
					return true;
				}
				else if(s.getCoordinate().getY() < this.streetCordArray[i].getY() && s.getCoordinate().getY() > this.streetCordArray[i+1].getY())  {
					this.streetStopList.add(s);
					s.setStreet(this);
					return true;
				}
			}
			else if(s.getCoordinate().getY() == this.streetCordArray[i].getY()) {
				if(s.getCoordinate().getX() > this.streetCordArray[i].getX() && s.getCoordinate().getX() < this.streetCordArray[i+1].getX()) {
					this.streetStopList.add(s);
					s.setStreet(this);
					return true;
				}
				else if(s.getCoordinate().getX() < this.streetCordArray[i].getX() && s.getCoordinate().getX() > this.streetCordArray[i+1].getX())  {
					this.streetStopList.add(s);
					s.setStreet(this);
					return true;
				}
			}
		}
		return false;
	}
	public Coordinate begin() {
		return this.firstCord;
	}
	public Coordinate end() {
		return this.lastCord;
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
}
