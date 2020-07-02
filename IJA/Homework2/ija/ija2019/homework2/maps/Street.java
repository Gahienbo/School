package ija.ija2019.homework2.maps;
import java.util.List;
import ija.ija2019.homework2.myMaps.MyStreet;

public interface Street {
	
	public boolean addStop(Stop stop);
	
	public Coordinate begin();
		
	public Coordinate end();
	
	public boolean follows(Street s);
	
	public List<Coordinate> getCoordinates();
	
	public String getId();
	
	public List<Stop> getStops();
	
	public static Street defaultStreet(String id,Coordinate...cord) {
		int valid = 0;
		for(int i=0;i<cord.length-1;i++) {
			if(cord[i].getX() == cord[i+1].getX()) {
				
			}
			else if(cord[i].getY() == cord[i+1].getY()) {
				
			}
			else {
				valid = 1;
			}
		}
		if (valid == 1) {
			return null;
		}
		else {
			return(new MyStreet(id,cord));
		}
	}
	
}
