package com.mdweb.tunnumerique.tools.mdwebNetworkingLib;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


public class Volley {
    private static Volley mInstance;
    private RequestQueue mRequestQueue;

    public RequestQueue getRequestQueueImage() {
        return requestQueueImage;
    }

    private RequestQueue requestQueueImage;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    public DisplayImageOptions getDefaultOptions() {
        return defaultOptions;
    }

    private DisplayImageOptions defaultOptions;

    private Volley(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized Volley getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Volley(context);
        }
        return mInstance;
    }

    public RequestQueue getmRequestQueueImage() {
        if (requestQueueImage == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueueImage = com.android.volley.toolbox.Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return requestQueueImage;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = com.android.volley.toolbox.Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}