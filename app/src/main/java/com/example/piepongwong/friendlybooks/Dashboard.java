package com.example.piepongwong.friendlybooks;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static java.security.AccessController.getContext;

/**
 * Created by Piepongwong on 28-3-2018.
 */

public class Dashboard extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private Dashboard currentActivity = this;
    private Fragment fragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Log.i("Navigation", "something selected: ");
                int id = item.getItemId();
                Class fragmentClass;

                switch (item.getItemId()){
                    case R.id.nav_camera:
                        IntentIntegrator integrator = new IntentIntegrator(Dashboard.this);
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                        integrator.setPrompt("Scan a barcode");
                        integrator.setCameraId(0);
                        integrator.setBeepEnabled(false);
                        integrator.initiateScan();
                    case R.id.nav_gallery:
                        fragmentClass = BookList.class;
                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .commitAllowingStateLoss ();
                        break;
                    case R.id.nav_slideshow:
                        BooksDbHelper mDbHelper = new BooksDbHelper(Dashboard.this);
                            List<Book> allBooks = mDbHelper.getAllBooks();
                        if (allBooks == null) {
                            Toast.makeText(Dashboard.this, "No books in database yet", Toast.LENGTH_LONG).show();
                        }
                    case  R.id.nav_manage:
                        fragmentClass = UserInfo.class;
                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .commit();
                }
                item.setChecked(true);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            /*** Check if book is already in database ***/
            String barcode =  scanResult.getContents();
            BooksDbHelper mDbHelper = new BooksDbHelper(Dashboard.this);
            if(mDbHelper.checkExists(barcode)) {
                Bundle args = new Bundle();
                args.putString("isbn", barcode);
                DoubleEntryDialog dialog = new DoubleEntryDialog();
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
                return;
            }
            /*** Get book info from google books api ***/
            RequestQueue queue = Volley.newRequestQueue(this);
            String googleBooksUrl = getResources().getString(R.string.googleBooksApi);
            googleBooksUrl = googleBooksUrl + scanResult.getContents();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, googleBooksUrl, null, new Response.Listener<JSONObject>() {
                        private static final String PROTOCOL_CHARSET="utf-8";

                        @Override
                        public void onResponse(JSONObject response) {
                            SaveBook saveThumbnail = new SaveBook(getApplicationContext(), response);
                            saveThumbnail.downloadImageAndSave();
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error

                        }
                    });
            queue.add(jsonObjectRequest);
            return;

        };
    }
}

