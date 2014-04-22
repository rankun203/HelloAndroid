package org.rankun.helloquotes.net;

import android.util.Log;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by rankun203 on 14-4-21
 */
public class GetDataEx1 {
    private static final String DEBUG_TAG = "HelloLogin";

    /**
     * connect to the url, then read a line and return.
     * @param urlStr the url
     * @return retrieved data
     * @throws Exception
     */
    public JSONObject getDataStr(String urlStr) throws Exception {
        JSONObject dataObj = null;
        InputStream is = null;
        BufferedReader br = null;
        HttpURLConnection conn = null;

        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            Log.d(DEBUG_TAG, "Connecting to: " + urlStr);
            conn.connect();

            int responseCode = conn.getResponseCode();
            Log.d(DEBUG_TAG, "This response is: " + responseCode);

            if (200 == responseCode) {
                is = conn.getInputStream();

                br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String l;
                while ((l = br.readLine()) != null) {
                    sb.append(l);
                }

                String sbStr = sb.toString();
                JSONTokener tok = new JSONTokener(sbStr);
                Object nextTok = tok.nextValue();
                if (nextTok instanceof JSONObject == true) {
                    dataObj = (JSONObject) nextTok;
                } else {
                    Log.e(DEBUG_TAG, "Wrong JSONObject format: " + nextTok.getClass());
                    Log.d(DEBUG_TAG, "Response = " + sbStr);
                }
            } else {
                Log.e(DEBUG_TAG, "Wrong response code: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            dataObj = null;
        } finally {
            if (null != conn) {
                conn.disconnect();
            }
            if (null != br) {
                br.close();
            }
            if (null != is) {
                is.close();
            }
        }

        return dataObj;
    }
}
