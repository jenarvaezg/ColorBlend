package jenarvaezg.colorselectorcojonudo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import jenarvaezg.colormodes.CMYKColorMode;
import jenarvaezg.colormodes.ColorMode;
import jenarvaezg.colormodes.HSVColorMode;
import jenarvaezg.colormodes.RGBColorMode;
import jenarvaezg.colormodes.YCbCrColorMode;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


public class MainActivity extends Activity {


    private static final int NELEMS = 4;

    public static final int MAXPROGRESS = 1000;

    private ColorMode currentMode;
    private boolean isBlackBackground;

    private SeekBar[] seekBars = new SeekBar[NELEMS];
    private TextView[] textViews = new TextView[NELEMS];
    private LinearLayout[] linearLayouts = new LinearLayout[NELEMS];
    private EditText[] editTexts = new EditText[NELEMS];

    private InterstitialAd mInterstitialAd;
    private Button rect;



    private int[] getButtonRGB(Button rect) {
        ColorDrawable drawable = (ColorDrawable) rect.getBackground();
        int colorBefore = drawable.getColor();
        return new int[]{Color.red(colorBefore),
                Color.green(colorBefore),
                Color.blue(colorBefore)};
    }

    private void changeColor(Button rect) {
        int[] RGB = getButtonRGB(rect);
        int[] progresses = new int[seekBars.length];
        for (int i = 0; i < seekBars.length; i++) {
            progresses[i] = seekBars[i].getProgress();
        }
        rect.setBackgroundColor(currentMode.getColor(progresses));
        rect.setTextColor(getContrastColor(RGB));
    }

    private int getContrastColor(int[] RGB) {
        int color = android.graphics.Color.rgb(RGB[0], RGB[1], RGB[2]);
        return ColorInfoActivity.getContrastColor(color);
    }

    private void changeSeekbarPos(SeekBar s, int pos){
        String text = editTexts[pos].getText().toString();
        s.setProgress(currentMode.textToProgress(text, pos));

    }

    private void changeEditTextText(EditText e, int progress, int pos){
        String s = currentMode.progressToText(progress, pos);
        e.setInputType(currentMode.getInputType());
        e.setText(s);

    }

    private void changeTextViews(ColorMode mode){
        String[] texts = mode.getTexts();
        for(int i = 0; i < texts.length; i++){
            textViews[i].setText(texts[i]);
        }
    }

    private void changeLayouts(ColorMode mode){
        LinearLayout main = (LinearLayout) findViewById(R.id.main);
        float available_weight = main.getWeightSum() / 2;
        int n = mode.getNElems();
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

    private void changeSeekbarsTints(ColorMode mode, SeekBar[] s){
        int[]tints = mode.getTints(isBlackBackground);
        for(int i = 0; i < tints.length; i++)
            s[i].getProgressDrawable().setTint(tints[i]);
    }

    private void changeMode(ColorMode mode){
        changeTextViews(mode);
        changeLayouts(mode);
        changeSeekbarsTints(mode, seekBars);
        changeColor(rect);
        for(int i = 0; i < NELEMS; i++) {
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

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rect = (Button) findViewById(R.id.rect);
        getElems();

        setupElems();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3471223650360332/6052562801");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                goToColorInfo();
            }
        });

        requestNewInterstitial();
        isBlackBackground = false;
        if (savedInstanceState != null) {
            currentMode = (ColorMode) savedInstanceState.getSerializable("mode");
            if (savedInstanceState.getBoolean("isBlack")){
                switchBackgroundColors();
            }
        } else {
            currentMode = new RGBColorMode();
        }


    }

    private void setupElems() {
        for(int i = 0; i < NELEMS; i++){
            final int pos = i;
            seekBars[i].setMax(MAXPROGRESS);
            seekBars[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    changeColor(rect);
                    changeEditTextText(editTexts[pos], progress, pos);
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
                    changeSeekbarPos(seekBars[pos], pos);
                    return false;
                }
            });
        }
        rect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {*/
                    goToColorInfo();
                //}
            }
        });
    }

    private void goToColorInfo(){
        Intent activityIntent = new Intent(rect.getContext(), ColorInfoActivity.class);
        Bundle newActivityInfo = new Bundle();
        ColorDrawable drawable = (ColorDrawable) rect.getBackground();
        newActivityInfo.putInt("color", drawable.getColor());
        newActivityInfo.putBoolean("isBlack", isBlackBackground);
        activityIntent.putExtras(newActivityInfo);
        startActivity(activityIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeMode(currentMode);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putSerializable("mode", currentMode);
        state.putBoolean("isBlack", isBlackBackground);
        super.onSaveInstanceState(state);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_rgb:
                currentMode = new RGBColorMode();
                break;
            case R.id.action_hsv:
                currentMode = new HSVColorMode();
                break;
            case R.id.action_ycbcr:
                currentMode = new YCbCrColorMode();
                break;
            case R.id.action_cmyk:
                currentMode = new CMYKColorMode();
                break;
            case R.id.action_change_background:
                switchBackgroundColors();
        }
        changeMode(currentMode);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    protected void switchBackgroundColors() {
        isBlackBackground = !isBlackBackground;
        switchViewBackground(findViewById(R.id.main));
        for(TextView t: textViews){
            switchTextColor(t);
        }
        for(EditText e: editTexts){
            switchTextColor(e);
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
