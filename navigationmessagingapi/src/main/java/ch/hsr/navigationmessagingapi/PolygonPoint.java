package ch.hsr.navigationmessagingapi;

import java.io.Serializable;

public class PolygonPoint implements Serializable {
    public int x;
    public int y;

    public PolygonPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
