package vut.fit.ija.homework1.myMaps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import vut.fit.ija.homework1.maps.Street;
import vut.fit.ija.homework1.maps.StreetMap;

public class MyStreetMap implements StreetMap {

	private List<Street> mapStreetList;
	
	
	public MyStreetMap() {
		this.mapStreetList = new ArrayList<Street>();
	}
    public void addStreet(Street s) {
    	mapStreetList.add(s);
    }
    public Street getStreet(String id) {
    	for (Street street : mapStreetList) {
    		if (street.getId().contentEquals(id)) {
    			return street;
    		}
        }
    	return null;
    }
}
