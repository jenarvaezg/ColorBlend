package jenarvaezg.colormodes;

import android.graphics.Color;

import jenarvaezg.colorselectorcojonudo.MainActivity;


/**
 * Created by joseen on 16/02/16.
 */
public class HSVColorMode implements ColorMode {

    private static int MAXH = 255;
    private static int MAXSV = 1;
    private static String[] texts = {"H", "S", "V"};
    private static int Nelems = 3;
    private static int[] tints = {Color.BLACK, Color.BLACK, Color.BLACK};
    private static int[] blackBackgroundTints = {Color.WHITE, Color.WHITE, Color.WHITE};

    @Override
    public int[] getMaxValues() {
        return new int[]{MAXH, MAXSV, MAXSV};
    }

    @Override
    public String[] getTexts() {
        return HSVColorMode.texts;
    }

    @Override
    public int getNElems() {
        return HSVColorMode.Nelems;
    }

    @Override
    public int[] getTints(boolean isBlackBackground) {
        if(isBlackBackground){
            return HSVColorMode.blackBackgroundTints;
        }
        return HSVColorMode.tints;
    }

    @Override
    public int getColor(int[] progresses) {
        float[] hsv = new float[HSVColorMode.Nelems];
        for(int i = 0; i < HSVColorMode.Nelems; i++){
            if (i == 0) {
                hsv[i] = (float) progresses[i] / MainActivity.MAXPROGRESS * MAXH;
            } else if (i <= 2) {
                hsv[i] = (float) progresses[i] / MainActivity.MAXPROGRESS * MAXSV;
            }
        }
        return Color.HSVToColor(hsv);
    }
}