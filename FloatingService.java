
package com.freefire.macro;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class FloatingService extends Service {

    private WindowManager windowManager;
    private View floatingView;
    private boolean isLocked = false;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget, null);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 200;
        params.y = 300;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(floatingView, params);

        final ImageView redCircle = floatingView.findViewById(R.id.red_circle);
        final ImageView lockBtn = floatingView.findViewById(R.id.lock_btn);
        final ImageView closeBtn = floatingView.findViewById(R.id.close_btn);

        GradientDrawable circleDrawable = new GradientDrawable();
        circleDrawable.setShape(GradientDrawable.OVAL);
        circleDrawable.setColor(0xFFD1001F); // Center color
        circleDrawable.setStroke(6, 0xFFFF0000); // Border red
        redCircle.setBackground(circleDrawable);

        redCircle.setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY;
            private float initialTouchX, initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isLocked) return true;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(floatingView, params);
                        return true;
                }
                return false;
            }
        });

        lockBtn.setOnClickListener(v -> {
            isLocked = !isLocked;
            Toast.makeText(this, isLocked ? "Locked" : "Unlocked", Toast.LENGTH_SHORT).show();
        });

        closeBtn.setOnClickListener(v -> stopSelf());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null) windowManager.removeView(floatingView);
    }
}
