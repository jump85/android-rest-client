package com.example.javaaiapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONObject;

import org.json.JSONArray;
import org.json.JSONException;

import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.VolleyError;


import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private TextView result;
    private EditText input;

    String baseUrl = "https://api.github.com/users/";  // This is the API base URL (GitHub API)
    String url;  // This will hold the full URL which will include the username entered in the etGitHubUser.

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = findViewById(R.id.result);
        input = findViewById(R.id.input);

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();
    }

    public void printText(View view){
        result.setText("Hai scritto :"+input.getText());
    }

    public void getData(View view) {
        String username = ""+input.getText();

        this.url = this.baseUrl + username + "/repos";
        System.out.println("Request to Github..."+this.url);

        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String resultText = "";

                        // Check the length of our response (to see if the user has any repos)
                        if (response.length() > 0) {

                            // The user does have repos, so let's loop through them all.
                            for (int i = 0; i < response.length(); i++) {
                                try {

                                    // For each repo, add a new line to our repo list.
                                    JSONObject jsonObj = response.getJSONObject(i);
                                    String repoName = jsonObj.get("name").toString();
                                    String lastUpdated = jsonObj.get("updated_at").toString();

                                    resultText+= (i+1)+". Nome: "+repoName+"\n last update: "+lastUpdated+"\n";


                                } catch (JSONException e) {
                                    // If there is an error then output this to the logs.
                                    System.out.println("Invalid JSON Object.");
                                }

                            }
                            System.out.println(resultText);
                            result.setText(resultText);
                        } else {
                            // The user didn't have any repos.
                            System.out.println("No repos found.");
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there a HTTP error then add a note to our repo list.
                        System.out.println("Error while calling REST API");
                        Log.e("Volley", error.toString());
                    }
                }
        );

        //Adds the JSON request to the request queue
        requestQueue.add(arrReq);
    }
}
