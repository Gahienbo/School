/**
 * Trida reprezentujici ikonu autobusu
 * @author Matěj sojka (xsojka04)
 * @author Jan Hranický (xhrani02)
 *
 */
package src.icons;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import src.maps.Coordinate;
import src.vehicles.Bus;

public class BusIcon {
  private final Circle circle;
  private final Text text;

  /**
   * Konstruktor třídy - nastavuje proměnné a zobrazí ikonu na mapě
   * @param bus -skupina autobusu
   * @param busGroup - aktualni souradnice ikony
   * @param coordinate - objekt autobusu na ktery je ikona vazana
   */
  public BusIcon(Group busGroup, Coordinate coordinate, Bus bus) {
    circle = new Circle();
    circle.setCenterX(coordinate.getX());
    circle.setCenterY(coordinate.getY());
    circle.setRadius(12);
    circle.setOnMouseClicked(event -> bus.onBusIconClick());
    circle.setFill(Color.ORANGE);

    text = new Text(Integer.toString(bus.getBusLine().getId()));
    text.setX(coordinate.getX() - 9);
    text.setY(coordinate.getY() + 6);
    text.setFont(new Font(18));
    text.setOnMouseClicked(event -> bus.onBusIconClick());
    text.setFill(Color.BLACK);
    busGroup.getChildren().add(circle);
    busGroup.getChildren().add(text);
  }

  /**
   * Posune ikonu na mape
   * @param diffX
   * @param diffY
   */
  public void move(double diffX, double diffY) {
    circle.setCenterX(circle.getCenterX() + diffX);
    circle.setCenterY(circle.getCenterY() + diffY);
    text.setX(text.getX() + diffX);
    text.setY(text.getY() + diffY);
  }

  /**
   * Odstrani ikonu z mapy
   * @param root
   */
  public void delete(Group root) {
    root.getChildren().remove(circle);
    root.getChildren().remove(text);
  }
}
