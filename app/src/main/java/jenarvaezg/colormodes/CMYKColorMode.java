package jenarvaezg.colormodes;

import android.graphics.Color;

import jenarvaezg.colorselectorcojonudo.MainActivity;

/**
 * Created by joseen on 16/02/16.
 */
public class CMYKColorMode implements ColorMode {

    private static int MAXCMYK = 1;
    private static String[] texts = {"C", "M", "Y", "K"};
    private static int Nelems = 4;
    private static int[] tints = {Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.BLACK};

    @Override
    public int[] getMaxValues() {
        return new int[]{MAXCMYK, MAXCMYK, MAXCMYK, MAXCMYK};
    }

    @Override
    public String[] getTexts() {
        return CMYKColorMode.texts;
    }

    @Override
    public int getNElems() {
        return CMYKColorMode.Nelems;
    }

    @Override
    public int[] getTints(boolean isBlackBackground) {
        return CMYKColorMode.tints;
    }

    @Override
    public int getColor(int[] progresses) {
        float[] CMYK = new float[Nelems];
        int[] RGB = new int[new RGBColorMode().getNElems()];
        for(int i = 0; i < Nelems; i++){
            CMYK[i] = ((float) progresses[i] / MainActivity.MAXPROGRESS);
        }
        for(int i = 0; i < 3; i++){
            RGB[i] = (int) (RGBColorMode.MAXRGB * (1.0f-CMYK[i]) * (1.0f-CMYK[3]));
        }
        return android.graphics.Color.rgb(RGB[0], RGB[1], RGB[2]);
    }
}
