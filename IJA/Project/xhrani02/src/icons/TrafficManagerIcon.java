/**
 * Trida starajici se GUI stupne dopravy
 * @author Matěj sojka (xsojka04)
 * @author Jan Hranický (xhrani02)
 *
 */
package src.icons;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import src.managers.TrafficManager;
import src.maps.Street;

public class TrafficManagerIcon {
  private Text trafficText;
  private Text currentTraffic;

  /**
   * Konstruktor tridy
   * @param root
   * @param street
   * @param trafficManager
   */
  public TrafficManagerIcon(Group root, Street street, TrafficManager trafficManager) {
    Rectangle rectangle = new Rectangle(420, 550, 300, 300);
    rectangle.setFill(Color.DARKGREY);
    root.getChildren().add(rectangle);
    this.createText(root, street);
    this.createButtons(root, trafficManager);
  }

  /**
   * Vytvori tlacitka pro zvyseni/snizeni stupne dopravy
   * @param root
   * @param trafficManager
   */
  private void createButtons(Group root, TrafficManager trafficManager) {
    createButton(630, 570, "+", root, 45, 1, trafficManager);
    createButton(630, 630, "-", root, 45, -1, trafficManager);
  }

  /**
   * Vytvori konkretni tlacitko
   * @param x - Xova souradnice
   * @param y - Yova souradnice
   * @param buttonText - napis na tlacitko
   * @param root
   * @param size - velikost
   * @param trafficChange
   * @param trafficManager
   */
  private void createButton(double x, double y, String buttonText, Group root, double size, int trafficChange, TrafficManager trafficManager) {
    Button button = new Button();
    button.setLayoutX(x);
    button.setLayoutY(y);
    button.setText(buttonText);
    button.setFont(new Font(20));
    button.setMinWidth(size);
    button.setMinHeight(size);
    button.setMaxWidth(size);
    button.setMaxHeight(size);
    button.setOnMouseClicked(z -> trafficManager.changeTrafficSize(trafficChange));
    root.getChildren().add(button);
  }

  /**
   * Vytvori text s informacemi o stavu dopravy ulice
   * @param root
   * @param street
   */
  private void createText(Group root, Street street) {
    trafficText = new Text(440, 580, "Street: " + street.getId());
    trafficText.setFont(new Font(20));
    trafficText.setFill(Color.GHOSTWHITE);
    trafficText.toFront();
    root.getChildren().add(trafficText);

    currentTraffic = new Text(440, 680, "Current traffic: " + street.getTrafficSize());
    currentTraffic.setFont(new Font(20));
    currentTraffic.setFill(Color.GHOSTWHITE);
    currentTraffic.toFront();
    root.getChildren().add(currentTraffic);
  }

  /**
   * Nastavi text u currentTraffic na s
   * @param s
   */
  public void setCurrentTrafficText(String s) {
    currentTraffic.setText(s);
  }

  /**
   * Nastavi text u trafficText na s
   * @param s
   */
  public void setTrafficText(String s) {
    trafficText.setText(s);
  }
}
