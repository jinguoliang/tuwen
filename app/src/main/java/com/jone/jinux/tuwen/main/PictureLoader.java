package com.jone.jinux.tuwen.main;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jone.jinux.tuwen.App;
import com.jone.jinux.tuwen.base.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jinux on 16/12/15.
 */
public class PictureLoader {
    public static interface OnResult<T> {
        void onResult(T result);
    }
    public  void load(String s, final OnResult<List<String>> onResult) {
        Utils.log("request: " + s);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, s, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Utils.log(response.toString(4));
                    List<String> urls = processJson(response);
                    onResult.onResult(urls);
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.toast("result error");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utils.log("errer: " + error + ":code " + error.networkResponse);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        Volley.newRequestQueue(App.getInstance()).add(request);
    }

    private List<String> processJson(JSONObject response) throws Exception {
        if (response.getBoolean("error")) {
            throw new Exception("json format error");
        }

        JSONArray array = response.getJSONArray("results");
        ArrayList<String> urls = new ArrayList<>(array.length());

        for (int i = 0; i< array.length() ;i++){
            JSONObject object = array.getJSONObject(i);
            urls.add(object.getString("url"));
        }

        return urls;
    }
}
