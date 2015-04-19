package ch.hsr.navigationmessagingapi;

import java.io.Serializable;

public enum MapPolygonTypes implements Serializable{
    UNKNOWN,
    ROAD_MOTORWAY,
    ROAD_PRIMARY,
    ROAD_SECONDARY,
    ROAD_DEFAULT,
    ROAD_TERTIARY,
    ROAD_RESIDENTIAL,
    ROAD_FOOTWAY,
    BUILDING,
    RAILWAY,
    WATER
}
