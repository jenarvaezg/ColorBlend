package jenarvaezg.colormodes;

import android.graphics.Color;

import java.io.Serializable;

/**
 * Created by joseen on 15/02/16.
 */
public interface ColorMode extends Serializable{
    int[] getMaxValues();
    String[] getTexts();
    int getNElems();
    int[] getTints(boolean isBlackBackground);
    int getColor(int[] progresses);
    String progressToText(int progress, int pos);
    int textToProgress(String text, int pos);
    int getInputType();
    float getFilteredProgress(float progress, int pos);
}
