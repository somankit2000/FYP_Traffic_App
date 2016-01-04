package com.example.tonyso.TrafficApp;

import android.util.Log;

import com.example.tonyso.TrafficApp.model.NearbyLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by soman on 2016/1/3.
 */
public class NearbyPlacesHandler {
    private String API_KEY;

    public NearbyPlacesHandler(String API_KEY) {
        this.API_KEY = API_KEY;
    }

    public String getAPI_KEY() {
        return API_KEY;
    }

    public void setAPI_KEY(String API_KEY) {
        this.API_KEY = API_KEY;
    }

    protected String getJSON(String url) {
        return getUrlContents(url);
    }

    public List<NearbyLocation> findPlaces(double latitude, double longitude, String placeSpacification, String currLocation) {
        String urlString = makeUrl(latitude, longitude, placeSpacification);
        try {
            String json = getJSON(urlString);

            System.out.println(json);
            JSONObject object = new JSONObject(json);
            JSONArray array = object.getJSONArray("results");
            ArrayList<NearbyLocation> arrayList = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                try {
                    NearbyLocation place = NearbyLocation.jsonToPontoReferencia((JSONObject) array.get(i));
                    Log.v("Places Services ", "" + place);
                    arrayList.add(place);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < arrayList.size(); i++) {
                String distanceUrl = makeDistanceURL(currLocation, arrayList.get(i).getName());
                try {
                    json = getJSON(distanceUrl);
                    Log.d("JSON Distance= ", json);
                    arrayList.get(i).setDistanceInKm(NearbyLocation.getDistanceByJSON(json));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return arrayList;
        } catch (JSONException ex) {
            Logger.getLogger(NearbyPlacesHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private String makeDistanceURL(String currentLocation, String destination) {
        //http://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal&sensor=false
        StringBuilder url = new StringBuilder("http://maps.googleapis.com/maps/api/directions/json?");
        try {
            url.append("origin=" + URLEncoder.encode(currentLocation, "UTF-8"));
            url.append("&");
            url.append("destination=" + URLEncoder.encode(destination, "UTF-8"));
            url.append("&sensor=false");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url.toString();
    }


    //https://maps.googleapis.com/maps/api/place/search/json?location=28.632808,77.218276&radius=500&types=atm&sensor=false&key=<key>
    private String makeUrl(double latitude, double longitude, String place) {
        StringBuilder urlString = new StringBuilder("https://maps.googleapis.com/maps/api/place/search/json?");

        if (place.equals("")) {
            urlString.append("&location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&radius=500");
            //   urlString.append("&types="+place);
            urlString.append("&sensor=false&key=" + API_KEY);
        } else {
            urlString.append("&location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&radius=1000");
            urlString.append("&types=" + place);
            urlString.append("&sensor=false&key=" + API_KEY);
        }


        return urlString.toString();
    }


    private String getUrlContents(String theUrl) {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(theUrl);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()), 8);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }

            bufferedReader.close();
        } catch (Exception e) {

            e.printStackTrace();

        }

        return content.toString();
    }

}
