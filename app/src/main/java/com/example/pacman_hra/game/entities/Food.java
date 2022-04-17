package com.example.pacman_hra.game.entities;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Food extends Entity {

    public boolean isLarge;

    public Food(int x, int y, boolean isLarge) {
        super(x, y);

        this.isLarge = isLarge;

        this.color = 0xfff5f5dc;
    }

    @Override
    public void draw(Canvas canvas, Paint paint, float scale) {

        float radius = scale / 6f;
        if (isLarge) {
            radius = scale / 3f;
        }
        paint.setColor(color);
        canvas.drawCircle(x * scale + scale / 2, y * scale + scale / 2, radius, paint);
    }

    @Override
    protected void move() {

    }

    @Override
    protected void animate() {

    }
}
