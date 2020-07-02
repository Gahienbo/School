/**
 * Trida starajici se o pohyb Autobusu
 * @author Matěj sojka (xsojka04)
 * @author Jan Hranický (xhrani02)
 *
 */
package src.managers;

import javafx.scene.Group;
import src.maps.BusLine;
import src.vehicles.Bus;

import java.util.ArrayList;
import java.util.List;

public class BusManager {
  private final List<Bus> busList;
  private final List<BusLine> busLineList;
  private final Group root;

  /**
   * Konstuktor tridy
   * @param busLineList - seznam vsech linek autobusu
   * @param root
   */
  public BusManager(List<BusLine> busLineList, Group root) {
    this.busList = new ArrayList<>();
    this.busLineList = busLineList;
    this.root = root;
  }

  /**
   * Zajistuje pohyb autobusu
   * @param time - aktualni cas hodin
   */
  public void moveBuses(int time) {
    List<Bus> remove = new ArrayList<>();
    for (Bus bus : busList) {
      if (!bus.updateBusPosition(root, time)) {
        remove.add(bus);
      }
    }
    busList.removeAll(remove);
  }

  /**
   * Zajistuje vytvoreni novych autobusu
   * @param time - aktualni cas hodin
   */
  public void departureBuses(int time) {
    for (BusLine busLine : busLineList) {
      if (!busLine.isDeparture(time))
        continue;
      Bus bus = new Bus("Bus" + time, busLine, root, time, busLine.getStopDepartureTimes());
      busList.add(bus);
    }
  }

  /**
   * Zresetuje hodiny a autobusy na vychozi pozice
   */
  public void reset() {
    for (Bus bus : busList) {
      bus.deleteBusIcon(root);
    }
    busList.clear();
  }
}
