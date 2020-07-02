/**
* Trida reprezentujici krizovatku
* @author Matěj sojka (xsojka04)
* @author Jan Hranický (xhrani02)
*
*/
package src.maps;

import java.util.List;

import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class CrossRoad {
  private final Coordinate crossRoadCord;
  private final int id;
  private final List<Street> streetList;
  private Stop stop;
  private Circle node;

  /**
  * kontruktor tridy
  * @param cord - souradnice krizovatky
  * @param id - oznaceni
  *
  */
  public CrossRoad(Coordinate cord, int id) {
    this.crossRoadCord = cord;
    this.id = id;
    this.streetList = new ArrayList<>();
  }

  /**
  * Prida ulici do seznamu ulic, ktere krizovatka spojuje
  * @param street - ulice pro pridani
  *
  */
  public void addStreet(Street street) {
    if (!streetList.contains(street)) {
      streetList.add(street);
    }
  }

  /**
  * Vykresli krizovatku
  * @param root - skupinu pod kterou se ma krizovatka vykreslit
  * @param streetMap - mapa do ktere krizovatka spada
  *
  */
  public void drawCrossRoad(Group root, StreetMap streetMap) {
    node = new Circle();
    node.setCenterX(this.crossRoadCord.getX());
    node.setCenterY(this.crossRoadCord.getY());
    node.setRadius(7);
    node.setFill(Color.BLUE);
    root.getChildren().add(node);
    Tooltip tp = new Tooltip("Krizovatka:" + this.id + "\n Souradnice - [" + this.getCord().getX() + "," + this.getCord().getY() + "]");
    Tooltip.install(node, tp);
    CrossRoad temp = this;
    node.setOnMousePressed(mouseEvent -> streetMap.crossRoadClick(temp));
  }

  /**
  * Getter vracejici pozici krizovatky
  * @return Coordinate - pozice krizovatky
  *
  */
  public Coordinate getCord() {
    return this.crossRoadCord;
  }

  /**
  * Getter vracejici seznam ulic ktere krizovatka spojuje
  * @return List&#060;Street&#062; - seznam ulic
  *
  */
  public List<Street> getStreetList() {
    return streetList;
  }

  /**
  * Getter vracejici oznaceni krizovatky
  * @return int - id krizovatky
  *
  */
  public int getId() {
    return this.id;
  }

  /**
  * Getter vracejici sekci mezi krizovatkama
  * @param newCrossRoad - druha krizovatka
  * @return StreetSection - null v pripade ze krizovatky nespojuje sekce
  *
  */
  public StreetSection getStreetSection(CrossRoad newCrossRoad) {
    for (Street street : this.getStreetList()) {
      List<StreetSection> sections = street.getStreetSections(newCrossRoad);
      if (!sections.isEmpty()) {
        for (StreetSection streetSection : sections) {
          if (streetSection.getEnd().getCord() == this.getCord() || streetSection.getStart().getCord() == this.getCord()) {
            return streetSection;
          }
        }
      }
    }
    return null;
  }

  /**
  * Zvyrazni krizovatku
  */
  public void highLightCrossRoad() {
    this.node.setFill(Color.GREEN);
  }

  /**
  * Zrusi zvyrazneni krizovatky
  */
  public void stopHighLightCrossRoad() {
    this.node.setFill(Color.BLUE);
  }

  /**
  * Setter nastavujici krizovatce zastavku
  * @param stop - zastavka pro pridani
  */
  public void setStop(Stop stop) {
    this.stop = stop;
  }

  /**
  * Getter vracejici zastavku krizovatky
  * @return stop - null v pripade ze krizovatka neobsahuje zastavku
  */
  public Stop getStop() {
    return this.stop;
  }
}
