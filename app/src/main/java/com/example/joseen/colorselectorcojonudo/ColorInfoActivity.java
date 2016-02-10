package com.example.joseen.colorselectorcojonudo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;

public class ColorInfoActivity extends Activity {


    private Button mainColor;
    private Button[] complementaries = new Button[2];
    private Button[] triad = new Button[3];
    private Button[] analogous = new Button[3];
    private TextView[] textViews = new TextView[4];
    private boolean isBlackBackground;

    private void getElems(){
        mainColor = (Button) findViewById(R.id.mainColor);
        complementaries[0] = (Button) findViewById(R.id.complementary1);
        complementaries[1] = (Button) findViewById(R.id.complementary2);
        triad[0] = (Button) findViewById(R.id.triad1);
        triad[1] = (Button) findViewById(R.id.triad2);
        triad[2] = (Button) findViewById(R.id.triad3);
        analogous[0] = (Button) findViewById(R.id.analogous1);
        analogous[1] = (Button) findViewById(R.id.analogous2);
        analogous[2] = (Button) findViewById(R.id.analogous3);
        textViews[0] = (TextView) findViewById(R.id.mainHeader);
        textViews[1] = (TextView) findViewById(R.id.complementaryHeader);
        textViews[2] = (TextView) findViewById(R.id.triadHeader);
        textViews[3] = (TextView) findViewById(R.id.analogousHeader);
    }

    private int shiftColorHSV(int color, float shift){
        float[] hsv = new float[3];
        Color.RGBToHSV(Color.red(color), Color.green(color),
                Color.blue(color), hsv);
        hsv[0] = (hsv[0] + shift) % 360;
        return Color.HSVToColor(hsv);
    }

    protected static int getContrastColor(int color){
        float[] hsv = new float[3];
        Color.RGBToHSV(Color.red(color), Color.green(color),
                Color.blue(color), hsv);
        hsv[0] = (hsv[0] + 180) % 360;
        hsv[1] += 0.5;
        if(hsv[1] > 1){
            hsv[1] -= 1.0;
        }
        hsv[2] += 0.5;
        if(hsv[2] > 1){
            hsv[2] -= 1.0;
        }
        return Color.HSVToColor(hsv);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_info);
        Intent intent = getIntent();
        Bundle info = intent.getExtras();
        int color = Color.BLACK;
        isBlackBackground = false;
        getElems();
        if(info != null){
            color = info.getInt("color");
            if(info.getBoolean("isBlack")){
                switchBackgroundColors();
            }
        }
        if(savedInstanceState != null){
            if (savedInstanceState.getBoolean("isBlack")){
                isBlackBackground = false;
                switchBackgroundColors();
            }
        }
        setupButtonsListeners();
        setMainColorInfo(color);
        setComplementariesAtributes(color);
        setTriadAttributes(color);
        setAnalogousAttributes(color);

    }

    private String arrayParenthesizedString(int[] arr){
        String s = "(";
        for(int i = 0; i < arr.length; i++){
            s += Integer.toString(arr[i]);
            if(i != arr.length - 1){
                s += ", ";
            }else{
                s += ")";
            }
        }
        return s;
    }

    private String arrayParenthesizedString(float[] arr, boolean percentage){
        String s = "(";
        for(int i = 0; i < arr.length; i++){
            if(percentage) {
                if (arr[i] <= 1.0) {
                    s += Integer.toString((int) (arr[i] * 100)) + "%";
                } else {
                    s += Integer.toString((int) arr[i]);
                }
            }else{
                s += Float.toString(arr[i]);
            }
            if(i != arr.length - 1){
                s += ", ";
            }else{
                s += ")";
            }
        }
        return s;
    }

    private String getHSVString(int color){
        float[] HSV = new float[3];
        Color.colorToHSV(color, HSV);
        String hsvString = "HSV: " + arrayParenthesizedString(HSV, true);
        return hsvString;
    }


    private String getYCbCrString(int color) {
        int[] RGB = new int[]{Color.red(color),
                Color.green(color),
                Color.blue(color)};
        int[] YCbCr = new int[3];

        YCbCr[0] = (int)( 0.299   * RGB[0] + 0.587   * RGB[1] + 0.114   * RGB[2]);

        YCbCr[1] = (int)(-0.16874 * RGB[0] - 0.33126 * RGB[1] + 0.50000 * RGB[2]);

        YCbCr[2] = (int)( 0.50000 * RGB[0] - 0.41869 * RGB[1] - 0.08131 * RGB[2]);

        /*YCbCr[0] = (int)(0.299*RGB[0]+0.587*RGB[1]+0.114*RGB[2]);
        YCbCr[1] = (int)(128-0.169*RGB[0]-0.331*RGB[1]+0.500*RGB[2]);
        YCbCr[2] = (int)(128+0.500*RGB[0]-0.419*RGB[1]-0.081*RGB[2]);*/

        return "YCbCr: " + arrayParenthesizedString(YCbCr);
    }


    private String getCMYKString(int color) {
        int[] RGB = new int[]{Color.red(color),
                Color.green(color),
                Color.blue(color)};
        float[] CMYK = new float[4];
        float[] RGBPrime = new float[3];
        for(int i = 0; i < RGB.length; i++){
            RGBPrime[i] = (float) RGB[i] / 255;
        }
        //K = 1-max(R', G', B')
        float max = RGBPrime[0];
        for (int i = 1; i < RGB.length; i++) {
            if (RGBPrime[i] > max) {
                max = RGBPrime[i];
            }
        }
        CMYK[3] = 1.0f - max;
        //C = (1-R'-K) / (1-K) || M = (1-G'-K) / (1-K) || Y = (1-B'-K) / (1-K)
        for(int i = 0; i < 3; i++){
            CMYK[i] = (1 - RGBPrime[i] - CMYK[3]) / (1 - CMYK[3]);
        }

        return "CMYK: " + arrayParenthesizedString(CMYK, true);

    }


    private void setMainColorInfo(int color) {
        setColorAttributes(mainColor, color);
        int[] RGB = new int[]{Color.red(color),
                Color.green(color),
                Color.blue(color)};

        String rgbString = "RGB: " + arrayParenthesizedString(RGB);
        String hsvString = getHSVString(color);
        String CMYKString = getCMYKString(color);
        String YCbCrString = "";//getYCbCrString(color);

        String hexString = "Hex: " + mainColor.getText().toString();
        String buttonString = rgbString + "\n" + hsvString + "\n" + CMYKString;
        buttonString += "\n" + YCbCrString + "\n" + hexString;
        mainColor.setText(buttonString);
    }

    private void setupButtonsListeners() {
        Button.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activityIntent = new Intent(v.getContext(), ColorInfoActivity.class);
                Bundle newActivityInfo = new Bundle();
                ColorDrawable drawable = (ColorDrawable) v.getBackground();
                newActivityInfo.putInt("color", drawable.getColor());
                newActivityInfo.putBoolean("isBlack", isBlackBackground);
                activityIntent.putExtras(newActivityInfo);
                startActivity(activityIntent);
            }
        };
        for(int i = 1; i < complementaries.length; i++){
            complementaries[i].setOnClickListener(listener);
        }
        for(int i = 1; i < triad.length; i++){
            triad[i].setOnClickListener(listener);
        }
        for(int i = 1; i < analogous.length; i++){
            analogous[i].setOnClickListener(listener);
        }

    }

    private void setAnalogousAttributes(int color) {
        float baseShift = 20f;
        for(int i = 0; i < analogous.length; i++){
            if(i % 2 != 0){
                setColorAttributes(analogous[i], shiftColorHSV(color, baseShift * -i));
            }else{
                setColorAttributes(analogous[i], shiftColorHSV(color, baseShift * i));
            }
        }
    }

    private void setTriadAttributes(int color) {
        float baseShift = 120f;
        for(int i = 0; i < triad.length; i++){
            setColorAttributes(triad[i], shiftColorHSV(color, baseShift * i));
        }
    }

    private void setComplementariesAtributes(int color) {
        float baseShift = 180f;
        for(int i = 0; i < complementaries.length; i++){
            setColorAttributes(complementaries[i], shiftColorHSV(color, baseShift * i));
        }
    }

    private void setColorAttributes(Button c, int color) {
        c.setBackgroundColor(color);
        c.setText(String.format("#%06X", (0xFFFFFF & color)));
        c.setTextColor(getContrastColor(color));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_color_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_change_background) {
            switchBackgroundColors();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSaveInstanceState(Bundle state){
        state.putBoolean("isBlack", isBlackBackground);
        super.onSaveInstanceState(state);
    }

    private void switchBackgroundColors() {
        isBlackBackground = !isBlackBackground;
        for(TextView t: textViews){
            switchViewBackground(t);
            switchTextColor(t);
        }


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
