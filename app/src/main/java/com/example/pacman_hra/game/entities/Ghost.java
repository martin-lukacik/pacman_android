package com.example.pacman_hra.game.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.example.pacman_hra.R;
import com.example.pacman_hra.game.Game;
import com.example.pacman_hra.game.enums.Direction;
import com.example.pacman_hra.game.enums.GhostState;
import com.example.pacman_hra.game.path.Dijkstra;
import com.example.pacman_hra.game.path.Node;

import java.util.ArrayList;

public class Ghost extends Entity {

    String name;

    final int stepsAhead;

    public GhostState state;

    private int stepsCompleted = 0;

    long spawnTime = System.currentTimeMillis();
    private long lastStateChange = System.currentTimeMillis();
    private Dijkstra dijkstra = null; // generuje kroky nepriatelov
    private Thread dijkstraThread = null;
    private ArrayList<Node> currentPath = null;
    private ArrayList<Node> nextPath = null;

    static Bitmap fleeSprite = null;
    static Bitmap deadSprite = null;

    int spawn_x, spawn_y;
    int corner_x, corner_y;

    int delay = 0;

    boolean fleeLock = false; // 1/2 step

    public Ghost(String name, int x, int y, int stepsAhead) {
        super(x, y);
        this.name = name;
        this.stepsAhead = stepsAhead;

        if (fleeSprite == null) {
            fleeSprite = BitmapFactory.decodeResource(Game.context.getResources(), R.drawable.flee);
        }
        if (deadSprite == null) {
            deadSprite = BitmapFactory.decodeResource(Game.context.getResources(), R.drawable.dead);
        }

        switch (name) {
            case "blinky":
                this.sprite = BitmapFactory.decodeResource(Game.context.getResources(), R.drawable.blinky);
                break;
            case "clyde":
                this.sprite = BitmapFactory.decodeResource(Game.context.getResources(), R.drawable.clyde);
                break;
            case "inky":
                this.sprite = BitmapFactory.decodeResource(Game.context.getResources(), R.drawable.inky);
                break;
            case "pinky":
                this.sprite = BitmapFactory.decodeResource(Game.context.getResources(), R.drawable.pinky);
                break;
        }

        setSprite(sprite);

        setState(GhostState.Patrol);
        spawn_x = x;
        spawn_y = y;
        delay = getDelay();
    }

    private int getDelay() {
        int mapY = Game.map.size();
        int mapX = Game.map.get(0).size();
        switch (name) {
            case "blinky": {
                corner_x = 1; corner_y = 1;
                return 0;
            }
            case "pinky": {
                corner_x = 1; corner_y = mapY - 2;
                return 2500;
            }
            case "inky": {
                corner_x = mapX - 2; corner_y = 1;
                return 5000;
            }
            default: {
                corner_x = mapX - 2; corner_y = mapY - 2;
                return 10000;
            }
        }
    }

    @Override
    public void draw(Canvas canvas, Paint paint, float scale) {
        RectF matrix = new RectF(x * scale, y * scale, x * scale + scale, y * scale + scale);
        canvas.drawBitmap(this.getSprite(), null, matrix, paint);
    }

    @Override
    protected void move() {

        if (state == GhostState.Flee) {
            if (fleeLock) {
                fleeLock = false;
                return;
            } else {
                fleeLock = true;
            }
        }

        if (delay > 0 && spawnTime + delay > System.currentTimeMillis())
            return;

        delay = 0;

        calculateMove();
        makeMove();
    }

    int step = 0;
    @Override
    protected void animate() {
        if (_source == null)
            return;

        int look = 1;
        switch (direction) {
            case Down:
                look = 2;
                break;
            case Left:
                look = 0;
                break;
            case Up:
                look = 3;
                break;
        }

        Bitmap src = this._source;

        if (state == GhostState.Flee) {
            look = 0;
            src = fleeSprite;
        } else if (state == GhostState.Dead) {
            src = deadSprite;
        }

        step = (step + 1) % 2;
        int width = src.getWidth()/4;
        int height = src.getHeight()/2;
        this.sprite = Bitmap.createBitmap(src, look * width, step * height, width, height);
        ////this.sprite = Bitmap.createBitmap(this._source, step * width, 0, width, this._source.getHeight());
    }


    private void calculateMove() {
        if (dijkstraThread == null && nextPath == null)
        {
            runThread();
        }
        else if (dijkstraThread != null && !dijkstraThread.isAlive())
        {
            dijkstraThread.interrupt();
            dijkstraThread = null;
        }
    }



    public void setState(GhostState newState)
    {
        state = newState;
        lastStateChange = System.currentTimeMillis();

        if (newState == GhostState.Flee || newState == GhostState.Dead) {
            if (newState == GhostState.Flee)
                /* set flee sprite */;
            else // po zjedeni zostanu len oci
                /* set dead sprite */;
        }
        else if (newState == GhostState.Chase || newState == GhostState.Patrol)
        {
            /* set default sprite */;
        }
        resetPath();
    }

    private void makeMove()
    {
        // change state
        if ((state != GhostState.Dead && state != GhostState.Flee))
        {
            final int changeStateTimeout = 20000;
            if (lastStateChange + changeStateTimeout < System.currentTimeMillis())
            {
                if (state == GhostState.Patrol)
                    setState(GhostState.Chase);
                else
                    setState(GhostState.Patrol);
            }

        }
        else if (currentPath != null && (stepsCompleted >= currentPath.size() - 1))
        {
            setState(GhostState.Patrol); // po navrate na zaciatok
        }

        if (state == GhostState.Patrol)
        {
            double pacmanDistance = Math.sqrt(Math.pow(Game.pacman.x - x, 2) + Math.pow(Game.pacman.y - y, 2));

            if (pacmanDistance < 5) // zabrani tomu aby duch prestal prenasledovat hraca ak je blizko
            {
                setState(GhostState.Chase);
            }
        }


        if (currentPath == null || stepsCompleted >= stepsAhead || currentPath.size() - 1 <= stepsCompleted) {
            if (nextPath != null) {

                currentPath = nextPath;
                stepsCompleted = 0;
                nextPath = null;
            }
            else return;
        }

        Node step = currentPath.get(stepsCompleted);

        if (step.x < x)      direction = Direction.Left;
        else if (step.x > x) direction = Direction.Right;
        else if (step.y > y) direction = Direction.Down;
        else if (step.y < y) direction = Direction.Up;

        setPosition(step.x, step.y);

        ++stepsCompleted;


        if (x == Game.pacman.x && y == Game.pacman.y)
        {
            if (state == GhostState.Flee)
            {
                setState(GhostState.Dead);
            }
            else if (state != GhostState.Dead)
            {
                // kill pac
                //game.pacman.kill(true);
            }
        }
    }

    private void resetPath()
    {
        currentPath = nextPath = null;
        stepsCompleted = 0;
    }

    private void runThread()
    {
        if (dijkstra == null)
            dijkstra = new Dijkstra(Game.map);

        int target_x, target_y;
        target_x = Game.pacman.x;
        target_y = Game.pacman.y;
        switch (state) {
            case Patrol:
                target_x = corner_x;
                target_y = corner_y;
                if (x == corner_x && y == corner_y)
                    setState(GhostState.Chase);
                break;
            case Dead:
            case Flee: // po zjedeni pac manom alebo uteku ide do stredu bludiska
                target_x = spawn_x;
                target_y = spawn_y;
                if (x == target_x && y == target_y)
                    setState(GhostState.Patrol);
                break;
        }

        int finalTarget_x = target_x;
        int finalTarget_y = target_y;

        dijkstraThread = new Thread(() -> {
            if (currentPath != null) {
                Node node = currentPath.get(Math.min(stepsAhead, currentPath.size() - 1));
                dijkstra.run(finalTarget_x, finalTarget_y, node.x, node.y);
            }
            else {
                dijkstra.run(finalTarget_x, finalTarget_y, x, y);
            }
            nextPath = dijkstra.getOutput();
        });

        dijkstraThread.start();
    }

}
