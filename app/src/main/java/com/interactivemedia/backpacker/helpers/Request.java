package com.interactivemedia.backpacker.helpers;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Vali on 26.11.2017.
 * this helper class serves as a mean to make http request using HttpUrlConnection
 * for now it includes get and post requests, as well as a function to upload images
 */

public class Request {

    private static final String DOMAIN_URL = "http://192.168.178.71:3000/api/v0";
    public static final String IMAGES_URL = "http://192.168.178.71:3000/uploads/imgs";

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
            //if the status code is anything else but 200, we want to return something different,
            //which can be handled in our AsyncTasks
            if (urlConnection.getResponseCode() != 200) {
                return "error";
            }
            response = readStream(urlConnection.getInputStream());
            urlConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();

            }
        }
        return response;
    }

    /**
     * this function performs a post request -> calls postOrPatch, which does the actual work
     * this function was added to have a structure, which reuses code for both post and patch
     * without having to change the function parameters (e.g. post(endpoint, body, patchRequest)
     *
     * @param endpoint -> String of the REST endpoint, is added to our URL
     * @param body     as string in JSON format
     * @return response as string
     */
    public static String post(String endpoint, String body){
        return postOrPatch(endpoint, body, false);
    }

    /**
     * this function performs a patch request -> calls postOrPatch, which does the actual work
     * this function was added to have a structure, which reuses code for both post and patch
     * without having to change the function parameters (e.g. post(endpoint, body, patchRequest)
     *
     * @param endpoint -> String of the REST endpoint, is added to our URL
     * @param body     as string in JSON format
     * @return response as string
     */
    public static String patch(String endpoint, String body){
        return postOrPatch(endpoint, body, true);
    }


    /**
     * this function can be used to perform a post or patch request to a server
     * it will be called by the two function post or patch
     * @param endpoint -> String of the REST endpoint, is added to our URL
     * @param body     as string in JSON format
     * @param patchRequest boolean, which signifies, if this function will be used as patch request
     * @return response as string
     */
    private static String postOrPatch(String endpoint, String body, boolean patchRequest) {
        String response = "";
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(DOMAIN_URL + endpoint);
            urlConnection = (HttpURLConnection) url.openConnection();
            //the following line will only be executed for a patch request, it sets the request method to patch
            //it seems kinda hacky, but there is no way to directly set the request method to patch
            if(patchRequest){
                urlConnection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
                urlConnection.setRequestProperty("Content-Type",
                        "application/merge-patch+json");
            } else {
                urlConnection.setRequestProperty("Content-Type",
                        "application/json");
            }
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream outputStream = urlConnection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(body);
            bufferedWriter.flush();

            //if the status code is anything else but 201 (post) or 204 (patch), we want to return something different,
            //which can be handled in our AsyncTasks
            if (urlConnection.getResponseCode() != 201 && urlConnection.getResponseCode() != 204) {
                Log.e("Status code", urlConnection.getResponseCode() + "");
                Log.e("error in post request", urlConnection.getResponseMessage());
                Log.e("Error stream", readStream(urlConnection.getErrorStream()));
                return "error";
            }
            response = readStream(urlConnection.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }

    /**
     * this function is used to upload pictures to the server
     * @param endpoint -> String of the REST endpoint, is added to our URL
     * @param picturePaths an array list, which holds paths to the images on the phone
     * @return response as string
     */
    public static String uploadPictures(String endpoint, ArrayList<String> picturePaths) {
        String response = "";
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(DOMAIN_URL + endpoint);

            //stuff we need for multipart request
            String crlf = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Cache-Control", "no-cache");
            urlConnection.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + boundary);

            //start content wrapper
            DataOutputStream request = new DataOutputStream(
                    urlConnection.getOutputStream());

            String fieldName = "picture";

            //loops through all paths which are passed by AddLocationActivity
            //creates File for every path, reads the file and writes its bytes
            for (String path : picturePaths) {
                File file = new File(path);
                String fileName = file.getName();
                request.writeBytes(twoHyphens + boundary + crlf);
                request.writeBytes("Content-Disposition: form-data; name=\"" +
                        fieldName + "\";filename=\"" +
                        fileName + "\"" + crlf);
                request.writeBytes(crlf);
                //file to bytes
                int size = (int) file.length();
                byte[] bytes = new byte[size];
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(bytes, 0, bytes.length);
                buf.close();
                request.write(bytes);
                request.writeBytes(crlf);
            }

            //end content wrapper
            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary +
                    twoHyphens + crlf);

            //flush output buffer
            request.flush();
            request.close();
            //if the status code is anything else but 202, we want to return something different,
            //which can be handled in our AsyncTasks
            if (urlConnection.getResponseCode() != 202) {
                Log.e("Error uploading images", urlConnection.getResponseMessage());
                return "error";
            }
            response = readStream(urlConnection.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
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



