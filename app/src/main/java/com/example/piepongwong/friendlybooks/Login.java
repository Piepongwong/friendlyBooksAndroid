package com.example.piepongwong.friendlybooks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class Login extends AppCompatActivity {
    private EditText usernameOrEmail;
    private EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        View loginForm = (View) findViewById(R.id.loginForm);
        usernameOrEmail = (EditText) loginForm.findViewById(R.id.usernameOrEmail);
        password = (EditText) loginForm.findViewById(R.id.password);

        Button loginButton = (Button) loginForm.findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        Button signupButton = (Button) loginForm.findViewById(R.id.createAccount);
        signupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), Signup.class);
            startActivity(intent);
            }
        });

    }

    public void submit() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject loginForm = new JSONObject();

        try {
            loginForm.put("username", usernameOrEmail.getText().toString());
            loginForm.put("password", password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        String loginUrl = getResources().getString(R.string.loginUrl);
        JsonObjectRequest jsonObjectRequest = new JSONRequestCustom
                (Request.Method.POST, loginUrl, loginForm, new Response.Listener<JSONObject>() {
                    private static final String PROTOCOL_CHARSET="utf-8";

                    @Override
                    public void onResponse(JSONObject response) {
                        /***Get cookie to maintain session with server***/
                        //Todo: save "state" in sharedpreferences
                        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.userData), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        try {
                            JSONObject headers = response.getJSONObject("headers");
                            String connectSid = headers.getString("set-cookie");
                            editor.putString("connectSidCookie", connectSid);
                            editor.putString(getString(R.string.username), response.getString("username"));
                            editor.putString(getString(R.string.firstname), response.getString("firstname"));
                            editor.putString(getString(R.string.lastname), response.getString("lastname"));
                            editor.putString(getString(R.string.email), response.getString("email"));
                            editor.commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(Login.this, getResources().getString(R.string.loginFailed), Toast.LENGTH_LONG).show();
                    }
                });
        queue.add(jsonObjectRequest);

    }
    // Custom request
    private class JSONRequestCustom extends JsonObjectRequest {

        public JSONRequestCustom(int method, String url, JSONObject jsonRequest, Response.Listener
                <JSONObject> listener, Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
        }

        public JSONRequestCustom(String url, JSONObject jsonRequest, Response.Listener<JSONObject>
                listener, Response.ErrorListener errorListener) {
            super(url, jsonRequest, listener, errorListener);
        }

        @Override
        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
            try {
                String jsonString = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                JSONObject jsonResponse = new JSONObject(jsonString);
                jsonResponse.put("headers", new JSONObject(response.headers));
                return Response.success(jsonResponse,
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (JSONException je) {
                return Response.error(new ParseError(je));
            }
        }
    }
}

