package com.silver.dan.castdemo.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.silver.dan.castdemo.FileSavedListener;
import com.silver.dan.castdemo.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class ImageUtils {

    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
        ExifInterface ei = new ExifInterface(image_absolute_path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static File getImagePath(Context context, String imgName) {
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        return new File(directory, imgName);
    }

//    public static void saveToInternalStorage(final Bitmap bitmapImage, final String imageName, final Context context, final FileSavedListener callback) {
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                FileOutputStream fos = null;
//                try {
//                    fos = new FileOutputStream(getImagePath(context, imageName));
//                    // Use the compress method on the BitMap object to write image to the OutputStream
//                    bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        if (fos != null) {
//                            fos.close();
//                        }
//                        callback.onSaved();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        callback.onError("Upload failed, try again later.");
//                    }
//                }
//            }
//        });
//    }

    public static String getS3ImageURL(String imageName, Context context, AmazonS3Client s3Client) {
        // get a url to access the image
        ResponseHeaderOverrides override = new ResponseHeaderOverrides();
        override.setContentType( "image/jpeg" );

        GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest( context.getString(R.string.BACKGROUND_PICTURE_BUCKET), imageName);
        urlRequest.setExpiration( new Date( System.currentTimeMillis() + 3600000 ) );  // Added an hour's worth of milliseconds to the current time.
        urlRequest.setResponseHeaders( override );

        return s3Client.generatePresignedUrl( urlRequest ).toString();
    }
}