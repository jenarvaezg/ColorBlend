package jenarvaezg.colorselectorcojonudo;

import android.graphics.Color;

/**
 * Created by joseen on 15/02/16.
 */
public interface ColorMode {
    int[] getMaxValues();
    String[] getTexts();
    int getNElems();
    Color[] getTints(boolean isBlackBackground);
    int getColor(int[] progresses);
}
