package com.bitwindow.popularmovies;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class to fetch data from server
 */
class FetchInternetDataTask extends AsyncTask<URL, Void, String> {

    private final String LOG_TAG = FetchInternetDataTask.class.getSimpleName();
    private static final boolean DEBUG = false; // Set this to false to disable logs.
    private final AsyncTaskCompleteListener<String> mlistener;
    private Exception mexception = null;

    public FetchInternetDataTask(AsyncTaskCompleteListener<String> listener)
    {
        this.mlistener = listener;
    }

    @Override
    protected String doInBackground(URL... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        URL url;
        try {

 //To test the behavior when screen orientation changes
//                for(int i=0;i<=4;i++){
//                    Thread.sleep(1000);
//                    Log.i(LOG_TAG, "doInBackground " + Integer.toString(i));
//                    if(isCancelled()){
//                        return null;
//                    }
//                }

            if (DEBUG) Log.i(LOG_TAG, "doInBackground()");
            if(params.length>0){
                url = params[0];

            } else{
                throw new IllegalArgumentException();

            }


            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(5000);
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                throw new IllegalStateException();

            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            return buffer.toString();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error ", e);
            mexception = e;


        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        // This will only happen if there was an error getting or parsing the data.
        return null;
    }
    @Override
    protected void onPreExecute(){
        mlistener.onTaskBefore();

    }
    @Override
    protected void onPostExecute(String result) {
        if(mexception != null){
            mlistener.onExceptionRaised(mexception);

        } else {
            mlistener.onTaskComplete(result);
        }
    }


}
