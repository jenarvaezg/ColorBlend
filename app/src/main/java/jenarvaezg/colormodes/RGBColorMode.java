package jenarvaezg.colormodes;

import android.graphics.Color;

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
        for(int p: progresses){
            p = (int) ((float) p / MainActivity.MAXPROGRESS * MAXRGB);
        }
        return android.graphics.Color.rgb(progresses[0], progresses[1], progresses[2]);
    }
}
