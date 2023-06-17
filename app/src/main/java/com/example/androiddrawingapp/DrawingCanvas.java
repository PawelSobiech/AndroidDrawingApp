package com.example.androiddrawingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class DrawingCanvas extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder mHolder;
    private Thread mDrawingThread;
    private boolean mThreadRunning = false;
    private Object mLock = new Object();
    private List<Path> mPaths;
    private List<Integer> mPathColors;
    private Paint mPaint;
    private int mSelectedColor = Color.RED;

    public DrawingCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mPaths = new ArrayList<>();
        mPathColors = new ArrayList<>();
        mPaint = new Paint();
        mPaint.setColor(mSelectedColor);
        mPaint.setStrokeWidth(3);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    public void resumeDrawing() {
        startDrawingThread();
    }

    public void pauseDrawing() {
        mThreadRunning = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        float x = event.getX();
        float y = event.getY();

        synchronized (mLock) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Path path = new Path();
                    path.moveTo(x, y);
                    mPaths.add(path);
                    mPathColors.add(mSelectedColor);
                    break;
                case MotionEvent.ACTION_MOVE:
                    Path currentPath = getCurrentPath();
                    currentPath.lineTo(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
        }
        return true;
    }

    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public void run() {
        while (mThreadRunning) {
            Canvas canvas = null;
            try {
                synchronized (mHolder) {
                    if (!mHolder.getSurface().isValid())
                        continue;

                    canvas = mHolder.lockCanvas(null);

                    synchronized (mLock) {
                        if (mThreadRunning) {
                            drawPaths(canvas);
                        }
                    }
                }
            } finally {
                if (canvas != null) {
                    mHolder.unlockCanvasAndPost(canvas);
                }
            }
            try {
                Thread.sleep(1000 / 25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void drawPaths(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        synchronized (mLock) {
            for (int i = 0; i < mPaths.size(); i++) {
                Path path = mPaths.get(i);
                int color = mPathColors.get(i);
                mPaint.setColor(color);
                paint.setColor(color);
                paint.setStyle(Paint.Style.FILL);

                PointF startPoint = getStartPoint(path);
                if (startPoint != null) {
                    canvas.drawCircle(startPoint.x, startPoint.y, 8, paint);
                }

                canvas.drawPath(path, mPaint);

                PointF lastPoint = getLastPoint(path);
                if (lastPoint != null) {
                    canvas.drawCircle(lastPoint.x, lastPoint.y, 8, paint);
                }
            }
        }
    }

    public void clearCanvas() {
        synchronized (mLock) {
            mPaths.clear();
            mPathColors.clear();
        }
    }

    private void startDrawingThread() {
        mDrawingThread = new Thread(this);
        mThreadRunning = true;
        mDrawingThread.start();
    }

    private Path getCurrentPath() {
        int size = mPaths.size();
        if (size > 0) {
            return mPaths.get(size - 1);
        }
        return null;
    }

    public void setSelectedColor(int color) {
        mSelectedColor = color;
        mPaint.setColor(mSelectedColor);
    }

    private PointF getLastPoint(Path path) {
        if (path != null) {
            float[] coords = new float[2];
            PathMeasure pathMeasure = new PathMeasure(path, false);
            pathMeasure.getPosTan(pathMeasure.getLength(), coords, null);
            return new PointF(coords[0], coords[1]);
        }
        return null;
    }

    private PointF getStartPoint(Path path) {
        if (path != null) {
            float[] coords = new float[2];
            PathMeasure pathMeasure = new PathMeasure(path, false);
            pathMeasure.getPosTan(0, coords, null);
            return new PointF(coords[0], coords[1]);
        }
        return null;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startDrawingThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        synchronized (mLock) {
            for (Path path : mPaths) {
                path.offset(width / 2, height / 2);
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mThreadRunning = false;
    }
}