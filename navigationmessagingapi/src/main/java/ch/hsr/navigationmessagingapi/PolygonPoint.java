package ch.hsr.navigationmessagingapi;

import java.io.Serializable;

public class PolygonPoint implements Serializable {
    private static final long serialVersionUID = 1L;

    public int x;
    public int y;

    public PolygonPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
