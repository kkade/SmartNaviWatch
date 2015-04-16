package ch.hsr.navigationmessagingapi;

import java.io.Serializable;

/**
 * Represents a polygon on the map
 */
public class MapPolygon implements Serializable{
    private int type;
    private PolygonPoint[]   outerBounds;
    private PolygonPoint[][] innerBounds;
    private int outerXMin = Integer.MAX_VALUE;
    private int outerYMin = Integer.MAX_VALUE;

    private int outerXMax = Integer.MIN_VALUE;
    private int outerYMax= Integer.MIN_VALUE;

    public MapPolygon(int type, PolygonPoint[] outerBounds, PolygonPoint[][] innerBounds) {
        this.type = type;
        this.outerBounds = outerBounds;
        this.innerBounds = innerBounds;

        recalculateBounds(outerBounds);
    }

    private void recalculateBounds(PolygonPoint[] outerBounds) {
        for(int i = 0; i < outerBounds.length; i++) {
            outerXMin = Math.min(outerXMin, outerBounds[i].x);
            outerYMin = Math.min(outerYMin, outerBounds[i].y);

            outerXMax = Math.max(outerXMax, outerBounds[i].x);
            outerYMax = Math.max(outerYMax, outerBounds[i].y);
        }
    }

    public int getOuterXMin() {
        return outerXMin;
    }

    public int getOuterYMin() {
        return outerYMin;
    }

    public int getOuterXMax() {
        return outerXMax;
    }

    public int getOuterYMax() {
        return outerYMax;
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
