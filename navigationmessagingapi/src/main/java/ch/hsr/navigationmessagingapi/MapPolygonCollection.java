package ch.hsr.navigationmessagingapi;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of polygons on the map
 */
public class MapPolygonCollection {
    private List<MapPolygon> polygons = new ArrayList<MapPolygon>();

    private int outerXMin = Integer.MAX_VALUE;
    private int outerYMin = Integer.MAX_VALUE;

    private int outerXMax = Integer.MIN_VALUE;
    private int outerYMax = Integer.MIN_VALUE;

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

    /**
     * Adds a new polygon to the collection
     * @param poly polygon
     */
    public void add(MapPolygon poly) {
        polygons.add(poly);
        recalcBounds();
    }

    /**
     * Makes the polygon points fit inside a bounding rectangle with the origin 0
     */
    public void normalize() {
        if(polygons.size() == 0) return;

        for(MapPolygon poly : polygons) {
            poly.offset(-outerXMin, -outerYMin);
        }

        recalcBounds();
    }

    private void recalcBounds() {
        outerXMin = Integer.MAX_VALUE;
        outerYMin = Integer.MAX_VALUE;

        outerXMax = Integer.MIN_VALUE;
        outerYMax= Integer.MIN_VALUE;

        for(MapPolygon p : polygons) {
            outerXMin = Math.min(outerXMin, p.getOuterXMin());
            outerYMin = Math.min(outerYMin, p.getOuterYMin());

            outerXMax = Math.max(outerXMax, p.getOuterXMax());
            outerYMax = Math.max(outerYMax, p.getOuterYMax());
        }
    }
}
