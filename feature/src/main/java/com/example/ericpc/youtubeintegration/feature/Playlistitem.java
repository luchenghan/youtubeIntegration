package com.example.ericpc.youtubeintegration.feature;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Playlistitem extends AppCompatActivity {
    ListView lvitem;
    ArrayList<PlaylistDetail> playlistitemDetailArrayList;
    CustomPlaylistAdapter customPlaylistAdapter;
    String TAG="Playlistitem";

    //String URL="https://www.googleapis.com/youtube/v3/playlistItems?part=snippet%2CcontentDetails&maxResults=25&playlistId="+showVideo+"&key=AIzaSyBKLY8Iukpl8-Fng0qN3puMfdtAyDDwccA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlistitem);
        lvitem=(ListView)findViewById(R.id.playList);

        playlistitemDetailArrayList=new ArrayList<>();
        customPlaylistAdapter=new CustomPlaylistAdapter(Playlistitem.this,playlistitemDetailArrayList);
        showPlaylistitem();
    }

    private void showPlaylistitem() {
        Bundle bundle = getIntent().getExtras();
        String showVideo = bundle.getString("videoId");
        String cht = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet%2CcontentDetails&maxResults=25&playlistId=";
        String num = "&key=AIzaSyBKLY8Iukpl8-Fng0qN3puMfdtAyDDwccA";
        String append = cht+showVideo+num;
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET,append, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("items");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        JSONObject jsonsnippet= jsonObject1.getJSONObject("snippet");
                        JSONObject jsonObjectdefault = jsonsnippet.getJSONObject("thumbnails").getJSONObject("medium");
                        JSONObject jsonVideoId=jsonsnippet.getJSONObject("resourceId");
                        PlaylistDetail videoDetails=new PlaylistDetail();

                        String videoid=jsonVideoId.getString("videoId");

                        Log.e(TAG," New Video Id" +videoid);
                        videoDetails.setURL(jsonObjectdefault.getString("url"));
                        videoDetails.setVideoName(jsonsnippet.getString("title"));
                        //videoDetails.setVideoDesc(jsonsnippet.getString("description"));
                        videoDetails.setVideoId(videoid);           //set playlistid

                        playlistitemDetailArrayList.add(videoDetails);
                    }
                    lvitem.setAdapter(customPlaylistAdapter);
                    customPlaylistAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }
}

