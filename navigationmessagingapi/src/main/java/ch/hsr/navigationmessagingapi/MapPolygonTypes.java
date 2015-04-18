package ch.hsr.navigationmessagingapi;

import java.io.Serializable;

public enum MapPolygonTypes implements Serializable{
    UNKNOWN,
    ROAD_FOOTWAY,
    ROAD_RESIDENTIAL,
    ROAD_MOTORWAY,
    ROAD_DEFAULT,
    ROAD_SECONDARY,
    ROAD_TERTIARY,
    BUILDING,
}
