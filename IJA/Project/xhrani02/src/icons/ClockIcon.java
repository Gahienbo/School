/**
 * Trida reprezentujici hodiny
 * @author Matěj sojka (xsojka04)
 * @author Jan Hranický (xhrani02)
 *
 */
package src.icons;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ClockIcon {
  private final Text clockText;

  /**
   * Vytvori ikonu hodin v okne
   * @param root
   */
  public ClockIcon(Group root) {
    Rectangle r = new Rectangle(0, 0, 100, 50);
    r.setFill(Color.BLACK);
    r.toFront();
    root.getChildren().add(r);
    clockText = new Text(15, 35, "");
    clockText.setFont(new Font(30));
    clockText.setFill(Color.GHOSTWHITE);
    clockText.toFront();
    root.getChildren().add(clockText);
  }
  /**
   * Nastavi cas na hodnotu parmetru
   * @param currentTimeString
   */
  public void setTime(String currentTimeString) {
    clockText.setText(currentTimeString);
  }
}
