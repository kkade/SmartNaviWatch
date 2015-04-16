package ch.hsr.smartnaviwatch;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import ch.hsr.navigationmessagingapi.MapPolygonCollection;

public class MapRenderer {
    public static Bitmap render(MapPolygonCollection map, int width, int height){
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bmp);

        Paint paint = new Paint();
        paint.setColor(Color.rgb(178,150,145));

        canvas.drawRect(0,0,width, height, paint);

        return bmp;
    }
}
