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
        Log.d("newpaint", "paintpaint " + map.getPolygons().size() );
        Canvas canvas = new Canvas(bmp);

        // Fill in map background
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.rgb(240, 237, 229));
        canvas.drawRect(0,0,width, height, backgroundPaint);


        float scaling = 0.02f;
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
                linePaint.setPathEffect(new DashPathEffect(new float[] {10,10}, 5));
                linePaint.setStrokeWidth(2);
                break;
            case ROAD_RESIDENTIAL:
            case ROAD_DEFAULT:
                linePaint.setColor(Color.WHITE);
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setStrokeWidth(5);
                break;
            case ROAD_TERTIARY:
                linePaint.setColor(Color.LTGRAY);
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setStrokeWidth(6);
                break;
            case ROAD_SECONDARY:
                linePaint.setColor(Color.rgb(219,167,9));
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setStrokeWidth(10);
                break;
            case ROAD_MOTORWAY:
                linePaint.setColor(Color.WHITE);
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setStrokeWidth(20);
                break;
            case BUILDING:
                linePaint.setColor(Color.rgb(100,100,80));
                linePaint.setStyle(Paint.Style.FILL);
                break;
        }

        return linePaint;
    }
}