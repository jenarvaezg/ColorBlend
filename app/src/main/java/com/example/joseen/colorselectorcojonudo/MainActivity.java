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
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;

import java.security.Key;
import java.util.Arrays;

public class MainActivity extends Activity {


    private static final int NELEMS = 4;

    private Modes currentMode = Modes.RGB;

    private SeekBar[] seekBars = new SeekBar[NELEMS];
    private TextView[] textViews = new TextView[NELEMS];
    private LinearLayout[] linearLayouts = new LinearLayout[NELEMS];
    private EditText[] editTexts = new EditText[NELEMS];

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

    private int[] getButtonRGB(Button rect){
        ColorDrawable drawable = (ColorDrawable) rect.getBackground();
        int colorBefore = drawable.getColor();
        int[] RGB = { Color.red(colorBefore),
                Color.green(colorBefore),
                Color.blue(colorBefore)};
        return RGB;
    }

    private void changeColor(Button rect, int pos, int progress){

        int[] RGB = getButtonRGB(rect);
        switch(currentMode){
            case RGB:
                rect.setBackgroundColor(getRGBInt(pos, progress, RGB));
                break;
            case HSV:
                rect.setBackgroundColor(getHSVInt(pos, progress, RGB));
                break;
            case YCBCR:
                rect.setBackgroundColor(getYCbCrInt(pos, progress, RGB));
        }

    }

    private void changeColorFromEditText(Button rect, int pos){
        changeColor(rect, pos, getProgressFromEditText(editTexts[pos], pos));
    }

    private int getProgressFromEditText(EditText e, int pos){
        float f = Float.parseFloat(e.getText().toString());
        int progress = 0;
        switch(currentMode){
            case RGB:
            case YCBCR:
                if(f > 255)
                    f = 255;
                progress = (int) (f / 255 * 100);
                e.setText(Integer.toString((int)f));
                break;
            case HSV:
                if(pos == 0){
                    if(f > 360)
                        f = 360;
                    progress = (int) (f / 360 * 100);

                }else{
                    if(f > 1)
                        f = 1;
                    progress = (int) (f * 100);
                }
                e.setText(Float.toString(f));
                break;
            case CMYK:
                if(f > 1)
                    f = 1;
                progress = (int) (f * 100);
                e.setText(Float.toString(f));
                break;
        }
        return progress;
    }

    private void changeSeekbarPos(SeekBar s, int pos){
        int progress = getProgressFromEditText(editTexts[pos], pos);
        s.setProgress(progress);
    }

    private void changeEditTextText(EditText e, int progress, int pos){
        String s = "";
        switch (currentMode){
            case RGB:
            case YCBCR:
                s = Integer.toString((int) ((float) progress * 255.0 / 100.0));
                break;
            case HSV:
                if(pos != 0){
                    s = Float.toString((float) progress / 100);
                }else{
                    s = Float.toString((float) progress / 100 * 360);
                }
                break;
            case CMYK:
                s = Float.toString((float) progress / 100);
        }
        e.setText(s);

    }

    private void changeTextViews(Modes mode){
        switch(mode){
            case RGB:
                textViews[0].setText("R:");
                textViews[1].setText("G:");
                textViews[2].setText("B:");
                currentMode = Modes.RGB;
                break;
            case HSV:
                textViews[0].setText("H:");
                textViews[1].setText("S:");
                textViews[2].setText("V:");
                currentMode = Modes.HSV;
                break;
            case YCBCR:
                textViews[0].setText("Y:");
                textViews[1].setText("Cb:");
                textViews[2].setText("Cr:");
                currentMode = Modes.YCBCR;
                break;
            case CMYK:
                textViews[0].setText("C:");
                textViews[1].setText("M:");
                textViews[2].setText("Y:");
                textViews[3].setText("K:");
                currentMode = Modes.CMYK;
                break;
        }
    }

    private void changeLayouts(Modes mode){
        LinearLayout main = (LinearLayout) findViewById(R.id.main);
        float available_weight = main.getWeightSum() / 2;
        int n = NELEMS;
        switch(mode){
            case RGB:
            case HSV:
            case YCBCR:
                n = 3;
                break;
            case CMYK:
                n = 4;
                break;
        }
        for(int i = 0; i < n; i++){
            linearLayouts[i].setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0, available_weight/n
            ));
        }
        for(int i = n; i < NELEMS; i++){
            linearLayouts[i].setLayoutParams(new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 0
            ));
        }
    }

    private void changeSeekbarsTints(Modes mode, SeekBar[] s){
        switch(mode){
            case RGB:
                s[0].getProgressDrawable().setTint(Color.RED);
                s[1].getProgressDrawable().setTint(Color.GREEN);
                s[2].getProgressDrawable().setTint(Color.BLUE);
                break;
            case CMYK:
                s[0].getProgressDrawable().setTint(Color.CYAN);
                s[1].getProgressDrawable().setTint(Color.MAGENTA);
                s[2].getProgressDrawable().setTint(Color.YELLOW);
                s[3].getProgressDrawable().setTint(Color.BLACK);
                break;
        }

    }

    private void changeMode(Modes mode){
        changeTextViews(mode);
        changeLayouts(mode);
        changeSeekbarsTints(mode, seekBars);
        for(int i = 0; i < NELEMS; i++) {
            changeEditTextText(editTexts[i], seekBars[i].getProgress(), i);
            changeColor(rect, i, seekBars[i].getProgress());
        }
    }

    private void getElems(){
        seekBars[0] = (SeekBar) findViewById(R.id.seek1);
        seekBars[1] = (SeekBar) findViewById(R.id.seek2);
        seekBars[2] = (SeekBar) findViewById(R.id.seek3);
        seekBars[3] = (SeekBar) findViewById(R.id.seek4);
        textViews[0] = (TextView) findViewById(R.id.textview1);
        textViews[1] = (TextView) findViewById(R.id.textview2);
        textViews[2] = (TextView) findViewById(R.id.textview3);
        textViews[3] = (TextView) findViewById(R.id.textview4);
        editTexts[0] = (EditText) findViewById(R.id.edittext1);
        editTexts[1] = (EditText) findViewById(R.id.edittext2);
        editTexts[2] = (EditText) findViewById(R.id.edittext3);
        editTexts[3] = (EditText) findViewById(R.id.edittext4);
        linearLayouts[0] = (LinearLayout) findViewById(R.id.layout1);
        linearLayouts[1] = (LinearLayout) findViewById(R.id.layout2);
        linearLayouts[2] = (LinearLayout) findViewById(R.id.layout3);
        linearLayouts[3] = (LinearLayout) findViewById(R.id.layout4);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        rect = (Button) findViewById(R.id.rect);
        getElems();
        changeMode(Modes.RGB);

        for(int i = 0; i < NELEMS; i++){
            final int pos = i;
            seekBars[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser) {
                        changeColor(rect, pos, progress);
                        changeEditTextText(editTexts[pos], progress, pos);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            editTexts[i].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    changeColorFromEditText(rect, pos);
                    changeSeekbarPos(seekBars[pos], pos);
                    return false;
                }
            });
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
        switch (id){
            case R.id.action_rgb:
                changeMode(Modes.RGB);
                break;
            case R.id.action_hsv:
                changeMode(Modes.HSV);
                break;
            case R.id.action_ycbcr:
                changeMode(Modes.YCBCR);
                break;
            case R.id.action_cmyk:
                changeMode(Modes.CMYK);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
