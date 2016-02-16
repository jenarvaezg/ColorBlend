package jenarvaezg.colormodes;

import android.graphics.Color;
import android.text.InputType;

import jenarvaezg.colorselectorcojonudo.MainActivity;

/**
 * Created by joseen on 16/02/16.
 */


public class RGBColorMode implements ColorMode {

    protected static int MAXRGB = 255;
    private static String[] texts = {"R", "G", "B"};
    private static int Nelems = 3;
    private static int[] tints = {Color.RED, Color.GREEN, Color.BLUE};

    @Override
    public int[] getMaxValues() {
        return new int[]{MAXRGB, MAXRGB, MAXRGB};
    }

    @Override
    public String[] getTexts() {
        return RGBColorMode.texts;
    }

    @Override
    public int getNElems() {
        return RGBColorMode.Nelems;
    }

    @Override
    public int[] getTints(boolean isBlackBackground) {
        return RGBColorMode.tints;
    }

    @Override
    public int getColor(int[] progresses) {
        for(int i = 0; i < Nelems; i++){
            progresses[i] = (int) ((float) progresses[i] / MainActivity.MAXPROGRESS * MAXRGB);
        }
        return android.graphics.Color.rgb(progresses[0], progresses[1], progresses[2]);
    }

    @Override
    public String progressToText(int progress, int pos) {
        return Integer.toString((int) ((float) progress * MAXRGB / MainActivity.MAXPROGRESS));
    }

    @Override
    public int textToProgress(String text, int pos) {
        Float progress = getFilteredProgress(Float.parseFloat(text), pos);
        return (int)(progress * MainActivity.MAXPROGRESS / MAXRGB);
    }

    @Override
    public int getInputType() {
        return InputType.TYPE_CLASS_NUMBER;
    }

    @Override
    public float getFilteredProgress(float progress, int pos) {
        if(progress > MAXRGB) {
            progress = MAXRGB;
        }else if(progress < 0){
            progress = 0;
        }
        return progress;
    }
}
