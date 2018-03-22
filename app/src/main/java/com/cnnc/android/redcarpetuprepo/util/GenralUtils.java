package com.cnnc.android.redcarpetuprepo.util;

import android.graphics.Point;
import android.view.Display;

/**
 * Created by NIKHIL on 3/21/2018.
 */

public class GenralUtils {

    public static int[] getScreenSize(Display display) {
        Point point = new Point();
        display.getRealSize(point);
        return new int[] {point.x,point.y};
    }
}
