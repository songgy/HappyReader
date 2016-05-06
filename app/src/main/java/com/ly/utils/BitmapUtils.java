package com.ly.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * 图片二次处理
 */
public class BitmapUtils {

    /**
     * 将图片转化为圆形图片
     * @param source  原始图片
     * @return
     */
    public static Bitmap getCircleBimap(Bitmap source) {
        //获取宽高中的最小值
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        //将中心区域裁剪出一个正方形
        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);  //这样截图出来是个正方形
        if (squaredBitmap != source) {
            source.recycle();
        }
        //新建一个空的bitmap
        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        //将图片绘制的空余部分以边缘延伸
        BitmapShader shader = new BitmapShader(squaredBitmap,
                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        //设置画笔
        paint.setShader(shader);
        //设置抗锯齿
        paint.setAntiAlias(true);
        //设置圆的半径
        float r = size / 2f;
        //画圆
        canvas.drawCircle(r, r, r, paint);
        //图片回收
        squaredBitmap.recycle();
        System.gc();
        return bitmap;
    }

    /**
     * 将图片转化为带圆角的图片
     * @param bitmap 原始图片
     * @param pixels  角度
     * @return
     */
    public static Bitmap getRoundCorner(Bitmap bitmap, int pixels) {     //第一个就是转换的图片   第二个：圆角的弧度
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        System.gc();
        return output;
    }

}
