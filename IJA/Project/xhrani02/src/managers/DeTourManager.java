/**
 * Trida starajici se volbu objizdek
 * @author Matěj sojka (xsojka04)
 * @author Jan Hranický (xhrani02)
 *
 */
package src.managers;

import javafx.scene.Group;
import javafx.scene.control.CheckBox;
import javafx.scene.paint.Color;
import src.icons.DeTourManagerIcon;
import src.maps.*;

import java.util.ArrayList;

public class DeTourManager {

  private final ArrayList<CrossRoad> closedList;
  private final ArrayList<CrossRoad> deTourList;
  private final StreetMap streetMap;
  private final Group root;
  private BusLine busLine;
  private final DeTourManagerIcon icon;
  private final ArrayList<CheckBox> deToursToDelete;
  private final ArrayList<StreetSection> recentlyHighlightedStreetSections;
  private boolean firstClosedFlag = false;
  private boolean nextFlag = false;
  private DeTour deTour;

  /**
   * Konstruktor tridy
   * @param streetMap
   * @param root
   */
  public DeTourManager(StreetMap streetMap, Group root) {
    this.closedList = new ArrayList<>();
    this.deTourList = new ArrayList<>();
    this.streetMap = streetMap;
    this.root = root;
    this.recentlyHighlightedStreetSections = new ArrayList<>();
    this.deToursToDelete = new ArrayList<>();
    this.icon = new DeTourManagerIcon(this, root);
  }

  /**
   * Zobrazi deTourManager
   */
  public void startDetourManager() {
    if (!this.busLine.hasDeTour()) {
      this.icon.showDeRouteButton.setVisible(true);
    } else {
      this.icon.startButton.setVisible(true);
    }
    this.deToursToDelete.clear();
  }

  /**
   * Zobrazi seznam zastavek
   * @param stopList
   */
  public void showStopList(String stopList) {
    hide();
    this.icon.setTitleText("Bus line: " + this.busLine.getId());
    this.icon.setBodyText(stopList);
    this.icon.routeWindow.setVisible(true);
  }

  /**
   * Metoda pro zpracovani akce kliknuti na tlacitko next
   */
  public void handleNextButtonClick() {
    if (this.closedList.isEmpty()) {
      this.icon.setTitleText("Street not selected");
    } else {
      this.icon.setButton.setVisible(true);
      this.icon.nextButton.setVisible(false);
      this.icon.setTitleText("Selected:");
      this.icon.setBodyText("Select one or more \nstreet sections");
      this.streetMap.setClosedFlag();
      this.streetMap.setDeTourFlag();
      this.nextFlag = !this.nextFlag;
      this.deTourList.add(this.closedList.get(0));
      this.highLightPossibleChoices(this.closedList.get(0));
    }
  }

  /**
   * Zobrazi detour s checkBoxem v nabidce. DeTourNumber urcuje o kolikaty v seznamu jde
   * @param deTourNumber
   */
  private void createDeTourText(int deTourNumber) {
    CheckBox checkBox = new CheckBox("Detour" + (deTourNumber + 1));
    checkBox.setLayoutX(50);
    checkBox.setLayoutY(560 + 20 * deTourNumber);
    checkBox.setTextFill(Color.GHOSTWHITE);
    this.deToursToDelete.add(checkBox);
    this.root.getChildren().add(checkBox);
  }

  /**
   * Metoda pro zpracovani akce kliknuti na tlacitko showRoute
   */
  public void handleShowRouteButtonClick() {
    this.icon.showDeRouteButton.setVisible(false);
    this.icon.titleText.setVisible(false);
    this.icon.bodyText.setVisible(false);
    this.icon.routeWindow.setVisible(true);
    for (int i = 0; i < this.busLine.getDeRouteList().size(); i++) {
      createDeTourText(i);
    }
    this.busLine.highLightDeTour(Color.MEDIUMPURPLE, Color.VIOLET);
    this.icon.deleteButton.setVisible(true);
    this.icon.startButtonSmall.setVisible(true);
  }

  /**
   * Metoda pro zpracovani akce kliknuti na tlacitko delete
   */
  public void handleDeleteButtonClick() {
    this.deHighLightRecent();
    int deletedCounter = 0;
    for (int i = 0; i < this.deToursToDelete.size(); i++) {
      if (this.deToursToDelete.get(i).isSelected()) {
        this.busLine.removeDeTour(i - deletedCounter);
        deletedCounter++;
      }
    }
    this.hide();
    this.resetStreetColors();
    this.startDetourManager();
  }

  /**
   * Odstrani zvyrazneni objizdky
   */
  private void clearDeRouteHighLight() {
    this.deHighLightRecent();
    this.clearListHighlight(this.closedList);
    this.clearListHighlight(this.deTourList);
  }

  /**
   * Odstrani zvyrazneni z trasy definovanou pomoci list
   * @param list
   */
  private void clearListHighlight(ArrayList<CrossRoad> list) {
    for (int i = 0; i < list.size() - 1; i++) {
      list.get(i).stopHighLightCrossRoad();
      if (list.get(i).getStop() != null)
        list.get(i).getStop().stopHighLightStop();
      list.get(i).getStreetSection(list.get(i + 1)).colorSection(Color.GHOSTWHITE);
    }
    if (list.size() > 0)
      list.get(list.size() - 1).stopHighLightCrossRoad();
  }

  /**
   * Nastavi barvu ulic na puvodni a zvyrazni aktualni linku
   */
  private void resetStreetColors() {
    this.streetMap.resetStreetColor();
    this.streetMap.refreshTrafficHighlight();
    this.busLine.highLightRoute();
  }

  /**
   * Metoda pro zpracovani akce kliknuti na tlacitko start
   */
  public void handleStartClick() {
    this.hide();
    this.resetStreetColors();
    this.streetMap.setInSelectionFlag();
    this.busLine.highLightCrossRoads();
    this.icon.routeWindow.setVisible(true);
    this.icon.nextButton.setVisible(true);
    this.icon.stopButton.setVisible(true);
    this.icon.startButton.setVisible(false);
    this.icon.titleText.setVisible(false);
    this.icon.setTitleText("Selected:");
    this.streetMap.setClosedFlag();
    this.firstClosedFlag = !this.firstClosedFlag;
  }

  /**
   * Metoda pro zpracovani akce kliknuti na tlacitko set
   */
  public void handleSetClick() {
    this.deTour = new DeTour();
    deTour.setRoute(this.deTourList);
    deTour.setSkippedRoute(this.closedList);
    this.busLine.addDeTourList(deTour);
    this.nextFlag = !this.nextFlag;
    this.icon.stopButton.setVisible(false);
    this.icon.setButton.setVisible(false);
    this.icon.bodyText.setVisible(false);
    this.icon.setTitleText("Bus line delay:");
    this.icon.setDelayIconsVisible(true);
    this.icon.setDelayTimeButton.setVisible(true);
  }

  /**
   * Metoda pro zpracovani akce kliknuti na tlacitko delay
   */
  public void handleSetDelayTimeButtonClick() {
    if ((this.icon.delayTextField.getText() != null && !this.icon.delayTextField.getText().isEmpty())) {
      try {
        this.deTour.setDelay(Integer.parseInt(this.icon.delayTextField.getText()));
        this.handleStopCLick();
      } catch (NumberFormatException e) {
        this.icon.delayLabel.setText("Delay must be a number");
      }
    } else {
      this.icon.delayLabel.setText("Enter delay");
    }
  }

  /**
   * Metoda pro zpracovani akce kliknuti na tlacitko stop
   */
  public void handleStopCLick() {
    this.clearDeRouteHighLight();

    this.hide();
    this.deTourList.clear();
    this.closedList.clear();
    if (this.streetMap.getClosedFlag())
      this.streetMap.setClosedFlag();
    if (this.streetMap.getDeRouteFlag())
      this.streetMap.setDeTourFlag();
    this.busLine.highLightRoute();
    if (this.nextFlag) {
      this.nextFlag = false;
    } else {
      this.busLine.stopHighLightCrossroads();
    }
    this.streetMap.setInSelectionFlag();
    this.streetMap.refreshTrafficHighlight();
    this.streetMap.refreshBusRouteHighlight();
  }

  /**
   * Metoda pro zpracovani akce kliknuti na tlacitko stopHighlight
   */
  public void handleStopHighlight() {
    this.clearDeRouteHighLight();
    this.hide();
    this.deTourList.clear();
    this.closedList.clear();
    if (this.streetMap.getClosedFlag()) this.streetMap.setClosedFlag();
    if (this.streetMap.getDeRouteFlag()) {
      this.streetMap.setDeTourFlag();
    }
    if (this.nextFlag) {
      this.nextFlag = false;
    }
    if (this.busLine != null) {
      this.busLine.stopHighLightCrossroads();
      this.busLine.highLightDeTour(Color.GHOSTWHITE, Color.GHOSTWHITE);
    }
  }

  /**
   * Nastavi busline
   * @param line
   */
  public void setBusLine(BusLine line) {
    this.busLine = line;
  }

  /**
   * Krizovatky z list dava do retezce po 3 na radek
   * @param list
   * @return retezec s id krizovatek
   */
  public String getListString(ArrayList<CrossRoad> list) {
    StringBuilder returnString = new StringBuilder();
    for (int i = 0; i < list.size(); i++) {
      if (i % 3 == 0 && i != 0) {
        returnString.append("\n");
      }
      returnString.append(list.get(i).getId());
      returnString.append(" ");
    }
    return returnString.toString();
  }

  /**
   * Schova ikonu a checkBoxy
   */
  public void hide() {
    this.icon.hide();
    for (CheckBox checkBox : this.deToursToDelete) {
      this.root.getChildren().remove(checkBox);
    }
  }

  /**
   * Pokusi se pridat krizovatku do seznamu krizovatek
   * @param crossRoad
   */
  public void addCrossRoadToDeRouteList(CrossRoad crossRoad) {
    if (deTourList.get(deTourList.size() - 1) == crossRoad || !deTourList.contains(crossRoad)) {
      this.deHighLightRecent();
      this.recentlyHighlightedStreetSections.clear();
    }
    if (!deleteIfInList(deTourList, crossRoad)) {

      if (deTourList.size() == 0 && this.closedList.get(0).getStreetSection(crossRoad) != null
        || deTourList.size() != 0 && deTourList.get(deTourList.size() - 1).getStreetSection(crossRoad) != null) {
        deTourList.add(crossRoad);
        this.streetMap.refreshTrafficHighlight();
        this.streetMap.refreshBusRouteHighlight();
        this.refreshChosenDeRoute();
        this.highLightPossibleChoices(crossRoad);
        this.busLine.highLightChosenStreetSections(deTourList, Color.VIOLET);
        crossRoad.highLightCrossRoad();
      }
      if (!deTourList.isEmpty()) {
        this.highLightPossibleChoices(this.deTourList.get(this.deTourList.size() - 1));
      }
    }
    this.icon.setBodyText("Selected crossroads: " + this.getListString(deTourList));
  }

  /**
   * Zvyrazni zvolenou objizdku
   */
  public void refreshChosenDeRoute() {
    if (this.deTourList.size() >= 2) {
      for (int i = 0; i < this.deTourList.size() - 1; i++) {
        this.deTourList.get(i).getStreetSection(this.deTourList.get(i + 1)).colorSection(Color.VIOLET);
      }
    }
    if (this.closedList.size() >= 2) {
      for (int i = 0; i < this.closedList.size() - 1; i++) {
        this.closedList.get(i).getStreetSection(this.closedList.get(i + 1)).colorSection(Color.MEDIUMPURPLE);
      }
    }
    for (StreetSection s : this.recentlyHighlightedStreetSections) {
      s.colorSection(Color.GREEN);
    }
  }

  /**
   * Pri krizovatku crossRoad do closedList
   * @param crossRoad
   */
  public void addCrossRoadToClosedList(CrossRoad crossRoad) {
    if (this.firstClosedFlag) {
      this.busLine.stopHighLightCrossroads();
      this.firstClosedFlag = !this.firstClosedFlag;
    }
    if (!deleteIfInList(closedList, crossRoad)) {
      if (this.busLine.belongsToLine(crossRoad)) {
        if (this.closedList.size() == 0) {
          this.closedList.add(crossRoad);
        } else if (this.closedList.get(this.closedList.size() - 1).getStreetSection(crossRoad) != null) {
          this.closedList.add(crossRoad);
        }
      }
    }
    this.icon.setBodyText("Selected crossroads: " + this.getListString(this.closedList));
    this.busLine.stopHighLightCrossroads();
    this.busLine.highLightRoute();
    this.highLightChosen();
    this.busLine.highLightChosenStreetSections(this.closedList, Color.MEDIUMPURPLE);
  }

  /**
   * Zvyrazni closedList
   */
  public void highLightChosen() {
    for (CrossRoad cs : this.closedList) {
      cs.highLightCrossRoad();
      if (cs.getStop() != null)
        cs.getStop().highLightStop();
    }
  }

  /**
   * Nastavi barvu obarvene trasy zpet na puvodni
   */
  private void deHighLightRecent() {
    for (StreetSection ss : this.recentlyHighlightedStreetSections) {
      ss.colorSection(Color.GHOSTWHITE);
    }
  }

  /**
   * Zvyrazni na mape krizovatky kudy muze vest objizdka
   * @param cs
   */
  private void highLightPossibleChoices(CrossRoad cs) {
    if (cs != this.closedList.get(this.closedList.size() - 1)) {
      for (Street s : cs.getStreetList()) {
        for (StreetSection ss : s.getStreetSectionList()) {
          StreetSection available;
          available = cs.getStreetSection(ss.getStart());
          if (available != null && (!this.busLine.belongsToLine(available.getEnd()) || !this.busLine.belongsToLine(available.getStart()))) {
            if (!this.deTourList.contains(available.getStart())) {
              available.colorSection(Color.GREEN);
              this.recentlyHighlightedStreetSections.add(available);
            }
          }
          available = cs.getStreetSection(ss.getEnd());
          if (available != null && (!this.busLine.belongsToLine(available.getEnd()) || !this.busLine.belongsToLine(available.getStart()))) {
            if (!this.deTourList.contains(available.getEnd())) {
              available.colorSection(Color.GREEN);
              this.recentlyHighlightedStreetSections.add(available);
            }
          }
        }
      }
    }
  }

  /**
   * Odstrani crossRoad z list pokud tam je
   * @param list
   * @param crossRoad
   * @return Je true pokud crossRoad je v list
   */
  public boolean deleteIfInList(ArrayList<CrossRoad> list, CrossRoad crossRoad) {
    if (!list.isEmpty()) {
      if (list.get(list.size() - 1) == crossRoad) {
        list.remove(crossRoad);
        return true;
      } else return list.contains(crossRoad);
    }
    return false;
  }

  /**
   * @return busline
   */
  public BusLine getBusLine() {
    return this.busLine;
  }

  /**
   * Nastavi busline na null
   */
  public void resetBusLine() {
    this.busLine = null;
  }
}
