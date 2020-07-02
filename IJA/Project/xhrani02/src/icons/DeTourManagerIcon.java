/**
 * Trida starajici se GUI objizdek
 * @author Matěj sojka (xsojka04)
 * @author Jan Hranický (xhrani02)
 *
 */
package src.icons;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import src.managers.DeTourManager;

public class DeTourManagerIcon {

  public Rectangle routeWindow;
  public Text titleText = new Text();
  public Text bodyText = new Text();
  public Button stopButton = new Button();
  public Button startButton = new Button();
  public Button startButtonSmall = new Button();
  public Button setButton = new Button();
  public Button showDeRouteButton = new Button();
  public Button nextButton = new Button();
  public Button deleteButton = new Button();
  public Button setDelayTimeButton = new Button();
  public Label delayLabel;
  public TextField delayTextField;

  /**
   * Kontruktor tridy
   * @param deTourManager
   * @param root
   */
  public DeTourManagerIcon(DeTourManager deTourManager, Group root) {
    createRouteWindow(root);
    createText(titleText, 20, 580, 20, root);
    createText(bodyText, 20, 605, 15, root);
    createButton(showDeRouteButton, 300, 560, "Show\ndetour", 15, 100, 120, deTourManager, root);
    createButton(startButton, 300, 560, "Create\ndetour", 15, 100, 120, deTourManager, root);
    createButton(setButton, 340, 555, "Set", 10, 50, 50, deTourManager, root);
    createButton(nextButton, 340, 555, "Next", 10, 50, 50, deTourManager, root);
    createButton(deleteButton, 340, 555, "Delete", 10, 50, 50, deTourManager, root);
    createButton(startButtonSmall, 340, 610, "Add", 10, 50, 50, deTourManager, root);
    createButton(stopButton, 340, 610, "Stop", 10, 50, 50, deTourManager, root);
    createButton(setDelayTimeButton, 300, 560, "Set\ndelay", 15, 100, 120, deTourManager, root);
    createDelayIcons(root);
  }

  /**
   * Vytvori pozadovany text
   * @param text - obsah
   * @param x - Xova souradnice
   * @param y - Yova souradnice
   * @param fontSize - velikost fontu
   * @param root - skupina
   */
  private void createText(Text text, double x, double y, double fontSize, Group root) {
    text.setX(x);
    text.setY(y);
    text.setFont(new Font(fontSize));
    text.setFill(Color.GHOSTWHITE);
    text.toFront();
    text.setVisible(false);
    root.getChildren().add(text);
  }

  /**
   * Vytvori Okno ve kterem se zobarzuji informace o objizdkach
   * @param root
   */
  private void createRouteWindow(Group root) {
    routeWindow = new Rectangle(0, 550, 440, 700);
    routeWindow.setFill(Color.DARKGREY);
    routeWindow.setVisible(false);
    root.getChildren().add(routeWindow);
  }

  /**
   * Vytvari tlacitka pro ovladani nastaveni objizdky
   * @param button -
   * @param x - Xova souradnice
   * @param y - Y souradnice
   * @param text - napis na tlacitku
   * @param fontSize - velikost fontu
   * @param width - sirka
   * @param height - vyska
   * @param deTourManager
   * @param root
   */
  private void createButton(Button button, int x, int y, String text, int fontSize, int width, int height, DeTourManager deTourManager, Group root) {
    button.setLayoutX(x);
    button.setLayoutY(y);
    button.setText(text);
    button.setFont(new Font(fontSize));
    button.setMinWidth(width);
    button.setMinHeight(height);
    button.setMaxWidth(width);
    button.setMaxHeight(height);
    button.setVisible(false);
    addOnClick(button, deTourManager);
    root.getChildren().add(button);
  }

  /**
   * Vytvori label pro zadani konstanthio zpozdeni objizdky
   * @param root
   */
  private void createDelayIcons(Group root) {
    delayLabel = new Label("Delay:");
    delayLabel.setLayoutX(20);
    delayLabel.setLayoutY(600);
    delayLabel.toFront();
    delayLabel.setTextFill(Color.GHOSTWHITE);
    delayLabel.setVisible(false);
    delayTextField = new TextField();
    delayTextField.setLayoutX(20);
    delayTextField.setLayoutY(620);
    delayTextField.toFront();
    delayTextField.setVisible(false);
    root.getChildren().add(delayLabel);
    root.getChildren().add(delayTextField);
  }

  /**
   * Zobrazi/Schova label pro zadani zpozdeni
   * @param isVisible
   */
  public void setDelayIconsVisible(boolean isVisible) {
    delayLabel.setVisible(isVisible);
    delayTextField.setVisible(isVisible);
  }

  /**
   * Prida button metodu z deTourManager, ktera se bude volat na kliknuti
   * @param button
   * @param deTourManager
   */
  private void addOnClick(Button button, DeTourManager deTourManager) {
    if (this.startButton.equals(button) || this.startButtonSmall.equals(button)) {
      button.setOnMouseClicked(z -> deTourManager.handleStartClick());
    } else if (this.nextButton.equals(button)) {
      button.setOnMouseClicked(z -> deTourManager.handleNextButtonClick());
    } else if (this.stopButton.equals(button)) {
      button.setOnMouseClicked(z -> deTourManager.handleStopCLick());
    } else if (this.showDeRouteButton.equals(button)) {
      button.setOnMouseClicked(z -> deTourManager.handleShowRouteButtonClick());
    } else if (this.deleteButton.equals(button)) {
      button.setOnMouseClicked(z -> deTourManager.handleDeleteButtonClick());
    } else if (this.setButton.equals(button)) {
      button.setOnMouseClicked(z -> deTourManager.handleSetClick());
    } else if (this.setDelayTimeButton.equals(button)) {
      button.setOnMouseClicked(z -> deTourManager.handleSetDelayTimeButtonClick());
    }
  }

  /**
   * Nastavi text u bodyText
   * @param text
   */
  public void setBodyText(String text) {
    this.bodyText.setText(text);
    this.bodyText.setVisible(true);
  }

  /**
   * Nastavi text u titleText
   * @param text
   */
  public void setTitleText(String text) {
    this.titleText.setText(text);
    this.titleText.setVisible(true);
  }

  /**
   * Schova veskere prvky UI objizdky
   */
  public void hide() {
    this.titleText.setVisible(false);
    this.routeWindow.setVisible(false);
    this.setButton.setVisible(false);
    this.startButton.setVisible(false);
    this.startButtonSmall.setVisible(false);
    this.stopButton.setVisible(false);
    this.bodyText.setVisible(false);
    this.showDeRouteButton.setVisible(false);
    this.nextButton.setVisible(false);
    this.deleteButton.setVisible(false);
    this.setDelayIconsVisible(false);
    this.setDelayTimeButton.setVisible(false);
  }
}
