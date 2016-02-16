package jenarvaezg.colormodes;

import android.graphics.Color;

/**
 * Created by joseen on 15/02/16.
 */
public interface ColorMode {
    int[] getMaxValues();
    String[] getTexts();
    int getNElems();
    int[] getTints(boolean isBlackBackground);
    int getColor(int[] progresses);
}
