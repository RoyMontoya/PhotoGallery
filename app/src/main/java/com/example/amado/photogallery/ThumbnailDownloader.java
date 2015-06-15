package com.example.amado.photogallery;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.GpsStatus;
import android.net.sip.SipSession;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.support.v4.util.LruCache;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Amado on 07/05/2015.
 */
public class ThumbnailDownloader<Token> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private static final int MESSAGE_CACHE = 1;

    LruCache mCache;

    Handler mHandler;
    Map<Token, String> requestMap= Collections.synchronizedMap(new HashMap<Token, String>());
    Handler mResponseHandler;
    Listener<Token> mListener;
    public int mTotal;

    public int getTotal() {
        return mTotal;
    }

    public interface Listener<Token>{
        void onThumbnailDownloaded(Token token, Bitmap thumbnail);
    }


    public void setListener(Listener<Token> listener){
        mListener= listener;
    }

    public ThumbnailDownloader(Handler responseHandler){
        super(TAG);
        mResponseHandler = responseHandler;

        int cacheSize = 4*1024*1024;
        mCache =  new LruCache(cacheSize);
    }
    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == MESSAGE_DOWNLOAD){
                    @SuppressWarnings("unchecked")
                            Token token = (Token)msg.obj;
                            Log.i(TAG, "Got a request for url: "+ requestMap.get(token));
                            handleRequest(token);
                }else if(msg.what== MESSAGE_CACHE){
                    @SuppressWarnings("unchecked")
                            String url = (String)msg.obj;
                    Log.i(TAG, "Got a cache request for url "+url);
                    handleRequest(url);
                }
            }
        };
    }

    public void queueThumbnail(Token token, String url) {
        if(url==null) return;
        Log.i(TAG, "got an URL: " + url);
        mTotal++;
        requestMap.put(token, url);

        mHandler.obtainMessage(MESSAGE_DOWNLOAD, token)
                .sendToTarget();
    }

    public void queueThumbnail(String url) {
        mHandler.obtainMessage(MESSAGE_CACHE, url)
                .sendToTarget();
    }



    private void handleRequest(final Token token){

        try {
            final String url = requestMap.get(token);
            if (url == null) return;

            final Bitmap bitmap;
            if(mCache.get(url) == null) {

                byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
                bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                mCache.put(url, bitmap);
                Log.i(TAG, "Bitmap created");
            }else{
                bitmap=(Bitmap)mCache.get(url);
            }

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(requestMap.get(token)!= url)return;

                    requestMap.remove(token);
                    mListener.onThumbnailDownloaded(token, bitmap);

                }
            });

        }catch (IOException ioe){
            Log.e(TAG, "Error downloading image", ioe);
    }
    }

    private void handleRequest(String url){
        try{
            final Bitmap bitmap;
            if(mCache.get(url)==null){
                byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
                bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                mCache.put(url, bitmap);
            }
        }catch (IOException e){
            Log.i(TAG,"Error downloading image", e);
        }
    }

    public void clearQueue(){
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();
    }
}
