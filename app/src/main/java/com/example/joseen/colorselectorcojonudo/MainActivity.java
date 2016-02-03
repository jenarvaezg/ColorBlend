package com.example.joseen.colorselectorcojonudo;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;

import java.util.Arrays;

public class MainActivity extends Activity {


    private static final int NSEEKBARS = 4;

    private Modes currentMode = Modes.RGB;

    private SeekBar[] seekBars = new SeekBar[NSEEKBARS];

    private Button rect;

    private enum Modes { RGB, HSV, YCBCR, CMYK }

    private int getRGBInt(int seekbar, int progress, int[] RGB){
        if(seekbar <= 2) {
            RGB[seekbar] = (int) ((float) progress / 100 * 255);
        }
        return android.graphics.Color.rgb(RGB[0], RGB[1], RGB[2]);
    }

    private int getHSVInt(int seekbar, int progress, int[] RGB) {
        float[] hsv = new float[3];
        Color.RGBToHSV(RGB[0], RGB[1], RGB[2], hsv);
        if (seekbar == 0) {
            hsv[seekbar] = (float) progress / 100.0f * 360.0f;
        } else if (seekbar <= 2) {
            hsv[seekbar] = (float) progress / 100.0f;
        }
        return Color.HSVToColor(hsv);
    }

    private int getYCbCrInt(int seekbar, int progress, int[] RGB){
        float r = RGB[0];
        float g = RGB[1];
        float b = RGB[2];
        if(seekbar > 2)
            return android.graphics.Color.rgb(RGB[0], RGB[1], RGB[2]);

        int[] YCbCr = new int[3];
        YCbCr[0] = (int)(0.299*r+0.587*g+0.114*b);
        YCbCr[1] = (int)(128-0.169*r-0.331*g+0.500*b);
        YCbCr[2] = (int)(128+0.500*r-0.419*g-0.081*b);
        YCbCr[seekbar] = (int) ((float) progress / 100 * 255);

        RGB[0] = ((int) ( 298.082 * (YCbCr[0] - 16)   +
                408.583 * (YCbCr[2] - 128)    )) >> 8;
        RGB[1] = ((int) ( 298.082 * (YCbCr[0] - 16)   +
                -100.291 * (YCbCr[1] - 128) +
                -208.120 * (YCbCr[2] - 128)    )) >> 8;
        RGB[2] = ((int) ( 298.082 * (YCbCr[0] - 16)   +
                516.411 * (YCbCr[1] - 128)    )) >> 8;

        for (int i=0; i<3; i++) {
            if (RGB[i] > 255)
                RGB[i] = 255;
            else if (RGB[i] < 0)
                RGB[i] = 0;
        }
        return android.graphics.Color.rgb(RGB[0], RGB[1], RGB[2]);
    }

    private void changeColor(Button rect, int seekbar, int progress){

        ColorDrawable drawable = (ColorDrawable) rect.getBackground();
        int colorBefore = drawable.getColor();
        int[] RGBBefore = { Color.red(colorBefore),
                            Color.green(colorBefore),
                Color.blue(colorBefore)};
        switch(currentMode){
            case RGB:
                rect.setBackgroundColor(getRGBInt(seekbar, progress, RGBBefore));
                break;
            case HSV:
                rect.setBackgroundColor(getHSVInt(seekbar, progress, RGBBefore));
                break;
            case YCBCR:
                rect.setBackgroundColor(getYCbCrInt(seekbar, progress, RGBBefore));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        rect = (Button) findViewById(R.id.rect);
        seekBars[0] = (SeekBar) findViewById(R.id.seek1);
        seekBars[1] = (SeekBar) findViewById(R.id.seek2);
        seekBars[2] = (SeekBar) findViewById(R.id.seek3);
        seekBars[3] = (SeekBar) findViewById(R.id.seek4);
        for(int i = 0; i < NSEEKBARS; i++){
            SeekBar seekBar = seekBars[i];
            final int number = i;
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    changeColor(rect, number, progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            seekBars[i] = seekBar;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_rgb:
                currentMode = Modes.RGB;
                break;
            case R.id.action_hsv:
                currentMode = Modes.HSV;
                break;
            case R.id.action_ycbcr:
                currentMode = Modes.YCBCR;
                break;
            case R.id.action_cmyk:
                currentMode = Modes.CMYK;
                break;
        }
        for(int i = 0; i < NSEEKBARS; i++){
            changeColor(rect, i, seekBars[i].getProgress());
        }

        return super.onOptionsItemSelected(item);
    }
}
