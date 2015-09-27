OCR Framework
=======================

<p align="left" >
  <img src="http://www.aumentia.com/images/sdks/ocrsdk@2x.png" width="415" alt="Aumentia" title="Aumentia">
</p>

* Real time OCR

<br>
******************
Setup
******************

Android Studio Version 1.3.2

1. Import the OCRAumentia.aar file

```
File -> New Module -> Import .JAR / .AAR Package
```

2. Add the new Module (**OCRAumentia**) as a dependency:

```
Right click on Project Name -> Open Module Settings -> Dependencies -> + -> Module Dependency -> OCRAumentia -> OK
```

<br>
******************
Init the framework
******************


```
// Get singleton instance
ocrAumentia = OCRAumentia.getmInstance();
```

Use the build in camera to real time analyse the output frames:

```
// Get singleton instance
// Init Text Recognition engine
ocrAumentia.init(this, API_KEY, OCRAumentia.SCREEN_ORIENTATION_PORTRAIT, PreviewSizeWidth, PreviewSizeHeight, true, ImageFormat.NV21, frame, 400);
```

Or analyse single images from the resources, assets, URL, etc

```
// Get singleton instance
// Init Text Recognition engine
ocrAumentia.init(this, API_KEY, 0);

…

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

```

<br>
******************
Implement OnTextRecognition methods
******************

Get a list with the matched words and their confidence:


```
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
```

For debug purpose you can also get the analysed frame with bounding boxes surrounding the matched words:

```
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
```

** Check the API doc for more info **


<br>
******************
API
******************
[api.aumentia.com](http://api.aumentia.com/ocr_android/)

<br>
******************
Min Android Version
******************
14

<br>
*************************
OCR Framework version
*************************
0.5


<br>
******************
License
******************
[LICENSE](https://github.com/aumentia/OCR-Aumentia-Android/blob/master/LICENSE)

<br>
******************
Bugs
******************
[Issues & Requests](https://github.com/aumentia/OCR-Aumentia-Android/issues)
