package com.example.pacman_hra;

import android.os.Handler;

public class UpdateThread extends Thread {

    private final Handler handler;

    private final static long timeout = 1000/60;

    public UpdateThread(Handler handler) {
        super();
        this.handler = handler;
    }

    @Override
    public void run() {
        while (true) {
            try {
                sleep(this.timeout);
            } catch (Exception ignored) { }
            handler.sendEmptyMessage(0);
        }
    }
}
