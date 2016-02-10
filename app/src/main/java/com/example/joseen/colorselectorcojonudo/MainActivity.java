package com.example.joseen.colorselectorcojonudo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Arrays;


public class MainActivity extends Activity {


    private static final int NELEMS = 4;

    private static final int MAXPROGRESS = 1000;
    private static final int MAXRGB = 255;
    private static final int MAXYCBCR = 255;
    private static final float MAXHUE = 360.0f;

    private Modes currentMode;
    private boolean isBlackBackground;

    private SeekBar[] seekBars = new SeekBar[NELEMS];
    private TextView[] textViews = new TextView[NELEMS];
    private LinearLayout[] linearLayouts = new LinearLayout[NELEMS];
    private EditText[] editTexts = new EditText[NELEMS];

    private Button rect;

    protected enum Modes { RGB, HSV, YCBCR, CMYK }

    private int getRGBInt(int pos, int progress, int[] RGB){
        if(pos <= 2) {
            RGB[pos] = (int) ((float) progress / MAXPROGRESS * MAXRGB);
        }
        return android.graphics.Color.rgb(RGB[0], RGB[1], RGB[2]);
    }

    private int getHSVInt(int pos, int progress, int[] RGB) {
        float[] hsv = new float[3];
        Color.RGBToHSV(RGB[0], RGB[1], RGB[2], hsv);
        if (pos == 0) {
            hsv[pos] = (float) progress / MAXPROGRESS * MAXHUE;
        } else if (pos <= 2) {
            hsv[pos] = (float) progress / MAXPROGRESS;
        }
        return Color.HSVToColor(hsv);
    }

    private int getYCbCrInt(int pos, int progress, int[] RGB){
        if(pos > 2)
            return android.graphics.Color.rgb(RGB[0], RGB[1], RGB[2]);
        int[] YCbCr = new int[3];
        //TODO Confirmed: this doesnt work as intended
        /*YCbCr[0] = (int)(0.299*RGB[0]+0.587*RGB[1]+0.114*RGB[2]);
        YCbCr[1] = (int)(128-0.169*RGB[0]-0.331*RGB[1]+0.500*RGB[2]);
        YCbCr[2] = (int)(128+0.500*RGB[0]-0.419*RGB[1]-0.081*RGB[2]);*/
        for(int i = 0; i < 3; i++){
            YCbCr[i] = (int) ((float)seekBars[i].getProgress() / MAXPROGRESS * MAXYCBCR);
        }

        YCbCr[pos] = (int) ((float) progress / MAXPROGRESS * MAXYCBCR);
        RGB[0] = ((int) ( 298.082 * (YCbCr[0] - 16)   +
                408.583 * (YCbCr[2] - 128)    )) >> 8;
        RGB[1] = ((int) ( 298.082 * (YCbCr[0] - 16)   +
                -100.291 * (YCbCr[1] - 128) +
                -208.120 * (YCbCr[2] - 128)    )) >> 8;
        RGB[2] = ((int) ( 298.082 * (YCbCr[0] - 16)   +
                516.411 * (YCbCr[1] - 128)    )) >> 8;
        for (int i=0; i<3; i++) {
            if (RGB[i] > MAXYCBCR)
                RGB[i] = MAXYCBCR;
            else if (RGB[i] < 0)
                RGB[i] = 0;
        }
        return android.graphics.Color.rgb(RGB[0], RGB[1], RGB[2]);
    }

    private int getCMYKInt(int pos, int progress, int[] RGB){
        float[] CMYK = new float[4];
        //ugly
        for(int i = 0; i < 4; i++){
            CMYK[i] = ((float) seekBars[i].getProgress()) / MAXPROGRESS;
        }
        if(pos < 4){
            CMYK[pos] = (float) progress / MAXPROGRESS; //This makes sense if user inputs value from EditText
            //R = 255 × (1-C) × (1-K) || G = 255 × (1-M) × (1-K) || B = 255 × (1-Y) × (1-K)
            for(int i = 0; i < 3; i++){
                RGB[i] = (int) (MAXRGB * (1.0f-CMYK[i]) * (1.0f-CMYK[3]));
            }
        }
        return android.graphics.Color.rgb(RGB[0], RGB[1], RGB[2]);
    }

    private int[] getButtonRGB(Button rect) {
        ColorDrawable drawable = (ColorDrawable) rect.getBackground();
        int colorBefore = drawable.getColor();
        return new int[]{Color.red(colorBefore),
                Color.green(colorBefore),
                Color.blue(colorBefore)};
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
                break;
            case CMYK:
                rect.setBackgroundColor(getCMYKInt(pos, progress, RGB));
        }
        rect.setTextColor(getContrastColor(getButtonRGB(rect)));
    }

    private int getContrastColor(int[] RGB) {
        int color = android.graphics.Color.rgb(RGB[0], RGB[1], RGB[2]);
        return ColorInfoActivity.getContrastColor(color);
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
                if(f > MAXYCBCR)
                    f = MAXYCBCR;
                progress = (int) (f / MAXYCBCR * MAXPROGRESS);
                e.setText(Integer.toString((int)f));
                break;
            case HSV:
                if(pos == 0){
                    if(f > MAXHUE)
                        f = MAXHUE;
                    progress = (int) (f / MAXHUE * MAXPROGRESS);

                }else{
                    if(f > 1)
                        f = 1;
                    progress = (int) (f * MAXPROGRESS);
                }
                e.setText(Float.toString(f));
                break;
            case CMYK:
                if(f > 1)
                    f = 1;
                progress = (int) (f * MAXPROGRESS);
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
                s = Integer.toString((int) ((float) progress * MAXYCBCR / MAXPROGRESS));
                e.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case HSV:
                if(pos != 0){
                    s = Float.toString((float) progress / MAXPROGRESS);
                }else{
                    s = Float.toString((float) progress / MAXPROGRESS * MAXHUE);
                }
                e.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;
            case CMYK:
                s = Float.toString((float) progress / MAXPROGRESS);
                e.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
        e.setText(s);

    }

    private void changeTextViews(Modes mode){

        switch(mode){
            case RGB:
            case HSV:
            case CMYK:
                for(int i = 0; i < mode.toString().length(); i++){
                    textViews[i].setText(Character.toString(mode.toString().charAt(i)) + ":");
                }

                break;
            case YCBCR: //special case
                textViews[0].setText("Y:");
                textViews[1].setText("Cb:");
                textViews[2].setText("Cr:");
                break;
        }
        currentMode = mode;
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
            default:
                for(SeekBar sb: s){
                    if(isBlackBackground) {
                        sb.getProgressDrawable().setTint(Color.WHITE);
                    }else{
                        sb.getProgressDrawable().setTint(Color.BLACK);
                    }
                }
        }

    }

    private void changeMode(Modes mode){
        changeTextViews(mode);
        changeLayouts(mode);
        changeSeekbarsTints(mode, seekBars);
        for(int i = 0; i < NELEMS; i++) {
            changeColor(rect, i, seekBars[i].getProgress());
            changeEditTextText(editTexts[i], seekBars[i].getProgress(), i);
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
        isBlackBackground = false;
        if (savedInstanceState != null) {
            currentMode = Modes.values()[savedInstanceState.getInt("mode")];
            if (savedInstanceState.getBoolean("isBlack")){
                switchBackgroundColors();
            }
        } else {
            currentMode = Modes.RGB;
        }

        for(int i = 0; i < NELEMS; i++){
            final int pos = i;
            seekBars[i].setMax(1000);
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
        rect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activityIntent = new Intent(v.getContext(), ColorInfoActivity.class);
                Bundle newActivityInfo = new Bundle();
                ColorDrawable drawable = (ColorDrawable) v.getBackground();
                newActivityInfo.putInt("color", drawable.getColor());
                newActivityInfo.putInt("mode", currentMode.ordinal());
                newActivityInfo.putBoolean("isBlack", isBlackBackground);
                activityIntent.putExtras(newActivityInfo);
                startActivity(activityIntent);
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        changeMode(currentMode);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putInt("mode", currentMode.ordinal());
        state.putBoolean("isBlack", isBlackBackground);
        super.onSaveInstanceState(state);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
            case R.id.action_change_background:
                switchBackgroundColors();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void switchBackgroundColors() {
        isBlackBackground = !isBlackBackground;
        switchViewBackground(findViewById(R.id.main));
        for(TextView t: textViews){
            switchTextColor(t);
        }
        for(EditText e: editTexts){
            switchTextColor((TextView) e);
        }
        changeSeekbarsTints(currentMode, seekBars);

    }

    protected void switchTextColor(TextView t) {

        if (isBlackBackground){
            t.setTextColor(Color.WHITE);
        }else{
            t.setTextColor(Color.BLACK);
        }
    }

    protected void switchViewBackground(View l) {
        if(isBlackBackground){
            l.setBackgroundColor(Color.BLACK);
        }else{
            l.setBackgroundColor(Color.WHITE);
        }
    }
}
