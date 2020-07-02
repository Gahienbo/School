/**
 * Trida starajici se stupen dopravy
 * @author Matěj sojka (xsojka04)
 * @author Jan Hranický (xhrani02)
 *
 */
package src.managers;

import javafx.scene.Group;
import src.icons.TrafficManagerIcon;
import src.maps.Street;
import src.maps.StreetMap;

public class TrafficManager {

  private final StreetMap streetMap;
  private Street street;
  private final TrafficManagerIcon icon;

  /**
   * Konstruktor tridy
   * @param root
   * @param streetMap
   */
  public TrafficManager(Group root, StreetMap streetMap) {
    this.streetMap = streetMap;
    this.setStreet(streetMap);
    this.icon = new TrafficManagerIcon(root, street, this);
  }

  /**
   * Zmeni trafficsize a barvy ulic
   * @param number
   */
  public void changeTrafficSize(int number) {
    this.street.addToTrafficSize(number);
    this.updateCurrentTraffic();
    this.streetMap.refreshBusRouteHighlight();
    this.streetMap.refreshDeTourSelection();
  }

  /**
   * Nastavuje aktualni text pomoci setCurrentTrafficText
   */
  private void updateCurrentTraffic() {
    icon.setCurrentTrafficText("Current traffic: " + this.street.getTrafficSize());
  }


  /**
   * Nastavuje aktualni text pomoci setTrafficText
   */
  private void updateTrafficText() {
    icon.setTrafficText("Street: " + this.street.getId());
  }

  /**
   * Nastavi this.street na street
   * @param street
   */
  public void setStreet(Street street) {
    this.street = street;
    updateTrafficText();
    updateCurrentTraffic();
  }

  /**
   * Nastavi this.street na prvni ulici v streetMap
   * @param streetMap
   */
  private void setStreet(StreetMap streetMap) {
    this.street = streetMap.getStreetList().get(0);
  }
}
