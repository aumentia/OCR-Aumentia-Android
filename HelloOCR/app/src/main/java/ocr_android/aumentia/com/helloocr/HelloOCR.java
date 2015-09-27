package ocr_android.aumentia.com.helloocr;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.aumentia.ocraumentia.OnTextRecognition;
import com.aumentia.ocraumentia.OCRAumentia;


import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;


public class HelloOCR extends Activity implements OnTextRecognition
{
    //--- GLOBAL VARIABLES -------------------------------------------------------------------------

    // Layout to place the camera
    private FrameLayout frame;

    // VS instance
    private OCRAumentia ocrAumentia;

    // App API_KEY
    private static final String     API_KEY     = "2f84eef79d20550d96b64f57a175a53b6ac2e043";

    // Log debug tag
    public static final String     HELLO_TAG    = "HelloOCR";

    // Draw output view
    private Bitmap outputBitmap                 = null;
    private ImageView mCustomCameraView         = null;

    private int     PreviewSizeWidth;
    private int     PreviewSizeHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_hello_ocr);

        // Get full screen size
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        PreviewSizeWidth   = dm.widthPixels;
        PreviewSizeHeight  = dm.heightPixels;

        // FrameLayout where we will add the camera view
        frame = (FrameLayout) findViewById(R.id.cameraFrameId);

        // Get singleton instance
        ocrAumentia = OCRAumentia.getmInstance();

        /*** DEMO1: REAL TIME TEXT RECOGNITION ***/
        // Init Text Recognition engine
        ocrAumentia.init(this, API_KEY, OCRAumentia.SCREEN_ORIENTATION_PORTRAIT, PreviewSizeWidth, PreviewSizeHeight, true, ImageFormat.NV21, frame, 400);

        /*** DEMO2: ANALYSE A PICTURE FROM THE ASSETS FOLDER ***/
        // Init Text Recognition engine
        //ocrAumentia.init(this, API_KEY, 0);

        // Set delegate
        ocrAumentia.setOnTextRecognitionCallback(this);
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    @Override
    public void onStart()
    {
        super.onStart();

        Log.d(HELLO_TAG, "*** onStart() *** ");
    }

    @Override
    public void onResume()
    {
        super.onResume();

        /*** DEMO1: REAL TIME TEXT RECOGNITION ***/
        ocrAumentia.start();

        /*** DEMO2: ANALYSE A PICTURE FROM THE ASSETS FOLDER ***/
        //processImageFromAssets();

        Log.d(HELLO_TAG, "*** onResume() *** ");
    }

    @Override
    public void onStop()
    {
        super.onStop();

        Log.d(HELLO_TAG, "*** onStop() ***");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        // UnRegister callback
        ocrAumentia.setOnTextRecognitionCallback( null );

        ocrAumentia.stop();

        ocrAumentia.release();

        frame.removeView(mCustomCameraView);

        mCustomCameraView   = null;

        System.gc();

        Log.d(HELLO_TAG, "*** onDestroy() ***");
    }

    @Override
    public void onPause()
    {
        super.onPause();

        ocrAumentia.stop();

        ocrAumentia.release();

        Log.d(HELLO_TAG, "*** onPause() ***");
    }

    @Override
    public void matchedWords(Map<String, Integer> wordConfidenceMap)
    {
        Iterator<Map.Entry<String, Integer>> iterator = wordConfidenceMap.entrySet().iterator();

        while (iterator.hasNext())
        {
            Map.Entry<String,Integer> pairs = iterator.next();
            Integer value   =  pairs.getValue();
            String key      = pairs.getKey();

            Log.d(HELLO_TAG, key + "--->" + value);
        }
    }

    @Override
    public void imageResult(int[] outputData, int width, int height, boolean isRotated)
    {
        if (outputData != null)
        {
            if ( mCustomCameraView == null )
            {
                mCustomCameraView = new ImageView(this);

                frame.addView(mCustomCameraView, new ViewGroup.LayoutParams(width, height));
            }

            if ( outputBitmap != null )
            {
                outputBitmap.recycle();
                outputBitmap = null;
            }

            outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            // Bitmap from output data
            outputBitmap.setPixels(outputData, 0, width, 0, 0, width, height);

            if (outputBitmap != null)
            {
                if ( !isRotated )
                {
                    Matrix matrix = new Matrix();

                    matrix.postRotate(90);

                    Bitmap rotatedBitmap = Bitmap.createBitmap(outputBitmap, 0, 0, outputBitmap.getWidth(), outputBitmap.getHeight(), matrix, true);

                    mCustomCameraView.setImageBitmap(rotatedBitmap);
                }
                else
                {
                    mCustomCameraView.setImageBitmap(outputBitmap);
                }
            }
        }
    }

    /**
     * Analyse image from assets folder and extract recognised text
     */
    private void processImageFromAssets()
    {
        AssetManager assetManager = getAssets();

        InputStream istr;

        Bitmap bitmap       = null;

        try
        {
            istr    = assetManager.open("pic1.jpg");
            bitmap  = BitmapFactory.decodeStream(istr);
            // Make sure the image is decoded in the ARGB_8888 format
            bitmap  = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        }
        catch (IOException e)
        {
            Log.e(HELLO_TAG, e.getMessage());
        }

        if ( bitmap != null )
        {
            ocrAumentia.processFrame(bitmap, false);
        }
    }
}
