package com.example.firstapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class BitmapUtils {

    public static Bitmap imageToBitmap(Image image) {
        if (image == null) {
            Log.e("BitmapUtils", "Image is null");
            return null;
        }

        try {
            // Get planes
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer yBuffer = planes[0].getBuffer();
            ByteBuffer uBuffer = planes[1].getBuffer();
            ByteBuffer vBuffer = planes[2].getBuffer();

            // Get sizes of the buffers
            int ySize = yBuffer.remaining();
            int uSize = uBuffer.remaining();
            int vSize = vBuffer.remaining();

            // Create NV21 byte array to store the YUV data
            byte[] nv21 = new byte[ySize + uSize + vSize];

            // Copy Y, U, and V data into the NV21 array
            yBuffer.get(nv21, 0, ySize);
            vBuffer.get(nv21, ySize, vSize);  // Corrected order (V comes before U in NV21 format)
            uBuffer.get(nv21, ySize + vSize, uSize);  // U follows V in NV21 format

            // Convert YUV to Bitmap
            YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21,
                    image.getWidth(), image.getHeight(), null);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 90, out);
            byte[] imageBytes = out.toByteArray();

            // Convert byte array to Bitmap
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } catch (Exception e) {
            Log.e("BitmapUtils", "YUV to Bitmap conversion failed", e);
            return null;
        } finally {
            // Close the image to prevent memory leaks
            image.close();
        }
    }
}
