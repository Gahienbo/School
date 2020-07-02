/**
* Trida reprezentujici linku autobusu
* @author Matěj sojka (xsojka04)
* @author Jan Hranický (xhrani02)
*
*/

package src.maps;

import javafx.scene.paint.Color;
import src.vehicles.Bus;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import static src.managers.TimeManager.DAY_LENGTH;

public class BusLine {
  private final int id;
  private final List<Integer> connections; // casy odjezdu spoju z 1. zastavky
  private final List<SimpleImmutableEntry<StreetSection, Stop>> route;
  private final Hashtable<String, Integer> stopDepartureTimes;
  private final int busSpeed;
  private final StreetMap streetMap;
  private final ArrayList<DeTour> deTourList;

  /**
  * Konstruktor tridy
  * @param id - ciselne oznaceni linky
  * @param streetMap - objekt streetMap na kteremu linka nalezi
  *
  */
  public BusLine(int id, StreetMap streetMap) {
    this.id = id;
    this.route = new ArrayList<>();
    this.connections = new ArrayList<>();
    this.stopDepartureTimes = new Hashtable<>();
    this.busSpeed = 10;
    this.streetMap = streetMap;
    this.deTourList = new ArrayList<>();
  }

  /**
  * Obsluha kliknuti na autobus linky
  * @param b - objekt autobusu
  *
  */
  public void handleBusClick(Bus b) {
    this.streetMap.busClick(b);
  }

  /**
  * Getter vracejici sekci trasy autobusove linky
  * @param index - index pozadovane sekce
  * @return StreetSection
  *
  */
  public StreetSection getRouteStreetSection(int index) {
    return this.route.get(index).getKey();
  }

  /**
  * Getter vracejici zastavku trasy autobusove linky
  * @param index - index pozadovane sekce
  * @return Stop
  *
  */
  private Stop getRouteStop(int index) {
    return this.route.get(index).getValue();
  }

  /**
  * Obarvi trasu linky
  * @param color - pozadovana barva
  *
  */
  public void colorRoute(Color color) {
    for (SimpleImmutableEntry<StreetSection, Stop> r : this.route) {
      r.getKey().colorSection(color);
    }
  }

  /**
  * Obarvi krizovatky
  *
  */
  public void highLightCrossRoads() {
    for (SimpleImmutableEntry<StreetSection, Stop> r : this.route) {
      r.getKey().getStart().highLightCrossRoad();
      r.getKey().getEnd().highLightCrossRoad();
      if (r.getValue() != null) r.getValue().highLightStop();
    }
  }

  /**
  * Zrusi Obarveni krizovatek
  *
  */
  public void stopHighLightCrossroads() {
    for (SimpleImmutableEntry<StreetSection, Stop> r : this.route) {
      r.getKey().getStart().stopHighLightCrossRoad();
      r.getKey().getEnd().stopHighLightCrossRoad();
      if (r.getValue() != null) r.getValue().stopHighLightStop();
    }
  }

  /**
  * Zvyrazni trasu
  *
  */
  public void highLightRoute() {
    colorRoute(Color.BLACK);
  }

  /**
  * Zrusi zvyrazneni trasy
  *
  */
  public void stopHighLightRoute() {
    for (SimpleImmutableEntry<StreetSection, Stop> r : this.route) {
      r.getKey().colorTrafficSection(r.getKey().getStreet().getTrafficSize());
    }
  }

  /**
  * Prida zastavku do trasy
  * @param stop - pridana zastavka
  * @param crossRoad - objekt krizovatky pridane zastavky
  *
  */
  public void addStop(Stop stop, CrossRoad crossRoad) {
    StreetSection streetSection = stop.getStreetSection(crossRoad);
    if (route.size() != 0) {
      SimpleImmutableEntry<StreetSection, Stop> last = route.get(route.size() - 1);
      if (!last.getKey().follows(streetSection)) {
        return;
      } else if (last.getKey().getId().equals(streetSection.getId())) {
        route.get(route.size() - 1).setValue(stop);
        return;
      }
    }
    route.add(new SimpleImmutableEntry<>(streetSection, stop));
  }

  /**
  * Prida sekci do trasy
  * @param streetSection - pridana sekce
  *
  */
  public void addStreetSection(StreetSection streetSection) {
    if (route.size() == 0) {
      return;
    }

    SimpleImmutableEntry<StreetSection, Stop> last = route.get(route.size() - 1);
    if (last.getKey().getId().equals(streetSection.getId())) {
      return;
    }
    route.add(new SimpleImmutableEntry<>(streetSection, null));
  }

  /**
  * Getter vracejici trasu linky
  * @return List&#060;SimpleImmutableEntry&#060;StreetSection, Stop&#062;&#062;
  *
  */
  public List<SimpleImmutableEntry<StreetSection, Stop>> getRoute() {
    return Collections.unmodifiableList(route);
  }

  /**
  * Prida spoj
  * @param departureTime - cas odjezdu spoje
  *
  */
  public void addConnection(Integer departureTime) {
    if (connections.contains(departureTime))
      return;
    connections.add(departureTime);
  }

  /**
  * Test casu odjezdu
  * @param currentTime - aktualni cas hodin
  * @return boolean - vraci true v pripade ze ma vyjet dalsi autobus
  */
  public boolean isDeparture(int currentTime) {
    return connections.contains(currentTime);
  }

  /**
  * Getter vracejici id linky
  * @return int - id linky
  */
  public int getId() {
    return id;
  }

  /**
  * Getter vracejici index krizovatky objizdky
  * @param coordinate - souradnice krizovatky
  * @return int - index
  */
  public int getDeRouteListIndex(Coordinate coordinate) {
    for (int i = 0; i < deTourList.size(); i++) {
      if (deTourList.get(i).startsWith(coordinate)) {
        return i;
      }
    }
    return -1;
  }

  /**
  * Getter vracejici objekt objizdky
  * @param coordinate - souradnice krizovatky na ktere ma objizdka zainact
  * @return DeTour - objekt objizdky
  */
  public DeTour getDeTourStartingWith(Coordinate coordinate) {
    for (DeTour deTour : deTourList) {
      if (deTour.startsWith(coordinate)) {
        return deTour;
      }
    }
    return null;
  }

  /**
  * Getter vracejici pocatecni pozici
  * @return Coordinate - pocatecni pozice
  */
  public Coordinate getStartCoordinate() {
    return this.getRouteStop(0).getCoordinate();
  }

  /**
  * Getter vracejici konecnou pozici
  * @return Coordinate - konecna pozice
  */
  public CrossRoad getEnd() {
    return this.getRouteStop(route.size() - 1).getCrossRoad();
  }

  /**
  * Getter vracejici konecnou krizovatky sekce
  * @param index - index pozadovane ulice
  * @return CrossRoad - konecna krizovatka
  */
  public CrossRoad getStreetSectionEnd(int index) {
    if (index == route.size() - 1) {
      return this.getEnd();
    }
    return this.getRouteStreetSection(index + 1).getCommonCrossRoad(this.getRouteStreetSection(index));
  }

  /**
  * Getter vracejici konecnou krizovatky sekce
  * @param sectionEnd - index pozadovane ulice
  * @param currentTime - index pozadovane ulice
  * @param exactStopDepartureTimes - index pozadovane ulice
  * @return Integer - konecna krizovatka
  */
  public Integer getToStopTime(CrossRoad sectionEnd, int currentTime, Hashtable<String, Integer> exactStopDepartureTimes) {
    Stop stop = sectionEnd.getStop();
    if (stop == null) {
      return 0;
    }

    int stopDepTime = exactStopDepartureTimes.get(stop.getId());
    if (currentTime <= stopDepTime && (stopDepTime - currentTime) < 600) {
      return stopDepTime - currentTime;
    } else if (currentTime >= stopDepTime) {
      int res = 0;
      if (currentTime + 70 > DAY_LENGTH && stopDepTime < 420) {
        res = DAY_LENGTH - currentTime + stopDepTime;
      }
      return res;
    } else {
      return 0;
    }
  }

  /**
  * Test zda je krizovatka na trase linky
  * @param crossRoad - testovana krizovatka
  * @return boolean - true v pripade ze je krizovatka soucasti trasy linky
  */
  public boolean belongsToLine(CrossRoad crossRoad) {
    for (SimpleImmutableEntry<StreetSection, Stop> part : route) {
      StreetSection streetSection = part.getKey();
      if (crossRoad == streetSection.getStart() || crossRoad == streetSection.getEnd())
        return true;
    }
    return false;
  }

  /**
  * Prida objizdku linky do seznamu objizdek
  * @param deTour - testovana krizovatka
  */
  public void addDeTourList(DeTour deTour) {
    this.setDetour(deTour);
  }

  /**
   * prida detour do deTourList
   * @param detour
   */
  private void setDetour(DeTour detour) {
    this.deTourList.add(detour);
  }

  /**
  * Getter vracejici seznam objizdek
  * @return ArrayList(DeTour) - seznam objizdek
  */
  public ArrayList<DeTour> getDeRouteList() {
    return this.deTourList;
  }

  /**
  * Vraci true v pripade ze ma linka objizdku
  * @return boolean - true v pripade ze ma linka objizdku
  */
  public boolean hasDeTour() {
    return this.deTourList.isEmpty();
  }

  /**
  * Prida cas odjezdu zastavce
  * @param stopId - jmeno zastavky
  * @param departureTime - cas odjezdu
  */
  public void addStopDepartureTime(String stopId, int departureTime) {
    stopDepartureTimes.put(stopId, departureTime);
  }

  /**
  * Getter vracejici odjezdy linky ze zastavek
  * @return Hashtable &#060;String, Integer  &#062; - odjezdy ze zastavek
  */
  public Hashtable<String, Integer> getStopDepartureTimes() {
    return stopDepartureTimes;
  }

  /**
  * Getter vracejici rychlost autobusu linky
  * @return int - rychlost autobusu
  */
  public int getBusSpeed() {
    return busSpeed;
  }

  /**
  * Getter vracejici index nasledujici krizovatky trasy autobusu
  * @param newPosition - aktualni pozice autobusu
  * @param currentRouteIndex - index posledni krizovatky, ze ktere autobus vyjel
  * @return int - index krizovatky
  */
  public int getRouteIndex(Coordinate newPosition, int currentRouteIndex) {
    for (int i = 0; i < route.size(); i++) {
      StreetSection streetSection = route.get(i).getKey();
      if (newPosition == streetSection.getStart().getCord() || newPosition == streetSection.getEnd().getCord())
        if (currentRouteIndex < i + 1)
          return i + 1;
    }
    return -2;
  }

  /**
   * Odstrani deTour z deTourList na indexu index
   * @param index
   */
  public void removeDeTour(int index) {
    this.deTourList.remove(index);
  }

  /**
  * Zvyrazni objizdku linky
  * @param closedColor - barva, kterou se ma zvyraznit uzavrena sekce linky
  * @param deTourColor -  barva, kterou se ma zvyraznit objizdna sekce linky
  */
  public void highLightDeTour(Color closedColor, Color deTourColor) {
    for (DeTour deTour : this.deTourList) {
      this.highLightChosenStreetSections(deTour.getRoute(), deTourColor);
      this.highLightChosenStreetSections(deTour.getSkippedRoute(), closedColor);
    }
  }

  /**
  * Zvyrazni aktualne vybrane sekce pri vyberu objizdky
  * @param list - seznam vybranych sekci
  * @param color -  zvolena barva
  */
  public void highLightChosenStreetSections(ArrayList<CrossRoad> list, Color color) {
    if (list.size() > 1) {
      for (int i = 0; i < list.size() - 1; i++) {
        StreetSection tmp = list.get(i).getStreetSection(list.get(i + 1));
        tmp.colorSection(color);
      }
    }
  }
}
