package com.zetaflame.testhandmark;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.core.Delegate;
import com.google.mediapipe.tasks.core.OutputHandler;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker;
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import kotlin.collections.CollectionsKt;

public class openPipeLlHelper {
    private static openPipeLlHelper instance;
    private  final String TAG = "OpenPipe";
    private  HandLandmarker handLandmarker = null;
    private  LandmarkerListener handlandMarkListener = null;
    private  final String MP_HAND_LANDMARKER_TASK = "hand_landmarker.task";
    //private static ExecutorService backgroundExec = null;
    private ScheduledExecutorService schedBack;
    private  Bitmap finalImg = null;
    private  OutputHandler.ResultListener<HandLandmarkerResult, MPImage> testMeth;
    private int width = 0;
    private int height = 0;
    private int[] bufferData = null; // Store bufferData
    private final Object bufferDataLock = new Object();

    private openPipeLlHelper() {
        // Private constructor to prevent external instantiation
    }

    public static openPipeLlHelper getInstance() {
        if (instance == null) {
            instance = new openPipeLlHelper();
        }
        return instance;
    }

    private  void detectImage(Bitmap image)
    {
        if (getBufferData() == null)
            return;
		Log.e(TAG, "start: detectImage ");


        int[] BuffData = getBufferData();

        long startTime = SystemClock.uptimeMillis();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int[] pixels = new int[width * height];
        for (int i = 0; i < width * height; i++) {
            // Convert pixel data to Color object
            pixels[i] = Color.rgb(Color.red(BuffData[i]), Color.green(BuffData[i]), Color.blue(BuffData[i]));
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        MPImage mpImage = new BitmapImageBuilder(bitmap).build();
		Log.e(TAG, "before try: detectImage ");
        // Convert the input Bitmap object to an MPImage object to run inference
        //MPImage mpImage = new BitmapImageBuilder(finalImg).build();
        try {

            //HandLandmarkerResult res =
            handLandmarker.detectAsync(mpImage,startTime);
            //float value = res.landmarks().get(0).get(0).x();
            Log.e(TAG, "detectImage: ");
        }catch (Throwable e)
        {
            Log.e(TAG, "didn't work pipe");
        }
		Log.e(TAG, "end: detectImage ");

    }


    public  void start(Activity activity)
    {
        openPipeLlHelper newOP = new openPipeLlHelper();
        Log.e(TAG, "start: starts");
        BaseOptions.Builder baseOptionsBuilder = BaseOptions.builder();

        //todo: test out GPU.
        baseOptionsBuilder.setDelegate(Delegate.CPU);

        baseOptionsBuilder.setModelAssetPath(MP_HAND_LANDMARKER_TASK);



        try{
            Log.e(TAG, "baseOpt start");
            BaseOptions baseB = baseOptionsBuilder.build();

            HandLandmarker.HandLandmarkerOptions optB = HandLandmarker.HandLandmarkerOptions.builder()
                    .setBaseOptions(baseB)
                    .setMinHandDetectionConfidence(0.5f)
                    .setMinTrackingConfidence(0.5f)
                    .setMinHandPresenceConfidence(0.5f)
                    .setNumHands(1)
                    .setRunningMode(RunningMode.LIVE_STREAM)
                    .setResultListener(this::returnLivestreamResults)
                    .setErrorListener(this::returnLivestreamError)
                    .build();


            //HandLandmarker.HandLandmarkerOptions.Builder options = optB;

            handLandmarker = HandLandmarker.createFromOptions(activity.getApplicationContext(), optB);
            Log.e(TAG, "baseOpt end");
        }catch(IllegalStateException e){
            Log.e(TAG, "hand no build:   " + e);
            throw e;
        }catch (RuntimeException e) {
            Log.e(TAG, "hand no build:   " + e);
        }


        try {
            Log.e(TAG, "hand start");
            AssetManager assetManager = activity.getApplicationContext().getAssets();
            InputStream inputStream = null;
            inputStream = assetManager.open("hand.jpg");
            Bitmap bitmap;
            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(assetManager,"hand.jpg"));
            finalImg = bitmap.copy(Bitmap.Config.ARGB_8888,true);
            inputStream.close();
            Log.e(TAG, "hand: end");
        } catch (IOException | OutOfMemoryError e) {
            throw new RuntimeException(e);
        }
        Log.e(TAG, "hand: post");

        Log.e(TAG, "start: endworks");
        startBackgroundTask();
        //backgroundExec = Executors.newSingleThreadExecutor();

//        backgroundExec.execute((Runnable)(new Runnable() {
//            @Override
//            public void run() {
//                Log.e(TAG, "runing: run");
//            }
//        }));
    }
     Executor runOnUiThreadExecutor() {
        return new MyExecutor();
    }

     class MyExecutor implements Executor {
        Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            handler.post(command);
        }


    }

    private  void startBackgroundTask() {
        Log.e(TAG, "start: backTask");
        schedBack = Executors.newSingleThreadScheduledExecutor();

        // Schedule the task to run every second
        schedBack.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // Your background logic here
                // This example prints "Hello" every second
                detectImage(finalImg);
                Log.e(TAG, "runing:Back");
            }
        }, 0, 1, TimeUnit.SECONDS);
        Log.e(TAG, "end: backTask");

    }

    public  class handMarkData {
        public  final int DELEGATE_CPU = 0;
        public  final int DELEGATE_GPU = 1;
        public  final float DEFAULT_HAND_DETECTION_CONFIDENCE = 0.5F;
        public  final float DEFAULT_HAND_TRACKING_CONFIDENCE = 0.5F;
        public  final float DEFAULT_HAND_PRESENCE_CONFIDENCE = 0.5F;
        public  final int DEFAULT_NUM_HANDS = 1;
        public  final int OTHER_ERROR = 0;
        public  final int GPU_ERROR = 1;
    }

    public  final class ResultBundle {
        public List results;
        public long inferenceTime;
        public int inputImageHeight;
        public int inputImageWidth;
        ResultBundle(){

        }
    }
    public interface LandmarkerListener{
        void onError(String error, int errorCode);
        void onResults(ResultBundle resultBundle);

    }

    private final void returnLivestreamError(RuntimeException error)
    {
        if (handlandMarkListener != null) {
            handlandMarkListener.onError("An unknown error has occurred" + error, 1);
        }
    }

    public synchronized void setBufferData(int[] bufferData) {
        synchronized (bufferDataLock) {
            this.bufferData = bufferData;
        }
    }

    public synchronized int[] getBufferData() {
        synchronized (bufferDataLock) {
            if (bufferData  != null)
                return bufferData;

            return null;
        }
    }

    //public static native void PassBufferData(int[] bufferData);

    // Method to be called from Unreal Engine to pass buffer data
    public void PassBufferDataFromUE(int[] bufferData, int _width, int _height) {
        if (width != _width) {
            width = _width;
            height = _height;
        }
        setBufferData(bufferData);
//        for (int i = 0; i < bufferData.length; i++) {
//            Log.e("PassBufferData", "bufferData[" + i + "] = " + bufferData[i]);
//        }
//        long startTime = SystemClock.uptimeMillis();
//        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        int[] pixels = new int[width * height];
//        for (int i = 0; i < width * height; i++) {
//            // Convert pixel data to Color object
//            pixels[i] = Color.rgb(Color.red(bufferData[i]), Color.green(bufferData[i]), Color.blue(bufferData[i]));
//        }
//        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//
//        MPImage mpImage = new BitmapImageBuilder(bitmap).build();
//        try {
//
//            //HandLandmarkerResult res =
//            handLandmarker.detectAsync(mpImage,startTime);
//            //float value = res.landmarks().get(0).get(0).x();
//            Log.e(TAG, "detectImage: ");
//        }catch (Throwable e)
//        {
//            Log.e(TAG, "didn't work pipe");
//        }

        //PassBufferData(bufferData);
    }

    private  OutputHandler.ResultListener<HandLandmarkerResult, MPImage> returnLivestreamResults(HandLandmarkerResult result, MPImage input)
    {
        Log.e(TAG, "returnLivestreamResults: "+ result.landmarks().get(0).get(0).x());
//        ResultBundle newRes = new ResultBundle();
//        newRes.results = CollectionsKt.listOf(result);
//        long finishTimeMs = SystemClock.uptimeMillis();
//        newRes.inferenceTime = finishTimeMs - result.timestampMs();
//        newRes.inputImageHeight = input.getHeight();
//        newRes.inputImageWidth = input.getWidth();
//        handlandMarkListener.onResults(newRes);
        return null;
    }
//    private final void returnLivestreamError(RuntimeException error) {
//        LandmarkerListener var10000 = this.handLandmarkerHelperListener;
//        if (var10000 != null) {
//            String var10001 = error.getMessage();
//            if (var10001 == null) {
//                var10001 = "An unknown error has occurred";
//            }
//
//            HandLandmarkerHelper.LandmarkerListener.DefaultImpls.onError$default(var10000, var10001, 0, 2, (Object)null);
//        }
//
//    }
}


