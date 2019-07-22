package com.zcj.test10;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author: cj_zuo
 * Date: 2019/7/22 11:15
 */
public class Test10FileUtil {

    private static class InstanceHolder {
        private static final Handler HANDLER_HOLDER = new Handler(Test10Activity.APP.getMainLooper());
    }

    public static void saveBitmap(final int width, final int height, final ByteBuffer buffer) {
        InstanceHolder.HANDLER_HOLDER.post(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("zcjLog", "prepare save bitmap");
                        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        bitmap.copyPixelsFromBuffer(buffer);
                        saveBitmap(bitmap);
                        buffer.clear();
                    }
                }).start();
            }
        });
    }

    private static void saveBitmap(final Bitmap b) {
        String path = Test10Activity.APP.getExternalFilesDir("") + File.separator + "fbo" + File.separator;
        File folder = new File(path);
        if (!folder.exists() && !folder.mkdirs()) {
            InstanceHolder.HANDLER_HOLDER.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Test10Activity.APP.getApplicationContext(), "无法保存照片", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        long dataTake = System.currentTimeMillis();
        final String jpegName = path + dataTake + ".jpg";
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InstanceHolder.HANDLER_HOLDER.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Test10Activity.APP.getApplicationContext(), "保存成功->" + jpegName, Toast.LENGTH_LONG).show();
            }
        });
    }
}
