package com.cotter.app;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthRequest {
    public String mainServerURL;
    public AuthRequest(String url) {
        mainServerURL = url;
    }
    public void GetUser(final Context context, final Callback callback) {
        String url = mainServerURL + "/user/" + Cotter.UserID;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                        Log.d("AUTH_REQUEST_GET_USER", "Success getting user: " + response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(getErrorMessage(error));
                        Log.d("AUTH_REQUEST_GET_USER", "Error getting user: " + getErrorMessage(error));
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("API_KEY_ID", Cotter.ApiKeyID);
                params.put("API_SECRET_KEY", Cotter.ApiSecretKey);

                return params;
            }
        };

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
    public void EnrollMethod(Context context, String method, String code, final Callback callback) {
        UpdateMethod(context, method, code, true, callback);
    }
    
    public void DeleteMethod(Context context, String method, String code, final Callback callback) {
        UpdateMethod(context, method, code, false, callback);
    }

    public void UpdateMethod(Context context, String method, String code, boolean enrolled, final Callback callback) {
        String url = mainServerURL + "/user/" + Cotter.UserID;

        JSONObject req = new JSONObject();

        try {
            req.put("method", method);
            req.put("enrolled", enrolled);
            req.put("code", code);
        } catch (Exception e) {
            callback.onError(e.toString());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.PUT, url, req, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(getErrorMessage(error));
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("API_KEY_ID", Cotter.ApiKeyID);
                params.put("API_SECRET_KEY", Cotter.ApiSecretKey);

                return params;
            }
        };

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }


    public void GetRules(Context context, final Callback callback) {
        String url = mainServerURL + "/rules";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                        Log.d("AUTH_REQUEST_GET_RULES", "Success getting rules: " + response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(getErrorMessage(error));
                        Log.d("AUTH_REQUEST_GET_RULES", "Success getting rules: " + getErrorMessage(error));
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("API_KEY_ID", Cotter.ApiKeyID);
                params.put("API_SECRET_KEY", Cotter.ApiSecretKey);

                return params;
            }
        };

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }


    public String ConstructApprovedEventMsg(String event, String timestamp, String method) {
        return String.join("", Cotter.getUser(Cotter.authRequest).client_user_id,Cotter.ApiKeyID,
                event,
                timestamp,
                method,
                "true"
        );
    }

    public JSONObject ConstructApprovedEventJSON(String event, String timestamp, String method, String code, final Callback callback) {
        final JSONObject req = new JSONObject();

        try {
            req.put("client_user_id", Cotter.getUser(Cotter.authRequest).client_user_id);
            req.put("issuer", Cotter.ApiKeyID);
            req.put("event", event);
            req.put("ip", getLocalIpAddress());
            req.put("timestamp", timestamp);
            req.put("method", method);
            req.put("code", code);
            req.put("approved", true);
        } catch (Exception e) {
            callback.onError(e.toString());
        }
        return req;
    }

    public void CreateApprovedEventRequest(Context context, JSONObject req, final Callback callback) {
        String url = mainServerURL + "/event/create";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, req, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(getErrorMessage(error));
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("API_KEY_ID", Cotter.ApiKeyID);
                params.put("API_SECRET_KEY", Cotter.ApiSecretKey);

                return params;
            }
        };

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getLocation(Activity act) {
        final List<Location> locContainer = new ArrayList<>();
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(act);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(act, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            locContainer.add(location);
                        }
                    }
                });
        Location loc = locContainer.get(0);
        return loc.getLatitude() + " " +loc.getLongitude();
    }

    public static String getErrorMessage(VolleyError error) {
        if (error.getMessage() != null) {
            Log.d("Volley Error", error.getMessage());
        }
        return "Fail http request";
    }
}
