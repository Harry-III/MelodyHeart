package com.hc.testheart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Harry 2017/5/17
 */
public class HeartView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder surfaceHolder;
    private int offsetX;
    private int offsetY;
    private Garden garden;
    private int width;
    private int height;
    private Paint backgroundPaint;
    private static boolean isDrawing = false;
    private Bitmap bm;
    private Canvas canvas;
    private int heartRadio = 1;
    private ExecutorService singleThread;
    private ArrayList<Bloom> blooms = new ArrayList<>();  //花瓣
    private static float angle = 10;

    public HeartView(Context context) {
        this(context, null);
    }

    public HeartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_UP) {
            reDraw();
        }
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        angle = 10;
        singleThread.shutdown(); //当时视图消失的时候，关闭线程
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.width = width;
        this.height = height;
        //我的手机宽度像素是1080，发现参数设置为30比较合适，这里根据不同的宽度动态调整参数
        heartRadio = width * 30 / 1080;

        offsetX = width / 2;
        offsetY = height / 2 - 100;
        bm = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        canvas = new Canvas(bm);

        drawOnSingleThread();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void init() {
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);   //界面添加回调方法
        garden = new Garden();
        backgroundPaint = new Paint();
//        backgroundPaint.setColor(Color.rgb(0xff, 0xff, 0xe0));
        backgroundPaint.setColor(Color.WHITE);   //设置画布的背景

        singleThread = Executors.newSingleThreadExecutor();
    }

    public void reDraw() {
        if (singleThread.isShutdown()) {
            singleThread.shutdownNow();
        }
        if (angle >= 30) {
            angle = 10;
            blooms.clear();
            drawOnSingleThread();
        } else {
            if (isDrawing) {
                isDrawing = false;
            } else {
                drawOnSingleThread();
            }
        }

    }

    private void drawOnSingleThread() {       //在单例的线程池中使用线程
        singleThread.execute(new Runnable() {
            @Override
            public void run() {
                if (isDrawing) return;
                isDrawing = true;
                Log.d("asdf", "current Thread:" + Thread.currentThread());

                while (isDrawing) {
                    Bloom bloom = getBloom(angle);
                    if (bloom != null) {
                        blooms.add(bloom);
                    }
                    if (angle >= 30) {
                        isDrawing = false;
                        break;
                    } else {
                        angle += 0.2;
                    }
                    drawHeart();
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
//                isDrawing = false;
            }
        });

    }

    private void drawHeart() {
        canvas.drawRect(0, 0, width, height, backgroundPaint); //每个花朵的背景
        for (Bloom b : blooms) {
            b.draw(canvas);
        }
        Canvas c = surfaceHolder.lockCanvas();

        c.drawBitmap(bm, 0, 0, null);

        surfaceHolder.unlockCanvasAndPost(c);

    }

    private Bloom getBloom(float angle) {

        Point p = getHeartPoint(angle);

        boolean draw = true;
        /**循环比较新的坐标位置是否可以创建花朵,
         * 为了防止花朵太密集
         * */
        for (int i = 0; i < blooms.size(); i++) {

            Bloom b = blooms.get(i);
            Point bp = b.getPoint();
            float distance = (float) Math.sqrt(Math.pow(p.x - bp.x, 2) + Math.pow(p.y - bp.y, 2));
            if (distance < Garden.Options.maxBloomRadius * 1.5) {
                draw = false;
                break;
            }
        }
        //如果位置间距满足要求，就在该位置创建花朵并将花朵放入列表
        if (draw) {
            Bloom bloom = garden.createRandomBloom(p.x, p.y);
            return bloom;
        }
        return null;
    }

    public Point getHeartPoint(float angle) {
        float t = (float) (angle / Math.PI);
        float x = (float) (heartRadio * (16 * Math.pow(Math.sin(t), 3)));
        float y = (float) (-heartRadio * (13 * Math.cos(t) - 5 * Math.cos(2 * t) - 2 * Math.cos(3 * t) - Math.cos(4 * t)));

        return new Point(offsetX + (int) x, offsetY + (int) y);
    }
}