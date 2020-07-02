/**
 * Hlavni trida programu - Zajistuje vytvoreni okna a sceny
 * @author Matěj sojka (xsojka04)
 * @author Jan Hranický (xhrani02)
 *
 */

package src;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import src.managers.*;

public class Main extends Application {
  private double diffX;
  private double diffY;

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("IJA2020 - projekt");
    primaryStage.setResizable(false);
    Group root = new Group();
    Scene scene = new Scene(root, 700, 700, Color.GRAY);

    primaryStage.setScene(scene);
    primaryStage.show();

    MapManager mapManager = new MapManager(root);
    mapManager.drawMap();
    mapManager.getStreetMap().setTrafficManager(new TrafficManager(root, mapManager.getStreetMap()));
    mapManager.getStreetMap().setDeTourManager(new DeTourManager(mapManager.getStreetMap(), root));

    TimeManager timeManager = new TimeManager(root);
    timeManager.setTimeSpeed(1);
    timeManager.startAnimation(new BusManager(mapManager.getStreetMap().getBusLineList(), mapManager.getBusGroup()));

    SpeedManager speedManager = new SpeedManager(root, scene, timeManager);
    speedManager.startScrollBar();

    scene.setOnScroll(event -> {
      if (!speedManager.getMoveFlag()) {
        double zoomFactor = event.getDeltaY() > 0 ? 1.05 : 0.93;
        mapManager.getMapGroup().setScaleX(mapManager.getMapGroup().getScaleX() * zoomFactor);
        mapManager.getMapGroup().setScaleY(mapManager.getMapGroup().getScaleY() * zoomFactor);
      }
    });
    scene.setOnMousePressed(mouseEvent -> {
      if (!speedManager.getMoveFlag()) {
        diffX = mouseEvent.getX() - mapManager.getMapGroup().getLayoutX();
        diffY = mouseEvent.getY() - mapManager.getMapGroup().getLayoutY();
      }
    });
    scene.setOnMouseDragged(mouseEvent -> {
      if (!speedManager.getMoveFlag()) {
        mapManager.getMapGroup().setLayoutX(mouseEvent.getX() - diffX);
        mapManager.getMapGroup().setLayoutY(mouseEvent.getY() - diffY);
      }
    });
  }
}