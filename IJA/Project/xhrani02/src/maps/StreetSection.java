/**
 * Trida reprezentujici sekci ulice
 * @author Matěj sojka (xsojka04)
 * @author Jan Hranický (xhrani02)
 *
 */
package src.maps;

import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import static java.lang.Math.sqrt;

public class StreetSection {

  private final CrossRoad sectionStart;
  private final CrossRoad sectionEnd;
  private final String id;
  private Street street;
  private Line line;

  /**
   * Konstruktor tridy
   * @param start
   * @param end
   * @param id
   */
  public StreetSection(CrossRoad start, CrossRoad end, String id) {
    this.sectionStart = start;
    this.sectionEnd = end;
    this.id = id;
  }

  /**
   * Nastavi ulici ktere sekce patri
   * @param street
   */
  public void setStreet(Street street) {
    this.street = street;
    sectionStart.addStreet(street);
    sectionEnd.addStreet(street);
  }

  /**
   * Getter vracejici zacatek sekce
   * @return krizovatka na zacatku sekce
   */
  public CrossRoad getStart() {
    return this.sectionStart;
  }

  /**
   * Getter vracejici konec sekce
   * @return krizovatka na konci sekce
   */
  public CrossRoad getEnd() {
    return this.sectionEnd;
  }

  /**
   * Getter vracejici ulici pod kterou sekce patri
   * @return ulice pod kterou sekce patri
   */
  public Street getStreet() {
    return this.street;
  }

  /**
   * Getter vracejici id ulice
   * @return id ulice
   */
  public String getId() {
    return this.id;
  }

  /**
   * Zjistuje jestli sekce dana paramterem navazuje na aktualni sekci
   * @param s sekce
   * @return true/false
   */
  public boolean follows(StreetSection s) {
    if (s.getStart().getCord() == getStart().getCord())
      return true;
    if (s.getEnd().getCord() == getEnd().getCord())
      return true;
    if (s.getStart().getCord() == getEnd().getCord())
      return true;
    return s.getEnd().getCord() == getStart().getCord();
  }

  /**
   * Vykresli sekci
   * @param root
   * @param streetMap
   */
  public void drawSection(Group root, StreetMap streetMap) {
    Coordinate streetStart = this.getStart().getCord();
    Coordinate streetEnd = this.getEnd().getCord();
    Line newStreet = new Line(streetStart.getX(), streetStart.getY(), streetEnd.getX(), streetEnd.getY());
    newStreet.setStroke(Color.GHOSTWHITE);
    newStreet.setStrokeWidth(10);
    this.line = newStreet;
    root.getChildren().add(newStreet);
    Tooltip tp = new Tooltip("Sekce: " + this.id + " Ulice: " + this.street.getId() + "\n Zacatek - [" + this.sectionStart.getCord().getX() + "," + this.sectionStart.getCord().getY() + "] \n Konec -[" + this.sectionEnd.getCord().getX() + "," + this.sectionEnd.getCord().getY() + "]");
    Tooltip.install(newStreet, tp);
    this.line.setOnMouseClicked(event -> streetMap.streetSectionClick(this));

  }

  /**
   * Obarvi sekci
   * @param c pozadovana barva
   */
  public void colorSection(Color c) {
    this.line.setStroke(c);
  }

  /**
   * Obarvi sekci podle jejiho stupne dopravy
   * @param trafficSize stupen dopravy
   */
  public void colorTrafficSection(int trafficSize) {
    this.line.setStroke(new Color(1, 1 - (0.1 * trafficSize), 1 - (0.1 * trafficSize), 1));
  }

  /**
   * Vraci nazev ulice
   * @return nazev ulice ve formatu StreetSection(id)
   */
  @Override
  public String toString() {
    return "StreetSection(" + this.getId() + ")";
  }

  /**
   * Vraci spolecnou krizovatku dvou sekci
   * @param s
   * @return krizovatka kterou maji sekce spolecnou nebo null
   */
  public CrossRoad getCommonCrossRoad(StreetSection s) {
    if (this.getStart().getCord() == s.getStart().getCord()) {
      return this.getStart();
    }
    if (this.getStart().getCord() == s.getEnd().getCord()) {
      return this.getStart();
    }
    if (this.getEnd().getCord() == s.getStart().getCord()) {
      return this.getEnd();
    }
    if (this.getEnd().getCord() == s.getEnd().getCord()) {
      return this.getEnd();
    }
    return null;
  }

  /**
   * Getter vracejici za jakou dobu dojede autobus na konec ulice
   * @param currentPosition
   * @param sectionEnd
   * @param busSpeed
   * @return
   */
  public double getTimeTillEnd(Coordinate currentPosition, Coordinate sectionEnd, int busSpeed) {
    double distance = getDistance(currentPosition, sectionEnd);
    return distance / (busSpeed - street.getTrafficSize());
  }

  /**
   * Vraci vzdalenost k zadanemu bodu
   * @param currentPosition
   * @param end
   * @return
   */
  private double getDistance(Coordinate currentPosition, Coordinate end) {
    double a = currentPosition.getX() - end.getX();
    double b = currentPosition.getY() - end.getY();
    return sqrt(a * a + b * b);
  }

  /**
   * Pocita novou pozici autobusu
   * @param currentPosition
   * @param sectionEnd
   * @param busSpeed
   * @param passedTime
   * @return
   */
  public Coordinate getNewPosition(Coordinate currentPosition, Coordinate sectionEnd, int busSpeed, double passedTime) {
    double timeTillEnd = getTimeTillEnd(currentPosition, sectionEnd, busSpeed);
    double diffX = sectionEnd.getX() - currentPosition.getX();
    double diffY = sectionEnd.getY() - currentPosition.getY();
    diffX = (diffX / timeTillEnd) * passedTime;
    diffY = (diffY / timeTillEnd) * passedTime;
    if (timeTillEnd < passedTime) {
      return sectionEnd;
    }
    return new Coordinate(currentPosition.getX() + diffX, currentPosition.getY() + diffY);
  }

  /**
   * Vraci druhou stranu sekce
   * @param c jeden z krajnich bodu sekce
   * @return
   */
  public Coordinate GetOtherEnd(Coordinate c) {
    if (c == getStart().getCord())
      return getEnd().getCord();
    return getStart().getCord();
  }
}
