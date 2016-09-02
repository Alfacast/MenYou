package com.alfacast.menyou.restaurant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alfacast.menyou.login.R;
import com.alfacast.menyou.UrlConfig;
import com.alfacast.menyou.login.app.AppController;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EditMenuActivity extends AppCompatActivity {

    private ProgressDialog eDialog;
    private static final String TAG = EditMenuActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_menu_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eDialog = new ProgressDialog(this);
        eDialog.setCancelable(false);

        final Button btnEditMenu = (Button) findViewById(R.id.btnEditMenu);
        final EditText nameMenu  = (EditText) findViewById(R.id.nameMenu);

        Intent intent=getIntent();
        Bundle b=intent.getExtras();
        Bundle c=intent.getExtras();

        final String repNameMenu = b.getString("nomemenu");
        final String idMenu = c.getString("idmenu");

        nameMenu.setText(repNameMenu);

        // Insert Button Click event
        btnEditMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                final String newMenu = nameMenu.getText().toString().replace(" ","%20");

                // Showing progress dialog before making http request
                eDialog.setMessage("Loading...");
                showDialog();

                // Creating volley request obj
                final JsonArrayRequest menuReq = new JsonArrayRequest(UrlConfig.URL_EditMenuActivity +idMenu+"&nomemenu="+newMenu,

                        new Response.Listener<JSONArray>() {

                            @Override
                            public void onResponse(JSONArray response) {
                                Log.d(TAG, response.toString());

                                // Parsing json
                                try {

                                    JSONObject obj = response.getJSONObject(0);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());

                    }

                });

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(menuReq);

                //lancio la mainactivity
                Intent i = new Intent(view.getContext(),
                        MainRistoranteActivity.class);
                view.getContext().startActivity(i);
                ((Activity)view.getContext()).finish();
            }

        });
    }
    private void showDialog() {
        if (eDialog.isShowing())
            eDialog.show();
    }
}

