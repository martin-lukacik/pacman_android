package com.example.pacman_hra;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.example.pacman_hra.game.Game;
import com.example.pacman_hra.game.enums.Direction;

public class SwipeListener extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
   /* @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH){
                return false;
            }
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                Game.pacman.direction = Direction.Left;
            }
            // left to right swipe
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                Game.pacman.direction = Direction.Right;
            }
            else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                Game.pacman.direction = Direction.Up;
            } else Game.pacman.direction = Direction.Down;
        } catch (Exception e) {

        }
        return false;
    }*/

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        float x1 = e1.getX();
        float y1 = e1.getY();

        float x2 = e2.getX();
        float y2 = e2.getY();

        Direction direction = getDirection(x1,y1,x2,y2);
        return onSwipe(direction);
    }

    public boolean onSwipe(Direction direction){

        Game.pacman.changeDirection(direction);

        return false;
    }

    public Direction getDirection(float x1, float y1, float x2, float y2){
        double angle = getAngle(x1, y1, x2, y2);
        return fromAngle(angle);
    }

    public double getAngle(float x1, float y1, float x2, float y2) {
        double rad = Math.atan2(y1-y2,x2-x1) + Math.PI;
        return (rad*180/Math.PI + 180)%360;
    }



    public static Direction fromAngle(double angle){
        if(inRange(angle, 45, 135)){
            return Direction.Up;
        }
        else if(inRange(angle, 0,45) || inRange(angle, 315, 360)){
            return Direction.Right;
        }
        else if(inRange(angle, 225, 315)){
            return Direction.Down;
        }
        else{
            return Direction.Left;
        }

    }
    private static boolean inRange(double angle, float init, float end){
        return (angle >= init) && (angle < end);
    }
}
