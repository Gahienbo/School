/**
 * Trida souradnice 
 * @author Matěj sojka (xsojka04)
 * @author Jan Hranický (xhrani02)
 *
 */
package src.maps;

public class Coordinate {
  private double x;
  private double y;

  /**
  * Konstruktor 
  * @param x - X-ova souradnice 
  * @param y - Y-ova souradnice
  */
  public Coordinate(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
  * Getter vracejici X-ovou souradnici 
  * @return Double - X-ova souradnice 
  */
  public double getX() {
    return this.x;
  }

  /**
  * Getter vracejici Y-ovou souradnici 
  * @return Double - Y-ova souradnice 
  */
  public double getY() {
    return this.y;
  }

  /**
  * Setter nastavujici X-ovou souradnici 
  * @param x - X-ova souradnice 
  */
  public void setX(double x) {
    this.x = x;
  }

  /**
  * Setter nastavujici Y-ovou souradnici 
  * @param y - Y-ova souradnice 
  */
  public void setY(double y) {
    this.y = y;
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
    if (!(objectToCompare instanceof Coordinate)) {
      return false;
    }
    Coordinate recast = (Coordinate) objectToCompare;

    return x == recast.x && y == recast.y;
  }
}