/**
* Trida reprezentujici objizdku
* @author Matěj sojka (xsojka04)
* @author Jan Hranický (xhrani02)
*
*/
package src.maps;

import java.util.ArrayList;

public class DeTour {
  private ArrayList<CrossRoad> route;
  private ArrayList<CrossRoad> skippedRoute;
  private int delay;

/**
* kontruktor tridy
*/
  public DeTour() {
    this.route = new ArrayList<>();
    this.skippedRoute = new ArrayList<>();
  }
/**
* Setter nastavujici objizdnou trasu
* @param route - seznam krizovatek objizdne trasy
*
*/
  public void setRoute(ArrayList<CrossRoad> route) {
    this.route = new ArrayList<>(route);
  }

  public void setSkippedRoute(ArrayList<CrossRoad> skippedRoute) {
    this.skippedRoute = new ArrayList<>(skippedRoute);
  }
/**
* Setter nastavujici zpozdeni objizdky
* @param delay - zpozdeni
*
*/
  public void setDelay(int delay) {
    this.delay = delay;
  }
/**
* Getter vracejici objizdnou trasu
* @return ArrayList&#060;CrossRoad&#062; - objizdna trasa
*
*/
  public ArrayList<CrossRoad> getRoute() {
    return this.route;
  }

  public ArrayList<CrossRoad> getSkippedRoute() {
    return this.skippedRoute;
  }

/**
* Testuje zda objizdka zacina krizovatkou
* @param coordinate - souradnice krizovatky
* @return Boolean - true v pripade ze objizdna trasa zacina krizovatkou
*
*/
  public Boolean startsWith(Coordinate coordinate) {
    return route.get(0).getCord().equals(coordinate);
  }
/**
* Getter vracejici zpozdeni objizdky
* @return int - zpozdeni
*
*/
  public int getDelay() {
    return delay;
  }
/**
* Getter vracejici posledni souradnici objizdky
* @return Coordinate - posledni souradnice objizdky
*
*/
  public Coordinate getLastCoordinate() {
    return route.get(route.size() - 1).getCord();
  }
}
