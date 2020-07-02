/**
* Trida reprezentujici zastavku
* @author Matěj sojka (xsojka04)
* @author Jan Hranický (xhrani02)
*
*/
package src.maps;

import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class Stop {
  private final String stopName;
  private final Coordinate coordinate;
  private Circle node;
  private final CrossRoad crossRoad;
/**
* kontruktor tridy
* @param stopName - jmeno zastavky
* @param crossRoad - krizovatka na ktere zastavka lezi
*/
  public Stop(String stopName, CrossRoad crossRoad) {
    this.stopName = stopName;
    this.coordinate = crossRoad.getCord();
    this.crossRoad = crossRoad;
    this.crossRoad.setStop(this);
  }
/**
* Getter vracejici id zastavky
* @return String - id zastavky
*/
  public String getId() {
    return this.stopName;
  }
/**
* Getter vracejici souradnici zastavky
* @return Coordinate - souradnice zastavky
*/
  public Coordinate getCoordinate() {
    return this.coordinate;
  }
/**
* Getter vracejici objekt krizovatky ne ktere zastavka lezi
* @return CrossRoad - objekt krizovatky
*/
  public CrossRoad getCrossRoad() {
    return crossRoad;
  }
  /**
  * Porovnava dva objekty 
  * @param objectToCompare - objekt k porovnani
  * @return boolean - v pripade ze se objekty rovnaji vraci true
  */
  @Override
  public boolean equals(Object objectToCompare) {
    if (objectToCompare == this) {
      return true;
    }
    if (!(objectToCompare instanceof Stop)) {
      return false;
    }
    Stop recast = (Stop) objectToCompare;

    return stopName.equals(recast.stopName);
  }
  /**
  * Vraci jmeno zastavky ve formatu stop(id)
  * @return string - jmeno zastavky ve formatu stop(id)
  */
  @Override
  public String toString() {
    return "stop(" + this.getId() + ")";
  }
  /**
  * Getter vracejici sekci mezi zastavkou a krizovatkou
  * @param crossRoad 
  * @return StreetSection - vraci null v pripade ze krizovatku a sekci nespojuje sekce
  */
  public StreetSection getStreetSection(CrossRoad crossRoad) {
    return this.crossRoad.getStreetSection(crossRoad);
  }
  /**
  * Zvyrazni zastavku
  */
  public void highLightStop() {
    this.node.setFill(Color.GREEN);
  }
  /**
  * Zastavi zvyrazneni zastavky
  */
  public void stopHighLightStop() {
    this.node.setFill(Color.DARKRED);
  }
/**
* Vykresli zastavku
* @param root - skupinu pod kterou se ma zastavka vykreslit
* @param streetMap - mapa do ktere zastavka spada
*
*/
  public void drawStop(Group root, StreetMap streetMap) {
    node = new Circle();
    node.setCenterX(this.getCoordinate().getX());
    node.setCenterY(this.getCoordinate().getY());
    node.setRadius(7);
    node.setFill(Color.DARKRED);
    root.getChildren().add(node);
    Tooltip tp = new Tooltip("Zastavka: " + this.stopName + "\nKrizovatka: " + this.crossRoad.getId() + "\nSouradnice - [" + this.coordinate.getX() + "," + this.coordinate.getY() + "]");
    Tooltip.install(node, tp);
    this.node.setOnMouseClicked(event -> streetMap.stopClick(this));
  }
}
