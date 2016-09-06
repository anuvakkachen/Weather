package phunware.weather.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Helper class to send HTTP requests
 *
 * @author Anu
 */
public class HttpHelper {

    private static final String HTTP_GET = "GET";

    public HttpHelper() {
    }

    /**
     * Send an HTTP GET request
     *
     * @param request Request to be sent
     * @return the response string received from server.
     * @throws IOException If an error occurs while sending the request and getting response
     */
    public String sendGetRequest(String request) throws IOException {
        HttpURLConnection httpConn = null;
        BufferedReader bReader = null;
        String response = null;
        try {
            URL url = new URL(request);
            //open connection and send request
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod(HTTP_GET);

            //read response
            InputStream inStream = httpConn.getInputStream();
            if (inStream == null) {
                throw new IOException("Error getting weather data");
            }

            bReader = new BufferedReader(new InputStreamReader(inStream));

            String singleResponseLine = null;
            StringBuilder responseBuilder = new StringBuilder();

            do {
                singleResponseLine = bReader.readLine();

                if (singleResponseLine != null) {
                    responseBuilder.append(singleResponseLine);
                    responseBuilder.append("\n");//Add a new line
                }

            } while (singleResponseLine != null);

            response = responseBuilder.toString();
        } catch (MalformedURLException e) {
            throw e;
        } finally {
            if (bReader != null) {
                bReader.close();
            }

            if (httpConn != null) {
                httpConn.disconnect();
            }
        }
        return response;
    }//end method sendGetRequest


    /**
     * Create a bitmap object from a url string
     *
     * @param urlString url as a string
     * @return Bitmap created from the url content
     * @throws IOException if any error occurs
     */
    public Bitmap getBitMap(String urlString) throws IOException {
        InputStream inputStream = null;
        Bitmap bitMap = null;
        try {
            URL url = new URL(urlString);
            inputStream = (InputStream) url.getContent();
            bitMap = BitmapFactory.decodeStream(inputStream);
        } catch (MalformedURLException e) {
            throw e;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return bitMap;
    }//end getBitMap


}//end class HttpHelper
