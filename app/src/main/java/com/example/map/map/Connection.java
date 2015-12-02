package com.example.map.map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.Objects;

/**
 * Created by gandhali on 11/25/15.
 */
public class Connection {
    // GET POST - Komal
    // private LatLng p;
    // Connection(LatLng p) { this.p = p; }
  //  String url = "10.0.2.2:3000";
   String url = "192.168.43.56:3000";

    protected class ConnectionPost extends AsyncTask {

        private LatLng position;
        private String id;
        ConnectionPost(LatLng p, String id){
            this.position =p;
            this.id = id;
        }

        @Override
        protected Object doInBackground(Object... arg0) {
            System.out.println("I am here 1");
            try {
                System.out.println("I am here 2");
                connect(position, id);
            } catch (Exception e) {
                System.out.println("I am here 3");
                e.printStackTrace();
            }
            return null;
        }

    }


    protected class ConnectionPostWeatherToStats extends AsyncTask {

        private LatLng position;
        private String weather;
        ConnectionPostWeatherToStats(LatLng p, String weather){
            this.position =p;
            this.weather = weather;
        }
        @Override
        protected Object doInBackground(Object... arg0) {
            System.out.println("I am here 1");
            try {
                System.out.println("I am here 2");
                connectPostWeather(position.latitude, position.longitude, weather);
            } catch (Exception e) {
                System.out.println("I am here 3");
                e.printStackTrace();
            }
            return null;
        }

    }


    protected class ConnectionGet extends AsyncTask<Object,Object,String> {
        double latitude;
        double longitude;
        private ProgressBar progressBar;
        ProgressDialog dialog;
        private GoogleMap map;

        public ConnectionGet(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("onPreExecute");
//            progressBar = MapsActivity.getProgressBar();
//            progressBar.setVisibility(ProgressBar.VISIBLE);

            ProgressDialog pd = new ProgressDialog(MapsActivity.mContext);
            pd.setTitle("Fetching weather status");
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            dialog = pd;
            pd.show();

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            System.out.println("onPostExecute");
//            progressBar.setVisibility(ProgressBar.INVISIBLE);
            if (this.dialog.isShowing()) { // if dialog box showing = true
                this.dialog.dismiss(); // dismiss it
            }
            if (result == null) {
                this.dialog.dismiss();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.mContext);
                alertDialogBuilder
                        .setMessage("No Turks found at this location")
                        .setCancelable(true)
                        .setNegativeButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });
                alertDialogBuilder.show();
                //also show register success dialog
            }
            else{

                MarkerOptions marker = new MarkerOptions().position(new LatLng(this.latitude, this.longitude)).title(result);
                result = result.toLowerCase().replace(' ', '_');
                marker.icon(BitmapDescriptorFactory.fromResource(MapsActivity.mContext.getResources().getIdentifier(result, "drawable", "com.example.map.map")));
                map.addMarker(marker);
            }
        }

        @Override
        protected String doInBackground(Object... arg0) {
            System.out.println("I am here 1");
            String weather = null;
            try {
                System.out.println("I am here 2");
                weather = connectGetWeather(this.latitude, this.longitude);
                map = (GoogleMap) arg0[0];
                connectGetWeather(this.latitude, this.longitude);
            } catch (Exception e) {
                System.out.println("I am here 3");
                e.printStackTrace();
            }
            return weather;
        }
    }

    private void connect2()throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        try {
            System.out.println("I am here 5");
            HttpGet httpget = new HttpGet("http://"+url+"/turks/get");
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                public String handleResponse(final HttpResponse response)
                        throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        System.out.println("I am here 8");
                        throw new ClientProtocolException(
                                "Unexpected response status: " + status);
                    }
                }

            };
            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
    }

    private void connect(LatLng position, String id)throws Exception {
        System.out.println("I am here 10000");

        JSONObject json = new JSONObject() ;//jsonParam[0];
        json.put("id", 109999999);
        json.put("name", "Test1");
        json.put("lat", position.latitude);
        json.put("lon", position.longitude);
        json.put("gcm_regid", id);

        //instantiates httpclient to make request
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpost = new HttpPost("http://"+url+"/turks/location/post");

        //passes the results to a string builder/entity
        StringEntity se = new StringEntity(json.toString());
        System.out.println("I am here 10000  "+se);
        //sets the post request as the resulting string
        httpost.setEntity(se);
        //sets a request header so the page receving the request
        //will know what to do with it
        httpost.setHeader("Accept", "application/json");
        httpost.setHeader("Content-type", "application/json");

        //Handles what is returned from the page
        ResponseHandler responseHandler = new BasicResponseHandler();
        httpclient.execute(httpost, responseHandler);
        httpclient.getConnectionManager().shutdown();
    }

    private void connectPostWeather(double lat, double lon, String weather) throws Exception{
        JSONObject json = new JSONObject() ;//jsonParam[0];
        json.put("weather", weather);
        json.put("lat", lat);
        json.put("lon", lon);


        //instantiates httpclient to make request
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpost = new HttpPost("http://"+url+"/stats/weather/post");

        //passes the results to a string builder/entity
        StringEntity se = new StringEntity(json.toString());
        System.out.println("I am here 10000  "+se);
        //sets the post request as the resulting string
        httpost.setEntity(se);
        //sets a request header so the page receving the request
        //will know what to do with it
        httpost.setHeader("Accept", "application/json");
        httpost.setHeader("Content-type", "application/json");

        //Handles what is returned from the page
        ResponseHandler responseHandler = new BasicResponseHandler();
        httpclient.execute(httpost, responseHandler);
        httpclient.getConnectionManager().shutdown();
    }

    private String connectGetWeather(double lat, double lon)throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        JSONObject jsonObject;
        JSONArray jsonArray;
        JSONParser jsonParser = new JSONParser();
        String weather=null;
        try {
            System.out.println("Trying to get weather status");
            HttpGet httpget = new HttpGet("http://"+url+"/weather/get/"+lat+"/"+lon);
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                public String handleResponse(final HttpResponse response)
                        throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        System.out.println("Get failed");
                        throw new ClientProtocolException(
                                "Unexpected response status: " + status);
                    }
                }

            };
            String responseBody = httpclient.execute(httpget, responseHandler);
            if(responseBody.length()>0)
            {
                jsonArray = (JSONArray) jsonParser.parse(responseBody);
                if(!jsonArray.isEmpty())
                {
                    jsonObject = (JSONObject)jsonArray.get(0);
                    weather = (String) jsonObject.get("weather");
                    System.out.println("----------------Response from Get Weather status------------------------");
                    System.out.println(responseBody);
                    System.out.println(weather);
                }
                else
                {
                    // add a pop-up to say no turk available at this moment
                    System.out.println("Empty response");
                    //weather = "snow";
                }
            }
            else
            {
                System.out.println("No response");
            }

        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return weather;

    }
}


