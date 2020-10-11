package com.example.eventtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {


    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private TextView textView;
    Log log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();

        loginButton = findViewById(R.id.login_button);
        textView = findViewById(R.id.textView);

        loginButton.setPermissions(Arrays.asList("user_events", "email"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                log.d("Demo", object.toString());
                try {
                    String events = "";
                    JSONObject eventsObject = object.getJSONObject("events");
                    JSONArray dataArray = eventsObject.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++){
                        JSONObject fields = dataArray.getJSONObject(i);
                        JSONObject place = fields.getJSONObject("place");
                        JSONObject location = place.getJSONObject("location");
                        Double latitude = location.getDouble("latitude");
                        Double longitude = location.getDouble("longitude");
                        events += "Event " + i + "\nlatitiude: " + Double.toString(latitude) +
                                " longitude: " + Double.toString(longitude) + "\n";
                    }
                    //String email = object.getString("email");
                    textView.setText(events);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("fields", "email, events");

        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();
    }

    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null){
                LoginManager.getInstance().logOut();
                textView.setText("");
            }
        }
    };

    @Override
    protected void onDestroy(){
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }


}
