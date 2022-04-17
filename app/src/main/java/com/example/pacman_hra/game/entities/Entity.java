package com.example.pacman_hra.game.entities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.pacman_hra.game.Game;
import com.example.pacman_hra.game.enums.Direction;

import java.util.ArrayList;

public abstract class Entity {

    Bitmap _source = null;
    Bitmap sprite = null;
    int color = Color.TRANSPARENT;

    float size = 10;

    public int x, y;

    public Entity() {
        this(-1, -1);
    }

    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    Direction direction = Direction.Right;
    Direction nextDirection = Direction.Right;

    private long lastMove = 0;
    private long lastFrame = 0;
    private long lastUpdate = System.currentTimeMillis();

    protected abstract void draw(Canvas canvas, Paint paint, float scale);
    protected abstract void move();
    protected abstract void animate();

    public void update() {
        final long currentTime = System.currentTimeMillis();
        final int deltaT = (int)(currentTime - lastUpdate);

        lastMove += deltaT;
        lastFrame += deltaT;

        final int timeBetweenMoves = 300;
        if (lastMove > timeBetweenMoves) {
            lastMove = 0;
            move();
        }

        final int timeBetweenFrames = 100;
        if (lastFrame > timeBetweenFrames) {
            lastFrame = 0;
            animate();
        }

        lastUpdate = currentTime;
    }

    protected int isValidMove(double offsetX, double offsetY) {
        final int tmp_x = (int)(x + offsetX);
        final int tmp_y = (int)(y + offsetY);

        ArrayList<ArrayList<Integer>> map = Game.map;
        if (tmp_y < 0 || tmp_y >= map.size() || tmp_x < 0 || tmp_x >= map.get(tmp_y).size())
            return 0;

        return map.get(tmp_y).get(tmp_x) != 0 ? 1 : 0;
    }

    public Context getContext() {
        return Game.context;
    }

    public void setSprite(Bitmap sprite) {
        this._source = sprite;
        this.sprite = this._source;
    }

    public Bitmap getSprite() {
        return this.sprite;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }

    public void setPosition(int x, int y) {
        //if (x != -1)
            this.x = x;

        //if (y != -1)
            this.y = y;
    }

    public int[] getPosition() {
        return new int[] { this.x, this.y };
    }

    public void setSize(float value) {
        this.size = value;
    }

    public float getSize() {
        return this.size;
    }
}
