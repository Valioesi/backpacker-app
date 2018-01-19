package com.interactivemedia.backpacker.helpers;

import android.content.Context;
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

    // public static final String DOMAIN_URL = "http://10.60.60.143:3000";   //Uni
    // public static final String DOMAIN_URL = "http://192.168.178.25:3000";   //Vali Stuttgart
    // public static final String DOMAIN_URL="http://192.168.178.50:3000"; //Rebecca Stuttgart
    public static final String DOMAIN_URL="http://10.60.32.5:3001";
    private static final String API_URL = DOMAIN_URL + "/api/v0";
    public static final String IMAGES_URL = DOMAIN_URL + "/uploads/imgs";

    private enum RequestMethod {POST, PUT, PATCH}


    /**
     * this function can be used to perform a get request to a server
     *
     * @param context  application context, needed to get shared preferences
     * @param endpoint -> String of the REST endpoint, is added to our URL
     * @return the server response as String
     */
    public static String get(Context context, String endpoint) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(API_URL + endpoint);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("access_token", Preferences.getIdToken(context));
            //if the status code is anything else but 200, we want to return something different,
            //which can be handled in our AsyncTasks
            if (urlConnection.getResponseCode() != 200) {
                Log.e("Error stream", readStream(urlConnection.getErrorStream()));
                return null;
            }
            return readStream(urlConnection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();

            }
        }
        return null;
    }

    /**
     * this function can be used to perform a delete request to a server
     *
     * @param context  application context, needed to get shared preferences
     * @param endpoint -> String of the REST endpoint, is added to our URL
     * @return the server response as String
     */
    public static String delete(Context context, String endpoint) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(API_URL + endpoint);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty("access_token", Preferences.getIdToken(context));
            //if the status code is anything else but 200, we want to return something different,
            //which can be handled in our AsyncTasks
            if (urlConnection.getResponseCode() % 100 == 2) {
                Log.e("Error stream", readStream(urlConnection.getErrorStream()));
                return null;
            }
            return readStream(urlConnection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();

            }
        }
        return null;
    }

    /**
     * this function performs a post request -> calls postPutOrPatch, which does the actual work
     * this function was added to have a structure, which reuses code for all post, put and patch
     * without having to change the function parameters (e.g. post(endpoint, body, method)
     *
     * @param context  application context, needed to get shared preferences
     * @param endpoint -> String of the REST endpoint, is added to our URL
     * @param body     as string in JSON format
     * @return response as string
     */
    public static String post(Context context, String endpoint, String body) {
        return postPutOrPatch(context, endpoint, body, RequestMethod.POST);
    }

    /**
     * this function performs a patch request -> calls postPutOrPatch, which does the actual work
     * this function was added to have a structure, which reuses code for all post, put and patch
     * without having to change the function parameters (e.g. post(endpoint, body, method)
     *
     * @param context  application context, needed to get shared preferences
     * @param endpoint -> String of the REST endpoint, is added to our URL
     * @param body     as string in JSON format
     * @return response as string
     */
    public static String patch(Context context, String endpoint, String body) {
        return postPutOrPatch(context, endpoint, body, RequestMethod.PATCH);
    }

    /**
     * this function performs a put request -> calls postPutOrPatch, which does the actual work
     * this function was added to have a structure, which reuses code for all post, put and patch
     * without having to change the function parameters (e.g. post(endpoint, body, patchRequest)
     *
     * @param context  application context, needed to get shared preferences
     * @param endpoint -> String of the REST endpoint, is added to our URL
     * @return response as string
     */
    public static String put(Context context, String endpoint) {
        return postPutOrPatch(context, endpoint, "{}", RequestMethod.PUT);
    }


    /**
     * this function can be used to perform a post or patch request to a server
     * it will be called by the two function post or patch
     *
     * @param context       application context, needed to get shared preferences
     * @param endpoint      -> String of the REST endpoint, is added to our URL
     * @param body          as string in JSON format
     * @param requestMethod value of RequestMethod enum (POST, PUT or PATCH), which signifies, which method will be used
     * @return response as string
     */
    private static String postPutOrPatch(Context context, String endpoint, String body, RequestMethod requestMethod) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(API_URL + endpoint);
            urlConnection = (HttpURLConnection) url.openConnection();
            //the following line will only be executed for a patch request, it sets the request method to patch
            //it seems kinda hacky, but there is no way to directly set the request method to patch
            switch (requestMethod) {
                case PATCH:
                    urlConnection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
                    urlConnection.setRequestProperty("Content-Type",
                            "application/merge-patch+json");
                    break;
                case POST:
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type",
                            "application/json");
                    break;
                case PUT:
                    urlConnection.setRequestMethod("PUT");
                    urlConnection.setRequestProperty("Content-Type",
                            "application/json");
                    break;
            }

            urlConnection.setRequestProperty("access_token", Preferences.getIdToken(context));
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream outputStream = urlConnection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(body);
            bufferedWriter.flush();

            //if the status code is anything else but a 2-something, we want to return something different,
            //which can be handled in our AsyncTasks
            if (urlConnection.getResponseCode() % 100 == 2) {
                Log.e("Status code", urlConnection.getResponseCode() + "");
                Log.e("error in post request", urlConnection.getResponseMessage());
                Log.e("Error stream", readStream(urlConnection.getErrorStream()));
                return null;
            }
            return readStream(urlConnection.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    /**
     * this function is used to upload pictures to the server
     *
     * @param context       application context, needed to get shared preferences
     * @param endpoint      -> String of the REST endpoint, is added to our URL
     * @param picturePaths  an array list, which holds paths to the images on the phone
     * @param requestMethod string, which indicates the HTTP method
     * @return response as string
     */
    public static String uploadPictures(Context context, String endpoint, ArrayList<String> picturePaths, String requestMethod) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(API_URL + endpoint);

            //stuff we need for multipart request
            String crlf = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);


            urlConnection.setRequestMethod(requestMethod);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Cache-Control", "no-cache");
            urlConnection.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + boundary);
            urlConnection.setRequestProperty("access_token", Preferences.getIdToken(context));

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
                Log.e("Error message", readStream(urlConnection.getErrorStream()));
                return null;
            }
            return readStream(urlConnection.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;

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



