/**
 * Trida starajici se o cas
 * @author Matěj sojka (xsojka04)
 * @author Jan Hranický (xhrani02)
 *
 */
package src.managers;

import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.control.Button;
import src.icons.ClockIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeManager {
  public static final int DAY_LENGTH = 1440; //pocet minut ve dni
  public static final int START_TIME = DAY_LENGTH - 1;
  private int timeSpeed;
  private int currentTime;
  private final ClockIcon clockIcon;
  private boolean lastEven;
  private final Group root;

  /**
   * Konstruktor třídy - inicializuje tvorbu ikony hodin
   * @param root
   */
  public TimeManager(Group root) {
    this.currentTime = START_TIME;
    this.root = root;
    this.clockIcon = new ClockIcon(root);
    this.timeSpeed = 1;
  }

  /**
   * Nastavi this.timeSpeed na timeSpeed
   * @param timeSpeed
   */
  public void setTimeSpeed(int timeSpeed) {
    this.timeSpeed = timeSpeed;
  }

  /**
   * @return currentTime
   */
  public int getCurrentTime() {
    return currentTime;
  }

  /**
   * Vola setTime nad clockIcon
   */
  private void updateClock() {
    clockIcon.setTime(currentTimeString());
  }

  /**
   * @return retezec reprezentujici aktualni cas
   */
  private String currentTimeString() {
    return timeString(currentTime);
  }

  /**
   * Prevede time v minutach na 24 hodinovy format
   * @param time
   * @return retezec reprezentujici time
   */
  public static String timeString(int time) {
    String hours = Integer.toString(time / 60);
    String minutes = Integer.toString(time % 60);
    hours = hours.length() == 1 ? "0" + hours : hours;
    minutes = minutes.length() == 1 ? "0" + minutes : minutes;
    return hours + ":" + minutes;
  }

  /**
   * Vypocita novy aktualni cas a vrati list s minutami ktere ubehly
   * @return
   */
  public List<Integer> updateCurrentTimeAndClock() {
    List<Integer> minutesPassed = new ArrayList<>();
    int newTime = (currentTime + timeSpeed);
    for (int time = currentTime + 1; time <= newTime; time++) {
      minutesPassed.add(time % DAY_LENGTH);
    }
    currentTime = minutesPassed.get(minutesPassed.size() - 1);
    updateClock();
    return minutesPassed;
  }

  /**
   * Zapocne casovac a take vytvori tlacitko reset
   * @param busManager
   */
  public void startAnimation(BusManager busManager) {
    Button button = new Button("RESET");
    button.setLayoutX(100);
    button.setOnMouseClicked(event -> resetAnimation(busManager));
    this.root.getChildren().add(button);
    new AnimationTimer() {
      @Override
      public void handle(long now) {
        boolean even = TimeUnit.SECONDS.convert(now, TimeUnit.NANOSECONDS) % 2 == 0;

        if (lastEven != even) {
          lastEven = even;
          update(busManager);
        }
      }
    }.start();
  }

  /**
   * Navrati animaci do vychozi pozice
   * @param busManager
   */
  private void resetAnimation(BusManager busManager) {
    this.currentTime = START_TIME;
    busManager.reset();
  }

  /**
   * Aktualizuje pozice autobusu pomoci busManageru
   * @param busManager
   */
  private void update(BusManager busManager) {
    List<Integer> minutesPassed = this.updateCurrentTimeAndClock();
    for (int minute : minutesPassed) {
      busManager.departureBuses(minute);
    }
    busManager.moveBuses(this.getCurrentTime());
  }
}
