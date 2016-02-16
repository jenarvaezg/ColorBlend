package jenarvaezg.colormodes;

import android.graphics.Color;

import jenarvaezg.colorselectorcojonudo.MainActivity;

/**
 * Created by joseen on 16/02/16.
 */
public class YCbCrColorMode implements ColorMode {

    private static int MAXYCBCR = 255;
    private static String[] texts = {"Y", "Cb", "Cr"};
    private static int Nelems = 3;
    private static int[] tints = {Color.BLACK, Color.BLACK, Color.BLACK};
    private static int[] blackBackgroundTints = {Color.WHITE, Color.WHITE, Color.WHITE};

    @Override
    public int[] getMaxValues() {
        return new int[]{MAXYCBCR, MAXYCBCR, MAXYCBCR};
    }

    @Override
    public String[] getTexts() {
        return YCbCrColorMode.texts;
    }

    @Override
    public int getNElems() {
        return YCbCrColorMode.Nelems;
    }

    @Override
    public int[] getTints(boolean isBlackBackground) {
        if(isBlackBackground){
            return YCbCrColorMode.blackBackgroundTints;
        }
        return YCbCrColorMode.tints;
    }

    @Override
    public int getColor(int[] progresses) {
        int[] YCbCr = new int[Nelems];
        int[] RGB = new int[new RGBColorMode().getNElems()];
        int[] maxRGBs = new RGBColorMode().getMaxValues();
        for(int i = 0; i < 3; i++){
            YCbCr[i] = (int) ((float)progresses[i] / MainActivity.MAXPROGRESS * MAXYCBCR);
        }
        RGB[0] = ((int) ( 298.082 * (YCbCr[0] - 16)   +
                408.583 * (YCbCr[2] - 128)    )) >> 8;
        RGB[1] = ((int) ( 298.082 * (YCbCr[0] - 16)   +
                -100.291 * (YCbCr[1] - 128) +
                -208.120 * (YCbCr[2] - 128)    )) >> 8;
        RGB[2] = ((int) ( 298.082 * (YCbCr[0] - 16)   +
                516.411 * (YCbCr[1] - 128)    )) >> 8;
        for (int i=0; i<Nelems; i++) {
            if (RGB[i] > maxRGBs[i])
                RGB[i] = maxRGBs[i];
            else if (RGB[i] < 0)
                RGB[i] = 0;
        }
        return android.graphics.Color.rgb(RGB[0], RGB[1], RGB[2]);
    }
}