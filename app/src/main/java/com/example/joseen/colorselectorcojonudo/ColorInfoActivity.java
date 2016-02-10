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
        MainActivity.Modes mode = MainActivity.Modes.RGB;
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
        setColorAttributes(mainColor, color, mode);
        setComplementariesAtributes(color, mode);
        setTriadAttributes(color, mode);
        setAnalogousAttributes(color, mode);
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

    private void setAnalogousAttributes(int color, MainActivity.Modes mode) {
        float baseShift = 20f;
        for(int i = 0; i < analogous.length; i++){
            if(i % 2 != 0){
                setColorAttributes(analogous[i], shiftColorHSV(color, baseShift * -i), mode);
            }else{
                setColorAttributes(analogous[i], shiftColorHSV(color, baseShift * i), mode);
            }
        }
    }

    private void setTriadAttributes(int color, MainActivity.Modes mode) {
        float baseShift = 120f;
        for(int i = 0; i < triad.length; i++){
            setColorAttributes(triad[i], shiftColorHSV(color, baseShift * i), mode);
        }
    }

    private void setComplementariesAtributes(int color, MainActivity.Modes mode) {
        float baseShift = 180f;
        for(int i = 0; i < complementaries.length; i++){
            setColorAttributes(complementaries[i], shiftColorHSV(color, baseShift * i), mode);
        }
    }

    private void setColorAttributes(Button c, int color, MainActivity.Modes mode) {
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
