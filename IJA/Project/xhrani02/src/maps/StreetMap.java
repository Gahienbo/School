/**
 * Trida reprezentujici mapu
 * @author Matěj sojka (xsojka04)
 * @author Jan Hranický (xhrani02)
 *
 */
package src.maps;

import java.util.ArrayList;
import java.util.List;

import src.managers.DeTourManager;
import src.managers.TrafficManager;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import src.vehicles.Bus;

public class StreetMap {

  private final List<Street> mapStreetList;
  private final List<CrossRoad> crossRoadList;
  private final List<Stop> stopList;
  private final List<BusLine> busLineList;

  private boolean highLightFlag = false;
  private boolean deTourFlag = false;
  private boolean closedFlag = false;
  private boolean inSelectionFlag = false;

  private TrafficManager trafficManager;
  private DeTourManager deTourManager;

  /**
   * Konstruktor tridy
   */
  public StreetMap() {
    this.mapStreetList = new ArrayList<>();
    this.crossRoadList = new ArrayList<>();
    this.stopList = new ArrayList<>();
    this.busLineList = new ArrayList<>();
  }

  /**
   * Obsluhuje kliknuti na krizovatku
   * @param cs
   */
  public void crossRoadClick(CrossRoad cs) {
    if (this.deTourFlag) this.deTourManager.addCrossRoadToDeRouteList(cs);
    if (this.closedFlag) this.deTourManager.addCrossRoadToClosedList(cs);
  }

  /**
   * Zastavi zvyrazenni trasy autobusu
   */
  public void stopHighLightRoute() {
    for (BusLine busLine : busLineList) {
      busLine.stopHighLightRoute();
    }
  }

  /**
   * Obsluhuje kliknuti na autobus
   * @param b
   */
  public void busClick(Bus b) {
    if (!this.inSelectionFlag) {
      this.stopHighLightRoute();
      this.deTourManager.hide();
      this.deTourManager.handleStopHighlight();
      if (!this.highLightFlag) {
        b.getBusLine().highLightRoute();
        this.deTourManager.setBusLine(b.getBusLine());
        this.deTourManager.showStopList(b.getLineString());
        this.deTourManager.startDetourManager();
      }
      this.highLightFlag = !this.highLightFlag;
    }
    if (!this.highLightFlag)
      this.deTourManager.resetBusLine();
    refreshTrafficHighlight();
    refreshBusRouteHighlight();
  }

  /**
   * Obnovi zvyrezeneni trasy autobusu
   */
  public void refreshBusRouteHighlight() {
    if (this.deTourManager.getBusLine() != null) this.deTourManager.getBusLine().highLightRoute();
  }

  /**
   * Obnovi zvyrazneni stupnu dopravy
   */
  public void refreshTrafficHighlight() {
    for (Street s : mapStreetList) {
      s.refreshStreetColor();
    }
  }

  /**
   * Obnovi zvyrazneni aktualniho vyberu objizdky
   */
  public void refreshDeTourSelection() {
    this.deTourManager.refreshChosenDeRoute();
  }

  /**
   * Obsluhuje kliknuti na zastavku
   * @param s
   */
  public void stopClick(Stop s) {
    crossRoadClick(s.getCrossRoad());
  }

  /**
   * Obsluhuje klikniti na sekci ulice
   * @param streetSection
   */
  public void streetSectionClick(StreetSection streetSection) {
    this.trafficManager.setStreet(streetSection.getStreet());
  }

  /**
   * Nastavi flag pro ulozeni objizdky
   */
  public void setDeTourFlag() {
    this.deTourFlag = !this.deTourFlag;
  }

  /**
   * Prida krizovatku do seznamu krizovatek
   * @param crossRoad
   */
  public void addToCrossRoadList(CrossRoad crossRoad) {
    this.crossRoadList.add(crossRoad);
  }

  /**
   * Resetuje zbarveni ulic od jejich pocatecniho stavu
   */
  public void resetStreetColor() {
    for (Street street : this.mapStreetList) {
      for (StreetSection streetSection : street.getStreetSectionList()) {
        streetSection.colorSection(Color.GHOSTWHITE);
      }
    }
  }

  /**
   * Vykresli ulici
   * @param streetGroup
   */
  public void drawStreet(Group streetGroup) {
    for (Street street : this.getStreetList()) {
      street.draw(streetGroup, this);
    }
  }

  /**
   * Vykresli zastavky
   * @param stopGroup
   */
  public void drawStops(Group stopGroup) {
    for (Stop stop : this.stopList) {
      stop.drawStop(stopGroup, this);
    }
  }

  /**
   * Vykresli krizovatky
   * @param intersectionGroup
   * @param streetMap
   */
  public void drawCrossRoads(Group intersectionGroup, StreetMap streetMap) {
    for (CrossRoad crossRoad : this.crossRoadList) {
      crossRoad.drawCrossRoad(intersectionGroup, streetMap);
    }
  }

  /**
   * Prida ulici do seznamu ulic
   * @param s
   */
  public void addStreet(Street s) {
    mapStreetList.add(s);
  }

  /**
   * Getter vracejici zastavku podle jejiho nazvu
   * @param stopName
   * @return
   */
  public Stop getStop(String stopName) {
    for (Stop stop : stopList) {
      if (stop.getId().contentEquals(stopName)) {
        return stop;
      }
    }
    return null;
  }

  /**
   * Prida zastavku do seznamu zastavek
   * @param s
   */
  public void addToStopList(Stop s) {
    this.stopList.add(s);
  }

  /**
   * Getter vracejici seznam linek autobusu
   * @return
   */
  public List<BusLine> getBusLineList() {
    return this.busLineList;
  }

  /**
   * Prida linku autobusu
   * @param busLine
   */
  public void addToBusLineList(BusLine busLine) {
    this.busLineList.add(busLine);
  }

  /**
   * Getter vracejici krizovatku podle jejiho id
   * @param id
   * @return
   */
  public CrossRoad getCrossRoad(int id) {
    for (CrossRoad crossRoad : this.crossRoadList) {
      if (crossRoad.getId() == id) {
        return crossRoad;
      }
    }
    return null;
  }

  /**
   * Getter vracejici seznam ulic
   * @return
   */
  public List<Street> getStreetList() {
    return this.mapStreetList;
  }

  /**
   * Priradi traffic manager
   * @param trafficManager
   */
  public void setTrafficManager(TrafficManager trafficManager) {
    this.trafficManager = trafficManager;
  }

  /**
   * Priradi detrou manager
   * @param deTourManager
   */
  public void setDeTourManager(DeTourManager deTourManager) {
    this.deTourManager = deTourManager;
  }

  /**
   * Nastavi flag, ktery rika ve ktere casti vyberu objizdky je prave uzivatel
   */
  public void setClosedFlag() {
    this.closedFlag = !this.closedFlag;
  }

  /**
   * Getter vracejici closed flag
   * @return
   */
  public boolean getClosedFlag() {
    return this.closedFlag;
  }

  /**
   * getter vracejici getderoute flag
   * @return
   */
  public boolean getDeRouteFlag() {
    return this.deTourFlag;
  }

  /**
   * Nastavi flag, ktery rika ve ktere casti vyberu objizdky je prave uzivatel
   */
  public void setInSelectionFlag() {
    this.inSelectionFlag = !this.inSelectionFlag;
  }
}
