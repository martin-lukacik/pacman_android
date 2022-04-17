package com.example.pacman_hra.game.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.example.pacman_hra.R;
import com.example.pacman_hra.game.Game;

public class BonusFood extends Food {

    static Bitmap foodSprite = null;

    public static final short spawnRateMillis = 10000;
    public static long lastSpawned = 0;

    public BonusFood(int x, int y) {
        super(x, y, false);

        if (foodSprite == null) {
            lastSpawned = System.currentTimeMillis();
            foodSprite = BitmapFactory.decodeResource(Game.context.getResources(), R.drawable.food);
        }
    }

    @Override
    public void draw(Canvas canvas, Paint paint, float scale) {
        RectF matrix = new RectF(x * scale, y * scale, x * scale + scale, y * scale + scale);
        canvas.drawBitmap(foodSprite, null, matrix, paint);
    }
}
