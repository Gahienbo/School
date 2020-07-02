/**
* Trida reprezentujici objizdku
* @author Matěj sojka (xsojka04)
* @author Jan Hranický (xhrani02)
*
*/
package src.maps;

import javafx.scene.Group;

import java.util.ArrayList;
import java.util.List;

public class Street {

  private final String streetName;
  private final List<StreetSection> streetSections;
  private int trafficSize = 0;

/**
* Konstruktor tridy
* @param name - id ulice
* @param streetSections - seznam sekci ulice
*/
  public Street(String name, List<StreetSection> streetSections) {
    this.streetName = name;
    this.streetSections = streetSections;
    for (StreetSection streetSection : streetSections) {
      streetSection.setStreet(this);
    }
  }
/**
* Getter vracejici seznam sekci
* @return List&#060;StreetSection&#062; - seznam sekci
*/
  public List<StreetSection> getStreetSectionList() {
    return this.streetSections;
  }
/**
* Getter id ulice
* @return String - id ulice
*/
  public String getId() {
    return streetName;
  }
/**
* Vykresli ulici
* @param root - skupinu pod kterou se ma ulice vykreslit
* @param streetMap - mapa do ktere ulice spada
*/
  public void draw(Group root, StreetMap streetMap) {
    for (StreetSection streetSection : streetSections) {
      streetSection.drawSection(root, streetMap);
    }
  }
/**
* Vrati seznam sekci ulice ktere prochazeji krizovatkou
* @param crossRoad - krizovatka 
* @return List&#060;StreetSection&#062; - seznam ulic, ktere prochazeji danou krizovatkou
*/
  public List<StreetSection> getStreetSections(CrossRoad crossRoad) {
    List<StreetSection> streetSections = new ArrayList<>();
    for (StreetSection streetSection : this.streetSections) {
      if (streetSection.getEnd() == crossRoad || streetSection.getStart() == crossRoad) {
        streetSections.add(streetSection);
      }
    }
    return streetSections;
  }
/**
* Refreshuje barvu ulice podle jejiho stupne dopravy
*/
  public void refreshStreetColor() {
    for (StreetSection streetSection : streetSections) {
      streetSection.colorTrafficSection(this.trafficSize);
    }
  }
/**
* Setter nastavujici stupen dopravy ulice
* @param trafficSize - stupen dopravy
*/
  public void setTrafficSize(int trafficSize) {
    this.trafficSize = trafficSize;
    refreshStreetColor();
  }
/**
* Getter vracejici stupen dopravy
* @return trafficSize - stupen dopravy
*/
  public int getTrafficSize() {
    return trafficSize;
  }
/**
* Aktualizace stupne dopravy
* @param number - zmena stupne dopravy 
*/
  public void addToTrafficSize(int number) {
    int newTrafficSize = this.trafficSize + number;
    if (0 <= newTrafficSize && newTrafficSize <= 8) {
      this.setTrafficSize(newTrafficSize);
    } else {
      this.setTrafficSize(this.trafficSize);
    }
  }
}
