/**
 * Trida starajici se o rychlost pohybu autobusu
 * @author Matěj sojka (xsojka04)
 * @author Jan Hranický (xhrani02)
 *
 */
package src.managers;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import src.maps.Coordinate;

public class SpeedManager {

  private final Group root;
  private Circle scrollButton;
  private Text speedText;
  private int speedValue;
  private final double startX;
  private final double startY;
  private final double endX;
  private final double endY;
  private final int offset;
  private boolean moveFlag = false;
  private final TimeManager timeManager;

  /**
   * Konstruktor tridy
   * @param root
   * @param scene
   * @param timeManager
   */
  public SpeedManager(Group root, Scene scene, TimeManager timeManager) {
    this.root = root;
    this.startX = this.endX = scene.getHeight() - 20;
    this.startY = 20;
    this.offset = (int) this.startY;
    this.endY = scene.getWidth() - scene.getWidth() / 2 - 20;
    this.timeManager = timeManager;
  }

  /**
   * Vytvori posuvnik a text s aktualni rychlosti casu
   */
  public void startScrollBar() {
    this.createScrollBar();
    this.createScrollButton();
    this.createSpeedIndicator();
  }

  /**
   * Vytvari text zobrazujici aktualni rychlost casu
   */
  public void createSpeedIndicator() {
    speedText = new Text(15, 75, "Speed : " + this.speedValue);
    speedText.setFont(new Font(30));
    speedText.setFill(Color.GHOSTWHITE);
    speedText.toFront();
    this.root.getChildren().add(speedText);
  }

  /**
   * Aktualizuje timeSpeed
   */
  public void updateSpeed() {
    this.speedText.setText("Speed : " + this.speedValue);
    this.timeManager.setTimeSpeed(this.speedValue);
  }

  /**
   * Vytvari posuvnik
   */
  public void createScrollBar() {
    Line sBarCenter = new Line(this.startX, this.startY, this.endX, this.endY);
    sBarCenter.setStroke(Color.DARKGREY);
    sBarCenter.setStrokeWidth(5);
    root.getChildren().add(sBarCenter);
  }

  /**
   * Vytvari posuvne tlacitko na posuvniku
   */
  public void createScrollButton() {
    this.scrollButton = new Circle();
    scrollButton.setCenterY(this.startY);
    scrollButton.setCenterX(this.endX);
    scrollButton.setRadius(15);
    scrollButton.setFill(Color.GHOSTWHITE);
    this.speedValue = (int) (((scrollButton.getCenterY() - offset) / (endY - startY)) * 60 + 1);
    this.root.getChildren().add(scrollButton);
    final Coordinate deltaCord = new Coordinate(0, 0);
    scrollButton.setOnMousePressed(mouseEvent -> {
      moveFlag = true;
      deltaCord.setY(scrollButton.getCenterY() - mouseEvent.getY());
    });
    scrollButton.setOnMouseDragged(mouseEvent -> {
      if ((mouseEvent.getSceneY() + deltaCord.getY()) < startY) {
        scrollButton.setCenterY(startY);
      } else if ((mouseEvent.getSceneY() + deltaCord.getY()) > endY) {
        scrollButton.setCenterY(endY - 1);
      } else {
        scrollButton.setCenterY(mouseEvent.getSceneY() + deltaCord.getY());
      }
      updateSpeed();
      speedValue = (int) (((scrollButton.getCenterY() - offset) / (endY - startY)) * 60 + 1);
    });

    scrollButton.setOnMouseReleased(mouseEvent -> moveFlag = false);
  }

  /**
   * @return moveFlag
   */
  public boolean getMoveFlag() {
    return this.moveFlag;
  }
}
