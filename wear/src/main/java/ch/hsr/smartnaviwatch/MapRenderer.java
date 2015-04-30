package ch.hsr.smartnaviwatch;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import ch.hsr.navigationmessagingapi.MapPolygon;
import ch.hsr.navigationmessagingapi.MapPolygonCollection;
import ch.hsr.navigationmessagingapi.MapPolygonTypes;
import ch.hsr.navigationmessagingapi.PolygonPoint;

public class MapRenderer {

    public static Bitmap render(MapPolygonCollection map, int width, int height){
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bmp);

        // Fill in map background
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.rgb(233, 229, 220));
        canvas.drawRect(0,0,width, height, backgroundPaint);

        if(map == null || map.getBottomRightViewRange() == null || map.getTopLeftViewRange() == null) return bmp;

        // Determine uniform scaling based on smaller view size
        float deltaX = map.getBottomRightViewRange().x - map.getTopLeftViewRange().x ;
        float deltaY = map.getBottomRightViewRange().y - map.getTopLeftViewRange().y;
        float scalingX = (float)width / deltaX;
        float scalingY = (float)height / deltaY;
        float scaling = Math.min(scalingX, scalingY);

        float offsetX = width / 2f;
        float offsetY = height / 2f;

        for(MapPolygon p : map.getPolygons()) {
            Path path = null;
            Paint polyPaint = getPaintByType(p.getType());
            for(PolygonPoint point : p.getOuterBounds()) {

                if (path == null) {
                    path =new Path();
                    path.moveTo(point.x * scaling + offsetX, point.y * scaling+ offsetY);
                }
                else {
                    path.lineTo(point.x* scaling+ offsetX,point.y* scaling+ offsetY);
                }
            }

            PolygonPoint[][] innerPoints = p.getInnerBounds();
            if (innerPoints != null && polyPaint.getStyle() != Paint.Style.STROKE) {
                path.setFillType(Path.FillType.EVEN_ODD);
                for (int pi = 0; pi < innerPoints.length; pi++) {
                    for (int ppi = 0; ppi < innerPoints[pi].length; ppi++) {
                        if (ppi == 0)
                            path.moveTo(innerPoints[pi][ppi].x * scaling+ offsetX, innerPoints[pi][ppi].y * scaling+ offsetY);
                        else
                            path.lineTo(innerPoints[pi][ppi].x * scaling+ offsetX, innerPoints[pi][ppi].y * scaling+ offsetY);
                    }
                }
            }

            canvas.drawPath(path, polyPaint);
        }

        return bmp;
    }

    private static Paint getPaintByType(MapPolygonTypes type) {
        Paint linePaint = new Paint();

        switch (type) {
            case ROAD_FOOTWAY:
                linePaint.setColor(Color.GRAY);
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setPathEffect(new DashPathEffect(new float[] {5,5}, 5));
                linePaint.setStrokeWidth(2);
                linePaint.setStrokeCap(Paint.Cap.ROUND);
                break;
            case RAILWAY:
                linePaint.setColor(Color.GRAY);
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setPathEffect(new DashPathEffect(new float[] {3, 10}, 5));
                linePaint.setStrokeWidth(5);
                linePaint.setStrokeCap(Paint.Cap.ROUND);
                break;
            case ROAD_RESIDENTIAL:
            case ROAD_DEFAULT:
                linePaint.setColor(Color.WHITE);
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setStrokeWidth(12);
                linePaint.setStrokeCap(Paint.Cap.ROUND);
                break;
            case ROAD_TERTIARY:
                linePaint.setColor(Color.LTGRAY);
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setStrokeWidth(10);
                linePaint.setStrokeCap(Paint.Cap.ROUND);
                break;
            case ROAD_SECONDARY:
                linePaint.setColor(Color.rgb(239,192,49));
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setStrokeWidth(15);
                linePaint.setStrokeCap(Paint.Cap.ROUND);
                break;
            case ROAD_PRIMARY:
                linePaint.setColor(Color.rgb(219,212,9));
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setStrokeWidth(20);
                linePaint.setStrokeCap(Paint.Cap.ROUND);
                break;
            case ROAD_MOTORWAY:
                linePaint.setColor(Color.rgb(219,113,9));
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setStrokeWidth(22);
                linePaint.setStrokeCap(Paint.Cap.ROUND);
                break;
            case ROUTE_PATH:
                linePaint.setColor(Color.argb(200,255,0,0));
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setStrokeWidth(8);
                linePaint.setStrokeCap(Paint.Cap.ROUND);
                break;
            case BUILDING:
                linePaint.setColor(Color.rgb(146,136,127));
                linePaint.setStyle(Paint.Style.FILL);
                break;
            case WATER:
                linePaint.setColor(Color.rgb(132,176,216));
                linePaint.setStyle(Paint.Style.FILL);
                break;
        }

        return linePaint;
    }
}









