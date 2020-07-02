package ija.ija2019.homework2.maps;

import ija.ija2019.homework2.myMaps.MyStop;

public interface Stop {
	
	public static Stop defaultStop(String id,Coordinate cord) {
		return(new MyStop(id,cord));
	}
	
	public Coordinate getCoordinate();
	
	public String getId();
	
	public Street getStreet();
	
	public void setStreet(Street s);
}
