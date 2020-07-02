package ija.ija2019.homework2.maps;

import java.util.AbstractMap;
import java.util.List;

import ija.ija2019.homework2.myMaps.MyLine;

public interface Line {
	
	public boolean addStop(Stop stop);
	
	public boolean addStreet(Street street);
	
	public static Line defaultLine(String id) {
		return (new MyLine(id));
	}
	
	public List<AbstractMap.SimpleImmutableEntry<Street, Stop>> getRoute();	
	
}
