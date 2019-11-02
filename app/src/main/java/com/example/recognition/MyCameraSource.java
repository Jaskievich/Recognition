package com.example.recognition;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.support.annotation.RequiresPermission;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

public class MyCameraSource implements Camera.PictureCallback {
    private Camera mCamera;
    private final Context mContext;
  //  private int zzbzf;
    private final ViewfinderView focusBox;
    private final TextRecognizer textRecognizer;
    private int rotation;

    public MyCameraSource(Context mContext, ViewfinderView focusBox, TextRecognizer textRecognizer ) {
        this.mContext = mContext;
        mCamera = null;
        this.focusBox = focusBox;
        this.textRecognizer = textRecognizer;
    }

    public void release() {
        this.stop();
    }

    @RequiresPermission("android.permission.CAMERA")
    public MyCameraSource start(SurfaceHolder holder) throws IOException {
        //  mCamera = CameraSource.getCameraInstance();
        mCamera = Camera.open();

        Camera.Parameters paramCam = mCamera.getParameters();
        int[] var5 = zza(paramCam, 30.0F);
        setRotation(mCamera, paramCam, 0);
    //    zza(mCamera, paramCam);
        final Size previewSize = new Size(1280, 1024);
        paramCam.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());
        paramCam.setPreviewFpsRange(var5[0], var5[1]);
        paramCam.setPreviewFormat(17);

        //   paramCam.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        if (paramCam.getSupportedFocusModes().contains("continuous-video")) {
            paramCam.setFocusMode("continuous-video");
        } else {
            Log.i("CameraSource", "Camera auto focus is not supported on this device.");
        }

        mCamera.setParameters(paramCam);
        mCamera.setPreviewDisplay(holder);
        mCamera.startPreview();
        return this;
    }

    public void stop() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

    }

    private static int[] zza(Camera.Parameters var0, float var1) {
        int var2 = (int) (var1 * 1000.0F);
        int[] var3 = null;
        int var4 = 2147483647;
        List var5 = var0.getSupportedPreviewFpsRange();
        Iterator var6 = var5.iterator();

        while (var6.hasNext()) {
            int[] var7 = (int[]) var6.next();
            int var8 = var2 - var7[0];
            int var9 = var2 - var7[1];
            int var10 = Math.abs(var8) + Math.abs(var9);
            if (var10 < var4) {
                var3 = var7;
                var4 = var10;
            }
        }
        return var3;
    }

   //*
    private void setRotation(Camera camera, Camera.Parameters parameters, int cameraId) {
        WindowManager windowManager =
                (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int degrees = 0;
        int rotation = windowManager.getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                Log.e("CameraSource", "Bad rotation value: " + rotation);
        }

        CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);

        int angle;
        int displayAngle;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            angle = (cameraInfo.orientation + degrees) % 360;
            displayAngle = (360 - angle); // compensate for it being mirrored
        } else {  // back-facing
            angle = (cameraInfo.orientation - degrees + 360) % 360;
            displayAngle = angle;
        }

        // This corresponds to the rotation constants in {@link Frame}.
        this.rotation = angle / 90;

        camera.setDisplayOrientation(displayAngle);
        parameters.setRotation(angle);
    }
  //  */


    public  void takePicture() {

            try {
                mCamera.takePicture(null, null, this);
            }catch(RuntimeException e){
                e.printStackTrace();
            }

    }


//    void SaveToFile(Bitmap bitmap, String file) {
//        try {
//            File saveDir = new File("/sdcard/CameraExample/");
//
//            if (!saveDir.exists()) {
//                saveDir.mkdirs();
//            }
//            FileOutputStream fos = new FileOutputStream("/sdcard/CameraExample/" + file /*"d.jpg"*/);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
//
//            fos.flush();
//            fos.close();
//        } catch (Exception e) {
//            Log.e("MyLog", e.toString());
//        }
//    }

    private ByteBuffer Bitmap2Bytebuffer(Bitmap mBitmap, Rect rct) {

        int[] arrInt = new int[rct.width() * rct.height()];
        mBitmap.getPixels(arrInt, 0, rct.width(), rct.left, rct.top, rct.width(), rct.height());
        byte[] arrByte = new byte[rct.width() * rct.height()];

        for (int i = 0; i < arrInt.length; ++i) {
            arrByte[i] = (byte) ((int) ((float) Color.red(arrInt[i]) * 0.299F + (float) Color.green(arrInt[i]) * 0.587F + (float) Color.blue(arrInt[i]) * 0.114F));
        }
        return ByteBuffer.wrap(arrByte);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

            Bitmap textBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            int w = textBitmap.getWidth();
            int h = textBitmap.getHeight();
            RectF rectf = focusBox.getNormBox();
            Rect rect = new Rect();

            rect.left = (int) (w * rectf.left);
            rect.right = (int) (w * rectf.right);
            rect.top = (int) (h * rectf.top);
            rect.bottom = (int) (h * rectf.bottom);

            ByteBuffer buffer = Bitmap2Bytebuffer(textBitmap, rect);

            textBitmap.recycle();


            try {
                Frame  var1 = (new Frame.Builder()).setImageData(buffer, rect.width(), rect.height(), 17).build();

                textRecognizer.receiveFrame(var1);

            } catch (IllegalStateException var11) {
                var11.printStackTrace();
            }

            //    textRecognizer.detect()
            //   SaveToFile(bmUpRightPartial, "d1.jpg");
            //   SaveToFile(textBitmap, "d2.jpg");

            camera.startPreview();

    }


}

