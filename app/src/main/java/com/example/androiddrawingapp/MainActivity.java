package com.example.androiddrawingapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private DrawingCanvas mDrawingCanvas;
    private Button mButton1;
    private Button mButton2;
    private Button mButton3;
    private Button mButton4;
    private Button mButtonClear;

    private int mSelectedColor = Color.BLACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawingCanvas = findViewById(R.id.powierzchnia_rysunku);
        mButton1 = findViewById(R.id.c1);
        mButton2 = findViewById(R.id.c2);
        mButton3 = findViewById(R.id.c3);
        mButton4 = findViewById(R.id.c4);
        mButtonClear = findViewById(R.id.X);

        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedColor = Color.RED;
                mDrawingCanvas.setSelectedColor(mSelectedColor);
            }
        });

        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedColor = Color.parseColor("#FFA500");
                mDrawingCanvas.setSelectedColor(mSelectedColor);
            }
        });

        mButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedColor = Color.BLUE;
                mDrawingCanvas.setSelectedColor(mSelectedColor);
            }
        });

        mButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedColor = Color.GREEN;
                mDrawingCanvas.setSelectedColor(mSelectedColor);
            }
        });

        mButtonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawingCanvas.clearCanvas();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDrawingCanvas.resumeDrawing();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDrawingCanvas.pauseDrawing();
    }
}
