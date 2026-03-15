package com.mdweb.tunnumerique.tools.mdwebNetworkingLib.cache;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;

public class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
    private Context context;
    private Resources res;
    private ViewGroup view;

    private BitmapMemoryCache bitmapMemoryCache;

    public BitmapWorkerTask() {
    }

    public BitmapWorkerTask(ViewGroup view, Context context, BitmapMemoryCache bitmapMemoryCache) {
        this.view = view;
        this.bitmapMemoryCache = bitmapMemoryCache;
        this.context = context;
    }

    public void setBitmapMemoryCache(BitmapMemoryCache bitmapMemoryCache) {
        this.bitmapMemoryCache = bitmapMemoryCache;
    }

    public void setContext(Context context) {
        this.context = context;
    }



    @Override
    protected Bitmap doInBackground(Integer... params) {
        final Bitmap bitmap = decodeSampledBitmapFromResource(context.getResources(), params[0], 640, 960);
        bitmapMemoryCache.addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
        return bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onPostExecute(Bitmap bitmap) {

         if (view != null && bitmap != null) {
             view.setBackground(new BitmapDrawable(context.getResources(), bitmap));
//            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//                view.setBackgroundDrawable(new BitmapDrawable(context.getResources(), bitmap));
//            }
//            else {
//
//                view.setBackground(new BitmapDrawable(context.getResources(), bitmap));
//            }
        }
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // BEGIN_INCLUDE (read_bitmap_dimensions)
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // END_INCLUDE (read_bitmap_dimensions)


        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // BEGIN_INCLUDE (calculate_sample_size)
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            long totalPixels = width * height / inSampleSize;

            // Anything more than 2x the requested pixels we'll sample down further
            final long totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }
        return inSampleSize;
        // END_INCLUDE (calculate_sample_size)
    }
}