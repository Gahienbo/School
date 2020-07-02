/**
 * Trida starajici se o vykresleni mapy
 * @author Matěj sojka (xsojka04)
 * @author Jan Hranický (xhrani02)
 *
 */
package src.managers;

import javafx.scene.Group;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import src.maps.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static src.managers.TimeManager.DAY_LENGTH;

public class MapManager {
  private StreetMap streetMap;
  private final Group root;
  private final Group mapGroup;
  private final Group streetGroup;
  private final Group intersectionGroup;
  private final Group stopGroup;
  private final Group busGroup;

  /**
   * Konstruktor tridy
   * @param root
   */
  public MapManager(Group root) {
    this.root = root;
    this.mapGroup = newGroup(root);
    this.streetGroup = newGroup(mapGroup);
    this.intersectionGroup = newGroup(mapGroup);
    this.stopGroup = newGroup(mapGroup);
    this.busGroup = newGroup(mapGroup);
  }

  /**
   * Getter vracejici skupinu na ktere je vykreslena mapa
   * @return Group
   */
  public Group getMapGroup() {
    return this.mapGroup;
  }

  /**
   * Vytvori novou skupinu
   * @param root
   * @return
   */
  private Group newGroup(Group root) {
    Group group = new Group();
    root.getChildren().add(group);
    return group;
  }

  /**
   * Vykresli mapu z nactenych datovych souboru
   */
  public void drawMap() {
    streetMap = loadStreetMap();
    assert streetMap != null;
    streetMap.drawStreet(streetGroup);
    streetMap.drawCrossRoads(intersectionGroup, streetMap);
    streetMap.drawStops(stopGroup);
  }

  /**
   * Z formatu JSON nacte krizovatky,zastavky,ulice a informace o linkach autobusu
   * @return
   */
  private StreetMap loadStreetMap() {
    StreetMap streetMap = new StreetMap();
    try {
      loadIntersectionList(System.getProperty("user.dir") + "/dest/intersections.json", streetMap);
      loadStopList(System.getProperty("user.dir") + "/dest/stops.json", streetMap);
      loadStreets(System.getProperty("user.dir") + "/dest/streets.json", streetMap);
      loadBusLineList(System.getProperty("user.dir") + "/dest/lines.json", streetMap);
      return streetMap;
    } catch (IOException e) {
      System.out.println("Chyba vstupniho souboru ");
      e.printStackTrace();
    } catch (JSONException e) {
      System.out.println("Chyba JSON formatovani");
      e.printStackTrace();
    } catch (Exception e) {
      System.out.println("Vyskytla se chyba. Nepocedlo se vlozit buslines");
      System.out.println(e.getMessage());
    }
    return null;
  }

  /**
   * Ze souboru ve formatu JSON ulozi krizovatky do seznamu
   * @param filePath
   * @param streetMap
   * @throws JSONException
   * @throws IOException
   */
  private void loadIntersectionList(String filePath, StreetMap streetMap) throws JSONException, IOException {
    String jsonTxt = new String(Files.readAllBytes(Paths.get(filePath)));
    JSONArray jsonArray = new JSONArray(jsonTxt);
    for (int i = 0; i < jsonArray.length(); i++) {
      loadIntersection(jsonArray.getJSONObject(i), streetMap, i);
    }
  }

  /**
   * pomocna fce nacita a vytvari instance tridy Crossroad
   * @param jsonObject
   * @param streetMap
   * @param id
   * @throws JSONException
   */
  private void loadIntersection(JSONObject jsonObject, StreetMap streetMap, int id) throws JSONException {
    CrossRoad crossRoad = new CrossRoad(new Coordinate(jsonObject.getInt("x"), jsonObject.getInt("y")), id);
    streetMap.addToCrossRoadList(crossRoad);
  }

  /**
   * Pro kazdou linku v souboru v filePath vola loadBusLine
   * @param filePath
   * @param streetMap
   * @throws IOException
   * @throws JSONException
   */
  private void loadBusLineList(String filePath, StreetMap streetMap) throws IOException, JSONException {
    String jsonTxt = new String(Files.readAllBytes(Paths.get(filePath)));
    JSONArray jsonArray = new JSONArray(jsonTxt);
    for (int i = 0; i < jsonArray.length(); i++) {
      loadBusLine(jsonArray.getJSONObject(i), streetMap);
    }
  }

  /**
   * Linku v jsonObject predela na busline a ulozi do streetMap
   * @param jsonObject
   * @param streetMap
   * @throws JSONException
   * @throws IOException
   */
  private void loadBusLine(JSONObject jsonObject, StreetMap streetMap) throws JSONException, IOException {
    BusLine busLine = new BusLine(jsonObject.getInt("Name"), streetMap);
    JSONArray waypoints = jsonObject.getJSONArray("Waypoints");
    CrossRoad prevCrossRoad = streetMap.getCrossRoad(waypoints.getJSONObject(1).getInt("IdSection"));
    for (int i = 0; i < waypoints.length(); i++) {
      JSONObject waypoint = waypoints.getJSONObject(i);
      String stopName = waypoint.getString("StopName");
      if (!stopName.isEmpty()) {
        Stop stop = streetMap.getStop(stopName);
        if (stop == null) {
          throw new IOException();
        }
        busLine.addStop(stop, prevCrossRoad);
        busLine.addStopDepartureTime(stopName, waypoint.getInt("DepartTime"));
        prevCrossRoad = stop.getCrossRoad();
      } else {
        CrossRoad newCrossRoad = streetMap.getCrossRoad(waypoint.getInt("IdSection"));
        if (prevCrossRoad != null) {
          StreetSection streetSection = prevCrossRoad.getStreetSection(newCrossRoad);
          if (streetSection == null) {
            throw new IOException();
          } else {
            busLine.addStreetSection(streetSection);
          }
        }
        prevCrossRoad = newCrossRoad;
      }
    }
    int departureEvery = jsonObject.getInt("DepartureEvery");
    int departureStartOffset = jsonObject.getInt("DepartureStartOffset");
    for (int i = departureStartOffset; i < DAY_LENGTH; i = i + departureEvery) {
      busLine.addConnection(i);
    }
    streetMap.addToBusLineList(busLine);
  }

  /**
   * Pro kazdou zastavku v souboru v filePath vola loadStop
   * @param filePath
   * @param streetMap
   * @throws IOException
   * @throws JSONException
   */
  private void loadStopList(String filePath, StreetMap streetMap) throws IOException, JSONException {
    String jsonTxt = new String(Files.readAllBytes(Paths.get(filePath)));
    JSONArray jsonArray = new JSONArray(jsonTxt);
    for (int i = 0; i < jsonArray.length(); i++) {
      loadStop(jsonArray.getJSONObject(i), streetMap);
    }
  }

  /**
   * Zastavku v jsonObject predela na stop a ulozi do streetMap
   * @param jsonObject
   * @param streetMap
   * @throws JSONException
   */
  private void loadStop(JSONObject jsonObject, StreetMap streetMap) throws JSONException {
    String name = jsonObject.getString("Name");
    CrossRoad crossRoad = streetMap.getCrossRoad(jsonObject.getInt("IdIntersection"));
    Stop stop = new Stop(name, crossRoad);
    crossRoad.setStop(stop);
    streetMap.addToStopList(stop);
  }

  /**
   * Pro kazdou ulici v souboru v filePath vola loadStreet
   * @param filePath
   * @param streetMap
   * @throws IOException
   * @throws JSONException
   */
  private void loadStreets(String filePath, StreetMap streetMap) throws IOException, JSONException {
    String jsonTxt = new String(Files.readAllBytes(Paths.get(filePath)));
    JSONArray jsonArray = new JSONArray(jsonTxt);
    for (int i = 0; i < jsonArray.length(); i++) {
      streetMap.addStreet(loadStreet(jsonArray.getJSONObject(i), streetMap));
    }
  }

  /**
   * Ulici v jsonObject predela na street a ulozi do streetMap
   * @param jsonObject
   * @param streetMap
   * @return
   * @throws JSONException
   */
  private Street loadStreet(JSONObject jsonObject, StreetMap streetMap) throws JSONException {
    List<StreetSection> streetSections = new ArrayList<>();
    String streetName = jsonObject.getString("Name");
    JSONArray intersectionIds = jsonObject.getJSONArray("IntersectionIds");
    for (int i = 1; i < intersectionIds.length(); i++) {
      JSONObject intersection1 = intersectionIds.getJSONObject(i - 1);
      JSONObject intersection2 = intersectionIds.getJSONObject(i);
      int id1 = intersection1.getInt("Id");
      int id2 = intersection2.getInt("Id");
      streetSections.add(new StreetSection(streetMap.getCrossRoad(id1), streetMap.getCrossRoad(id2), streetName + "_" + i));
    }
    return new Street(streetName, streetSections);
  }

  /**
   * @return streetMap nactena ze souboru
   */
  public StreetMap getStreetMap() {
    return this.streetMap;
  }

  /**
   * @return group kde se pracuje s ikonami autobusu
   */
  public Group getBusGroup() {
    return busGroup;
  }
}
