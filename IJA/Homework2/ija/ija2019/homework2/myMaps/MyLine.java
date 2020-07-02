package ija.ija2019.homework2.myMaps;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import ija.ija2019.homework2.maps.Line;
import ija.ija2019.homework2.maps.Stop;
import ija.ija2019.homework2.maps.Street;

public class MyLine implements Line{
	private String id;
	private List<Stop> lineStopList;
	private List<Street> lineStreetList;
	private List<AbstractMap.SimpleImmutableEntry<Street, Stop>> lineRouteList;
	
	public boolean addStop(Stop stop) {
		if(this.lineStopList.isEmpty()) {
			this.lineStopList.add(stop);
			this.lineStreetList.add(stop.getStreet());
			this.lineRouteList.add(new AbstractMap.SimpleImmutableEntry<Street, Stop>(stop.getStreet(),stop));
			return true;
		}
		else {
			if(this.lineStreetList.get(lineStreetList.size()-1).follows(stop.getStreet())) {
				this.lineStopList.add(stop);
				this.lineStreetList.add(stop.getStreet());
				this.lineRouteList.add(new AbstractMap.SimpleImmutableEntry<Street, Stop>(stop.getStreet(),stop));
				return true;
			}
		}
		return false;
	}
	public boolean addStreet(Street street) {
		if(!this.lineStopList.isEmpty()) {
			if(this.lineStreetList.get(lineStreetList.size()-1).follows(street)) {
				this.lineStreetList.add(street);
				this.lineStopList.add(null);
				this.lineRouteList.add(new AbstractMap.SimpleImmutableEntry<Street, Stop>(street,null));
				return true;
			}
		}
		return false;
	}
	
	public List<AbstractMap.SimpleImmutableEntry<Street, Stop>> getRoute() {
		return Collections.unmodifiableList(this.lineRouteList);
	}
	public MyLine(String id) {
		this.id = id;
		this.lineStopList = new ArrayList<Stop>();
		this.lineStreetList = new ArrayList<Street>();
		this.lineRouteList = new ArrayList<AbstractMap.SimpleImmutableEntry<Street, Stop>>();
	}
}
