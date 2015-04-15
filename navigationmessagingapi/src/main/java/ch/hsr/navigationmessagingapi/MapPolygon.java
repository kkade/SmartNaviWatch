package ch.hsr.navigationmessagingapi;

import android.graphics.Point;

/**
 * Represents a polygon on the map
 */
public class MapPolygon {
    private int type;
    private Point[]   outerBounds;
    private Point[][] innerBounds;
    private int outerX = Integer.MAX_VALUE;
    private int outerY = Integer.MAX_VALUE;

    public MapPolygon(int type, Point[] outerBounds, Point[][] innerBounds) {
        this.type = type;
        this.outerBounds = outerBounds;
        this.innerBounds = innerBounds;

        for(int i = 0; i < outerBounds.length; i++) {
            outerX = Math.min(outerX, outerBounds[i].x);
            outerY = Math.min(outerX, outerBounds[i].x);
        }
    }

    public int getOuterX() {
        return outerX;
    }

    public int getOuterY() {
        return outerY;
    }

    public void offset(int deltaX, int deltaY) {
        for(int outerPoint = 0; outerPoint < outerBounds.length; outerPoint++) {
            outerBounds[outerPoint].x += deltaX;
            outerBounds[outerPoint].y += deltaY;
        }

        for(int innerPoly = 0; innerPoly < innerBounds.length; innerPoly++) {
            for(int innerPoint =0; innerPoint < innerBounds[innerPoly].length; innerPoint++) {
                innerBounds[innerPoly][innerPoint].x += deltaX;
                innerBounds[innerPoly][innerPoint].y += deltaY;
            }
        }
    }
}
