package com.example.pacman_hra.game.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.example.pacman_hra.R;
import com.example.pacman_hra.game.Game;
import com.example.pacman_hra.game.enums.Direction;

import java.util.ArrayList;

public class Pacman extends Entity {

    public int score = 0;

    public int spawn_x, spawn_y;

    public Pacman(int x, int y) {
        super(x, y);

        Bitmap sprite = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.pac);

        spawn_x = x;
        spawn_y = y;

        setSprite(sprite);
    }

    public boolean changeDirection(Direction direction) {

        boolean doNow = true;

        if (direction == Direction.Down) {
            if (isValidMove(0, 1) != 1) {
                doNow = false;
            }
        } else if (direction == Direction.Up) {
            if (isValidMove(0, -1) != 1) {
                doNow = false;
            }
        } else if (direction == Direction.Left) {
            if (isValidMove(-1, 0) != 1) {
                doNow = false;
            }
        } else {
            if (isValidMove(1, 0) != 1) {
                doNow = false;
            }
        }

        if (doNow) {
            this.direction = direction;
            nextDirection = direction;
        } else {
            nextDirection = direction;
        }

        return false;
    }

    @Override
    public void draw(Canvas canvas, Paint paint, float scale) {
        RectF matrix = new RectF(x * scale, y * scale, x * scale + scale, y * scale + scale);
        canvas.drawBitmap(this.getSprite(), null, matrix, paint);
    }

    @Override
    protected void move() {

        // use if possible
        changeDirection(nextDirection);

        ArrayList<ArrayList<Integer>> map = Game.map;

        // tunel
        if (y == map.size() / 2 - 1)
        {
            if (x == 0)
                x = map.get(y).size() - 1;
            else if (x == map.get(y).size() - 1)
                x = 0;
        }

        switch (direction) {
            case Up:
                if (isValidMove(0, -1) == 1)
                    setPosition(x, y - 1);
                else direction = nextDirection;
                break;
            case Down:
                if (isValidMove(0, 1) == 1)
                    setPosition(x, y + 1);
                else direction = nextDirection;
                break;
            case Left:
                if (isValidMove(-1, 0) == 1)
                    setPosition(x - 1, y);
                else direction = nextDirection;
                break;
            case Right:
                if (isValidMove(1, 0) == 1)
                    setPosition(x + 1, y);
                else direction = nextDirection;
                break;
        }
    }

    int step = 0;
    @Override
    protected void animate() {
        step = (step + 1) % 3;
        int width = _source.getWidth()/3;

        Matrix matrix = new Matrix();

        switch (direction) {
            case Down:
                matrix.postRotate(90);
                break;
            case Left:
                matrix.postRotate(180);
                break;
            case Up:
                matrix.postRotate(270);
                break;
        }

        this.sprite = Bitmap.createBitmap(this._source, step * width, 0, width, this._source.getHeight(), matrix, true);
    }
}
