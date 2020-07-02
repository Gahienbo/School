/**
 * Trida autobusu
 * @author Matěj sojka (xsojka04)
 * @author Jan Hranický (xhrani02)
 *
 */
package src.vehicles;

import javafx.scene.Group;
import src.icons.BusIcon;
import src.maps.*;

import java.util.AbstractMap;
import java.util.Hashtable;
import java.util.Map;

import static src.managers.TimeManager.DAY_LENGTH;
import static src.managers.TimeManager.timeString;

public class Bus {
  private final String id;
  private final BusLine busLine;
  private final Hashtable<String, Integer> busStopDepartures;
  private BusIcon busIcon;
  private int lastUpdatedTime;
  private Coordinate currentPosition;
  private int currentRouteIndex;
  private DeTour currentDeTour;
  private int currentDeTourIndex;

  public Bus(String id, BusLine line, Group busGroup, int departureTime, Hashtable<String, Integer> busStopDepartures) {
    this.id = id;
    this.busLine = line;
    this.busIcon = null;
    this.currentRouteIndex = 0;
    this.currentDeTourIndex = 0;
    this.currentDeTour = null;
    this.busStopDepartures = new Hashtable<>();
    this.lastUpdatedTime = departureTime;
    this.setStartPosition();
    this.busIcon = new BusIcon(busGroup, this.currentPosition, this);
    for (Map.Entry<String, Integer> entry : busStopDepartures.entrySet()) {
      this.busStopDepartures.put(entry.getKey(), (entry.getValue() + departureTime) % DAY_LENGTH);
    }
  }

  /**
  *Nastavi pozici autobusu
  */
  private void setStartPosition() {
    int deRouteIndex = this.busLine.getDeRouteListIndex(this.busLine.getStartCoordinate());
    if (deRouteIndex != -1) {
      this.currentDeTourIndex = 0;
      this.currentDeTour = this.busLine.getDeRouteList().get(deRouteIndex);
    }
    this.currentPosition = this.busLine.getStartCoordinate();
  }

  /**
  *Smaze ikonu autobusu ze sceny
  */
  public void deleteBusIcon(Group root) {
    busIcon.delete(root);
  }

  /**
  * Getter vracejici linku autobusu
  */
  public BusLine getBusLine() {
    return this.busLine;
  }

  /**
  * Getter vracejici id autobusu
  */
  public String getId() {
    return this.id;
  }

  /**
  * Vypocita novou pozici autobusu 
  */
  private Coordinate getNewPosition(int currentTime) {
    Coordinate newPosition = currentPosition;
    int passedTime = getPassedTime(currentTime);
    double timeForThisSection;
    CrossRoad sectionEnd;
    if (currentDeTour != null) {
      sectionEnd = this.currentDeTour.getRoute().get(this.currentDeTourIndex + 1); //tady uprava pro objizdku
      timeForThisSection = this.currentDeTour.getRoute().get(this.currentDeTourIndex).getStreetSection(sectionEnd).getTimeTillEnd(this.currentPosition, sectionEnd.getCord(), this.busLine.getBusSpeed());
    } else {
      sectionEnd = this.busLine.getStreetSectionEnd(this.currentRouteIndex); //tady uprava pro objizdku
      timeForThisSection = this.busLine.getToStopTime(sectionEnd, currentTime, this.busStopDepartures) + this.busLine.getRouteStreetSection(this.currentRouteIndex).getTimeTillEnd(this.currentPosition, sectionEnd.getCord(), this.busLine.getBusSpeed());
    }
    while (timeForThisSection < passedTime) {
      if (currentDeTour != null) {
        this.currentDeTourIndex++;
      } else {
        this.currentRouteIndex++;
      }
      newPosition = sectionEnd.getCord();
      passedTime -= timeForThisSection;
      if (busLine.getDeRouteListIndex(newPosition) != -1) {
        this.currentDeTourIndex = 0;
        currentDeTour = this.busLine.getDeRouteList().get(busLine.getDeRouteListIndex(newPosition));
        sectionEnd = this.currentDeTour.getRoute().get(this.currentDeTourIndex + 1);
        timeForThisSection = this.currentDeTour.getRoute().get(this.currentDeTourIndex).getStreetSection(sectionEnd).getTimeTillEnd(this.currentPosition, sectionEnd.getCord(), this.busLine.getBusSpeed());
      } else if (this.currentDeTour != null) {
        if ((currentDeTourIndex + 1) < currentDeTour.getRoute().size()) {
          sectionEnd = this.currentDeTour.getRoute().get(this.currentDeTourIndex + 1);
          timeForThisSection = this.currentDeTour.getRoute().get(this.currentDeTourIndex).getStreetSection(sectionEnd).getTimeTillEnd(this.currentPosition, sectionEnd.getCord(), this.busLine.getBusSpeed());
        } else {
          currentDeTour = null;
          currentRouteIndex = this.busLine.getRouteIndex(newPosition, currentRouteIndex);
          if (isEndOfRoute()) {
            return null;
          }
          sectionEnd = this.busLine.getStreetSectionEnd(currentRouteIndex);
          timeForThisSection = this.busLine.getToStopTime(sectionEnd, currentTime, this.busStopDepartures) + this.busLine.getRouteStreetSection(currentRouteIndex).getTimeTillEnd(newPosition, sectionEnd.getCord(), this.busLine.getBusSpeed());
        }
      } else {
        if (isEndOfRoute()) {
          return null;
        }
        sectionEnd = this.busLine.getStreetSectionEnd(currentRouteIndex);
        timeForThisSection = this.busLine.getToStopTime(sectionEnd, currentTime, this.busStopDepartures) + this.busLine.getRouteStreetSection(currentRouteIndex).getTimeTillEnd(newPosition, sectionEnd.getCord(), this.busLine.getBusSpeed());
      }
    }
    newPosition = this.busLine.getRouteStreetSection(currentRouteIndex).getNewPosition(newPosition, sectionEnd.getCord(), this.busLine.getBusSpeed(), passedTime);
    return newPosition;
  }
  /**
  * @return boolean - Vraci true v pripade ze bus dojel na konec trasy
  */
  private boolean isEndOfRoute() {
    return this.currentRouteIndex >= this.busLine.getRoute().size();
  }
  /**
  * @param busGroup - skupina autobusu
  * @param currentTime - aktualni cas hodin
  * @return boolean - Vraci true v pripade ze nastal cas updatoat pozici autobusu
  */
  public boolean updateBusPosition(Group busGroup, int currentTime) {
    Coordinate newPosition = getNewPosition(currentTime);
    if (newPosition == null) {
      busIcon.delete(busGroup);
      return false;
    }
    this.lastUpdatedTime = currentTime;
    busIcon.move(newPosition.getX() - currentPosition.getX(), newPosition.getY() - currentPosition.getY());
    this.currentPosition = newPosition;
    return true;
  }
  /**
  * @return String - Vraci string trasy autobusove linky
  */
  public String getLineString() {
    StringBuilder returnString = new StringBuilder();
    Coordinate nextCoordinate = this.busLine.getStartCoordinate();
    int delay = 0;
    for (int i = 0; i < this.busLine.getRoute().size(); ) {
      AbstractMap.SimpleImmutableEntry<StreetSection, Stop> part = this.busLine.getRoute().get(i);
      DeTour deTour = this.busLine.getDeTourStartingWith(nextCoordinate);
      if (deTour != null) {
        nextCoordinate = deTour.getLastCoordinate();
        delay += deTour.getDelay();
        i = this.busLine.getRouteIndex(nextCoordinate, i);
      } else {
        if (part.getValue() != null) {
          returnString.append(timeString(busStopDepartures.get(part.getValue().getId()) + delay)).append(" ").append(part.getValue().getId()).append("\n");
        }
        nextCoordinate = part.getKey().GetOtherEnd(nextCoordinate);
        i++;
      }
    }
    return returnString.toString();
  }
 /**
  * @return int - vraci pocet casovych jednotek ktere ubehly od posledniho updatu
  */
  private int getPassedTime(int currentTime) {
    if ((currentTime - this.lastUpdatedTime < 0)) {
      return (DAY_LENGTH - this.lastUpdatedTime) + currentTime;
    } else {
      return currentTime - this.lastUpdatedTime;
    }
  }
 /**
  * Obsluzna funkce pro kliknuti na ikonu autobusu
  */
  public void onBusIconClick() {
    this.busLine.handleBusClick(this);
  }
}
