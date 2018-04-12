package com.example.piepongwong.friendlybooks;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by Piepongwong on 12-4-2018.
 */

public class DoubleEntryDialog extends DialogFragment {
    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commit();
        } catch (IllegalStateException e) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commit();
            Log.d("ABSDIALOGFRAG", "Exception", e);
            //TODO: fix this hack
        }

    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context theContext = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialogDoubleEntry)
                .setPositiveButton(R.string.yesDialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /*** Get book info from google books api ***/
                        RequestQueue queue = Volley.newRequestQueue(theContext);
                        String googleBooksUrl = getResources().getString(R.string.googleBooksApi);
                        googleBooksUrl = googleBooksUrl + getArguments().getString("isbn");;

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                (Request.Method.GET, googleBooksUrl, null, new Response.Listener<JSONObject>() {
                                    private static final String PROTOCOL_CHARSET="utf-8";

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        SaveBook saveThumbnail = new SaveBook(theContext, response);
                                        saveThumbnail.downloadImageAndSave();
                                    }
                                }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // TODO: Handle error

                                    }
                                });
                        queue.add(jsonObjectRequest);
                    }
                })
                .setNegativeButton(R.string.noDialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}