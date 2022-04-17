package com.example.pacman_hra;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.example.pacman_hra.game.Game;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    Game game;
    private GestureDetectorCompat gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        game = new Game(this);

        //game = findViewById(R.id.game);
        setContentView(R.layout.activity_main);

        //setContentView(game);
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout v = findViewById(R.id.layout);

        v.addView(game, 0);

        TextView ui = new TextView(this);
        ui.setTextSize(30);
        ui.setGravity(Gravity.CENTER);
        v.addView(ui, 1);

        Space space = new Space(this);
        space.setLayoutParams(new LinearLayout.LayoutParams(game.getWidth(), (int) (5 * getResources().getDisplayMetrics().density)));
        v.addView(space, 2);


        ToggleButton accelerometer = new ToggleButton(this);
        accelerometer.setOnCheckedChangeListener(this::accelerometerToggle);
        accelerometer.setText("Accelerometer is ON");
        accelerometer.setTextOn("Accelerometer is OFF");
        accelerometer.setTextOff("Accelerometer is ON");
        v.addView(accelerometer, 3);

        v.setBackgroundColor(Color.BLACK);

        UpdateThread thread = new UpdateThread(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ui.setText("Score: " + Game.pacman.score);
                ui.setTextColor(Color.WHITE);
                game.update();
            }
        });
        thread.start();

        gestureDetector = new GestureDetectorCompat(this, new SwipeListener());
        //v.setOnTouchListener(this);
        game.setOnTouchListener(this);
    }

    private void accelerometerToggle(CompoundButton compoundButton, boolean b) {
        boolean paused = game.isPaused;
        if (b) {
            game.onPause();
        } else {
            game.onResume();
        }
        game.isPaused = paused;
    }

    protected void onResume() { // registracia pre zber udajov zo senzora
        super.onResume();
        game.onResume();
    }

    protected void onPause() { // ukoncenie zberu udajov zo senzora
        super.onPause();
        game.onPause();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //Game.pacman.changeDirection(motionEvent);
        return gestureDetector.onTouchEvent(motionEvent);
    }
}