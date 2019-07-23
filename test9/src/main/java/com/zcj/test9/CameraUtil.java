package com.zcj.test9;

import android.hardware.Camera;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * @author: cj_zuo
 * Date: 2019/7/23 8:56
 */
public class CameraUtil {

    public static void setPropSize(Camera camera, int viewWidth, int viewHeight) {
        Camera.Parameters parameters = camera.getParameters();
        int[] proPreviewSize = getPropPreviewSize(camera, viewWidth, viewHeight);
        int[] proPictureSize = getPropPictureSize(camera, viewWidth, viewHeight);
        parameters.setPreviewSize(proPreviewSize[0], proPreviewSize[1]);
        parameters.setPictureSize(proPictureSize[0], proPictureSize[1]);
        camera.setParameters(parameters);
    }

    private static int[] getPropPreviewSize(Camera camera, int viewWidth, int viewHeight) {
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        Collections.sort(supportedPreviewSizes, new CameraSizeComparator());
        int i;
        float viewRatio = (float) viewWidth / viewHeight;
        //根据长宽比、最小宽度来获取最佳预览尺寸
        //1、去掉太小的预览尺寸
        Iterator<Camera.Size> iterator = supportedPreviewSizes.iterator();
        while (iterator.hasNext()) {
            Camera.Size next = iterator.next();
            if (next.height < viewWidth) {
                iterator.remove();
            }
        }
        //2、在剩余的预览尺寸中选取与可视区域最接近的长款比例
        Collections.sort(supportedPreviewSizes, new CameraSizeRatioComparator());
        for (i = 0; i < supportedPreviewSizes.size(); i++) {
            if (Math.abs(ratioDiff(supportedPreviewSizes.get(i), viewRatio)) < 0.1f) {
                break;
            }
            if (i < supportedPreviewSizes.size() - 1) {
                if (ratioDiff(supportedPreviewSizes.get(i), viewRatio) <= 0
                        && ratioDiff(supportedPreviewSizes.get(i + 1), viewRatio) >= 0) {
                    break;
                }
            }
        }
        if (i == supportedPreviewSizes.size()) {
            i = 0;
        }
        return new int[]{supportedPreviewSizes.get(i).width, supportedPreviewSizes.get(i).height};
    }

    private static int[] getPropPictureSize(Camera camera, int viewWidth, int viewHeight) {
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPictureSizes();
        Collections.sort(supportedPreviewSizes, new CameraSizeComparator());
        int i;
        float viewRatio = (float) viewWidth / viewHeight;
        //根据长宽比、最小宽度来获取最佳预览尺寸
        //1、去掉太小的预览尺寸
        Iterator<Camera.Size> iterator = supportedPreviewSizes.iterator();
        while (iterator.hasNext()) {
            Camera.Size next = iterator.next();
            if (next.height <= viewWidth) {
                iterator.remove();
            }
        }
        //2、在剩余的预览尺寸中选取与可视区域最接近的长款比例
        Collections.sort(supportedPreviewSizes, new CameraSizeRatioComparator());
        for (i = 0; i < supportedPreviewSizes.size(); i++) {
            if (Math.abs(ratioDiff(supportedPreviewSizes.get(i), viewRatio)) < 0.1f) {
                break;
            }
            if (i < supportedPreviewSizes.size() - 1) {
                if (ratioDiff(supportedPreviewSizes.get(i), viewRatio) <= 0
                        && ratioDiff(supportedPreviewSizes.get(i + 1), viewRatio) >= 0) {
                    break;
                }
            }
        }
        if (i == supportedPreviewSizes.size()) {
            i = 0;
        }
        return new int[]{supportedPreviewSizes.get(i).width, supportedPreviewSizes.get(i).height};
    }

    private static float ratioDiff(Camera.Size size, float ratio) {
        float sizeRatio = (float) size.width / size.height;
        return sizeRatio - ratio;
    }

    private static class CameraSizeComparator implements Comparator<Camera.Size> {

        @Override
        public int compare(Camera.Size o1, Camera.Size o2) {
            return o1.height - o2.height;
        }
    }

    private static class CameraSizeRatioComparator implements Comparator<Camera.Size> {

        @Override
        public int compare(Camera.Size o1, Camera.Size o2) {
            return (int) (((float) o1.width / o1.height) * 100 - ((float) o2.width / o2.height) * 100);
        }
    }
}
