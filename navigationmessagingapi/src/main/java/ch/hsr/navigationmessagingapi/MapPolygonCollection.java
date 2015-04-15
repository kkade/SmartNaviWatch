package ch.hsr.navigationmessagingapi;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of polygons on the map
 */
public class MapPolygonCollection {
    private List<MapPolygon> polygons = new ArrayList<MapPolygon>();

    /**
     * Adds a new polygon to the collection
     * @param poly polygon
     */
    public void add(MapPolygon poly) {
        polygons.add(poly);
    }

    /**
     * Makes the polygon points fit inside a bounding rectangle with the origin 0
     */
    public void normalize() {
        if(polygons.size() == 0) return;

        int boundingRectangleX = polygons.get(0).getOuterX();
        int boundingRectangleY = polygons.get(0).getOuterY();

        for(MapPolygon poly : polygons) {
            boundingRectangleX = Math.min(poly.getOuterX(), boundingRectangleX);
            boundingRectangleY = Math.min(poly.getOuterY(), boundingRectangleY);
        }

        for(MapPolygon poly : polygons) {
            poly.offset(-boundingRectangleX, -boundingRectangleY);
        }
    }
}
