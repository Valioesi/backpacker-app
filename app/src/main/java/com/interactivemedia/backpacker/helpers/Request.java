package com.interactivemedia.backpacker.helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Vali on 26.11.2017.
 * this helper class serves as a mean to make http request using HttpUrlConnection
 * for now it includes get and post requests
 */

public class Request {

    private static final String DOMAIN_URL = "http://192.168.0.15:3000";
    /**
     * this function can be used to perform a get request to a server
     *
     * @param endpoint -> String of the REST endpoint, is added to our URL
     * @return the server response as String
     */
    public static String get(String endpoint) {
        String response = "";
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(DOMAIN_URL + endpoint);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            response = readStream(urlConnection.getInputStream());
            urlConnection.disconnect();
        }  catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();

            }
        }
        return response;
    }


    /**
     * this function can be used to perform a post request to a server
     *
     * @param endpoint -> String of the REST endpoint, is added to our URL
     * @param body      as string in JSON format
     * @return response as string
     */
    public static String post(String endpoint, String body) {
        String response = "";
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(DOMAIN_URL + endpoint);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type",
                    "application/json");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream outputStream = urlConnection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(body);
            bufferedWriter.flush();

            response = readStream(urlConnection.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
        }
        return response;
    }

    /**
     * this method converts the inputstream to a string
     *
     * @param in is the inputstream
     * @return the response as String
     */
    private static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }
}



