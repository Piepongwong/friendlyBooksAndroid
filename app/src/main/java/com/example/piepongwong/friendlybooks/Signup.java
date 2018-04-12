package com.example.piepongwong.friendlybooks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Piepongwong on 28-3-2018.
 */

public class Signup extends AppCompatActivity {
    private EditText username;
    private EditText firstname;
    private EditText lastname;
    private EditText email;
    private EditText password;
    private EditText passwordCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        View signupForm = (View) findViewById(R.id.signupForm);
        username = (EditText) signupForm.findViewById(R.id.username);
        firstname = (EditText) signupForm.findViewById(R.id.firstname);
        lastname = (EditText) signupForm.findViewById(R.id.lastname);
        email = (EditText) signupForm.findViewById(R.id.email);
        password = (EditText) signupForm.findViewById(R.id.password);
        passwordCheck = (EditText) signupForm.findViewById(R.id.passwordCheck);

        Button signupButton = (Button) signupForm.findViewById(R.id.signupButton);

        signupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                submit();
            }
        });
    }

    public void submit() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject signupForm = new JSONObject();

        try {
            signupForm.put("username", username.getText().toString());
            signupForm.put("firstname", firstname.getText().toString());
            signupForm.put("lastname", lastname.getText().toString());
            signupForm.put("email", email.getText().toString());
            signupForm.put("password", password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String passwordCheckString = passwordCheck.getText().toString(); // Todo: implement password match check

        String signupUrl = getResources().getString(R.string.signupUrl);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, signupUrl, signupForm, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                    /*    mTextView.setText("Response: " + response.toString()); */
                        //Todo: save "state" in sharedpreferences
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });
        queue.add(jsonObjectRequest);
    }
}