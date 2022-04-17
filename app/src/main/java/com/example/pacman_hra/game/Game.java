package com.example.pacman_hra.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pacman_hra.game.entities.BonusFood;
import com.example.pacman_hra.game.entities.Food;
import com.example.pacman_hra.game.entities.Ghost;
import com.example.pacman_hra.game.entities.Pacman;
import com.example.pacman_hra.game.enums.Direction;
import com.example.pacman_hra.game.enums.GhostState;

import java.util.ArrayList;

public class Game extends View implements SensorEventListener {

    public static Context context;
    private final SensorManager sManager;
    private final Sensor accelerometer;
    Paint paint;

    private float squareSize;

    public static ArrayList<ArrayList<Integer>> map;
    ArrayList<Ghost> ghosts = new ArrayList<>();
    ArrayList<Food> food = new ArrayList<>();
    public static Pacman pacman;
    BonusFood bonusFood = null;

    TextView ui = null;

    public Game(Context context) {
        super(context);
        this.paint = new Paint();

        if (Game.context == null) {
            Game.context = context;
        }

        // Sensors
        sManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE); // zisk managera
        accelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        reset("level1");
        setLongClickable(true);
    }

    void reset(String level) {

        BonusFood.lastSpawned = System.currentTimeMillis();

        map = MapLoader.loadMap(level + ".txt");
        squareSize = getWidth() / (float) map.get(0).size();

        ghosts = new ArrayList<>();
        food = new ArrayList<>();

        pacman = new Pacman(14, 23);

        ghosts.add(new Ghost("clyde", 15, 14, 100));
        ghosts.add(new Ghost("blinky", 14, 14, 10));
        ghosts.add(new Ghost("pinky", 13, 14, 25));
        ghosts.add(new Ghost("inky", 12, 14, 50));

        for (int y = 0; y < map.size(); ++y) {
            for (int x = 0; x < map.get(y).size(); ++x) {
                if (map.get(y).get(x) == 2) {
                    Food f = new Food(x, y, false);
                    food.add(f);
                } else if (map.get(y).get(x) == 3) {
                    Food f = new Food(x, y, true);
                    food.add(f);
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        squareSize = getWidth() / (float) map.get(0).size();
        setLayoutParams(new LinearLayout.LayoutParams((int) (map.get(0).size() * squareSize), (int) (map.size() * squareSize)));

        for (int y = 0; y < map.size(); ++y) {
            for (int x = 0; x < map.get(y).size(); ++x) {
                if (map.get(y).get(x) == 0)
                    paint.setColor(Color.BLACK);
                else
                    paint.setColor(0xff191970);
                float _x = x * squareSize;
                float _y = y * squareSize;
                canvas.drawRect(_x, _y, _x + squareSize, _y + squareSize, paint);
            }
        }

        for (int i = 0; i < food.size(); ++i) {
            Food f = food.get(i);
            f.draw(canvas, paint, squareSize);
        }

        for (int i = 0; i < ghosts.size(); ++i) {
            Ghost g = ghosts.get(i);
            g.draw(canvas, paint, squareSize);
        }

        if (bonusFood != null)
            bonusFood.draw(canvas, paint, squareSize);

        pacman.draw(canvas, paint, squareSize);
    }

    public void onResume() {
        isPaused = false;
        sManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    public void onPause() { // ukoncenie zberu udajov zo senzora
        isPaused = true;
        sManager.unregisterListener(this);
    }

    long lastSensorUpdate = System.currentTimeMillis();
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (lastSensorUpdate > System.currentTimeMillis() - 100)
            return;
        lastSensorUpdate = System.currentTimeMillis();

        final float sensitivity = 3;

        float[] p = sensorEvent.values;
        if (p[0] < 0 - sensitivity) pacman.changeDirection(Direction.Right);
        if (p[0] > 0 + sensitivity) pacman.changeDirection(Direction.Left);
        if (p[1] < 0 - sensitivity) pacman.changeDirection(Direction.Up);
        if (p[1] > 0 + sensitivity) pacman.changeDirection(Direction.Down);

        //System.out.printf("%.03f\t%.03f\t%.03f\n", p[0], p[1], p[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public boolean isPaused = false;
    long lastUpdate = System.currentTimeMillis();
    public void update() {

        long currentTime = System.currentTimeMillis();
        if (lastUpdate == 0 && !isPaused)
            lastUpdate = currentTime;

        long deltaT = currentTime - lastUpdate;

        if (isPaused)
            return;

        this.invalidate();


        pacman.update();

        if (BonusFood.lastSpawned + BonusFood.spawnRateMillis < currentTime && (pacman.x != pacman.spawn_x || pacman.y != pacman.spawn_y)) {
            if (bonusFood != null) {
                // expired
                bonusFood = null;
            } else {
                bonusFood = new BonusFood(pacman.spawn_x, pacman.spawn_y);
            }
            BonusFood.lastSpawned = currentTime;
        }

        for (int i = ghosts.size() - 1; i >= 0; --i) {
            Ghost g = ghosts.get(i);
            g.update();

            if (pacman.x == g.x && pacman.y == g.y) {
                if (g.state == GhostState.Flee) {
                    g.setState(GhostState.Dead);
                    pacman.score += 200;
                } else if (g.state != GhostState.Dead){
                    // kill pacman
                    reset("level1");
                }
            }
        }

        // food
        for (int i = food.size() - 1; i >= 0; --i) {
            Food f = food.get(i);
            if (f.x == pacman.x && f.y == pacman.y) {

                pacman.score += 10;

                if (f.isLarge) {
                    for (Ghost g : ghosts) {
                        g.setState(GhostState.Flee);
                    }

                    pacman.score += 40;
                }

                // bonus food 10 + 40

                food.remove(f); // eat food
            }
        }

        // bonus food
        if (bonusFood != null && pacman.x == bonusFood.x && pacman.y == bonusFood.y) {
            bonusFood = null;
            BonusFood.lastSpawned = currentTime;

            pacman.score += 100;
        }

        if (food.size() == 0) {
            reset("level2"); // win
        }
    }
}
