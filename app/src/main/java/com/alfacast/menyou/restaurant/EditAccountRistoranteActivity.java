package com.alfacast.menyou.restaurant;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alfacast.menyou.login.R;
import com.alfacast.menyou.login.activity.LoginActivity;
import com.alfacast.menyou.login.app.AppConfig;
import com.alfacast.menyou.login.app.AppController;
import com.alfacast.menyou.login.helper.SQLiteHandlerRestaurant;
import com.alfacast.menyou.login.helper.SessionManager;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class EditAccountRistoranteActivity extends AppCompatActivity {

    private static final String TAG = EditAccountRistoranteActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private Button btnEdit;
    private EditText nameRestaurant;
    private EditText restaurantAddress;
    private EditText restaurantPartitaIva;
    private EditText restaurantEmail;
    private EditText restaurantTel;
    private EditText restaurantPassword;
    private Button btnInsertFoto;
    private ImageView viewImage;

    private SQLiteHandlerRestaurant db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ristorante_edit_account_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nameRestaurant = (EditText) findViewById(R.id.nameRestaurant);
        restaurantAddress = (EditText) findViewById(R.id.RestaurantAddress);
        restaurantPartitaIva = (EditText) findViewById(R.id.PartitaIva);
        restaurantEmail = (EditText) findViewById(R.id.RestaurantEmail);
        restaurantTel = (EditText) findViewById(R.id.RestaurantTel);
        restaurantPassword = (EditText) findViewById(R.id.RestaurantPassword);
        btnInsertFoto=(Button)findViewById(R.id.btnSelectPhoto);
        btnEdit = (Button) findViewById(R.id.btnEditAccount);
        viewImage=(ImageView)findViewById(R.id.viewImage);

        // SqLite database handler
        db = new SQLiteHandlerRestaurant(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String nome = user.get("nome");
        String indirizzo = user.get("address");
        String partitaIva = user.get("partitaIva");
        String email = user.get("email");
        String telefono = user.get("tel");
        String foto = user.get("foto");

        // Displaying the user details on the screen
        nameRestaurant.setText(nome);
        restaurantAddress.setText(indirizzo);
        restaurantPartitaIva.setText(partitaIva);
        restaurantEmail.setText(email);
        restaurantTel.setText(telefono);

        //decodifica immagine da db
        byte[] decodedString = Base64.decode(String.valueOf(foto), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        viewImage.setImageBitmap(decodedByte);

        btnInsertFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        // Update Button Click event
        btnEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String nome = nameRestaurant.getText().toString().trim();
                String address = restaurantAddress.getText().toString().trim();
                String partitaIva = restaurantPartitaIva.getText().toString().trim();
                String email = restaurantEmail.getText().toString().trim();
                String tel = restaurantTel.getText().toString().trim();
                String password = restaurantPassword.getText().toString().trim();

                viewImage.buildDrawingCache();
                Bitmap bitmap = viewImage.getDrawingCache();
                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream);
                final byte[] image=stream.toByteArray();
                String foto = Base64.encodeToString(image, Base64.NO_WRAP);

                if (!nome.isEmpty() && !address.isEmpty() && !partitaIva.isEmpty() && !email.isEmpty() && !tel.isEmpty() && !password.isEmpty() && !foto.isEmpty()) {
                    editRestaurant(nome, address, partitaIva, email, tel, password, foto);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

    }

    private void selectImage() {

        final CharSequence[] options = { "Scatta una foto", "Seleziona dalla galleria","Annulla" };

        AlertDialog.Builder builder = new AlertDialog.Builder(EditAccountRistoranteActivity.this);
        builder.setTitle("Aggiungi foto");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Scatta una foto"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                }
                else if (options[item].equals("Seleziona dalla galleria"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                }
                else if (options[item].equals("Annulla")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    //Imposta orientamento automatico foto da dati exif
                    ExifInterface exif = new ExifInterface(f.getPath());
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                    int angle = 0;

                    if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                        angle = 90;
                    }
                    else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                        angle = 180;
                    }
                    else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                        angle = 270;
                    }

                    Matrix mat = new Matrix();
                    mat.postRotate(angle);

                    Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f), null, null);
                    Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);

                    viewImage.setImageBitmap(bitmap);

                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {

                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.w("path gallery...", picturePath+"");
                viewImage.setImageBitmap(thumbnail);
            }
        }
    }

    private void editRestaurant(final String nome, final String address, final String partitaIva,final String email, final String tel, final String password, final String foto) {
        // Tag used to cancel the request
        String tag_string_req = "req_update";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_RESTAURANTUPDATE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Response: " + response.toString());
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String id_database = "";
                        String nome = user.getString("nome");
                        String address = user.getString("address");
                        String partitaIva = user.getString("partitaIva");
                        String email = user.getString("email");
                        String tel = user.getString("telefono");
                        String foto = user.getString("foto");

                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                        db.updateUser(id_database, nome, address, partitaIva, email, tel, foto, uid, created_at);

                        Toast.makeText(getApplicationContext(), "User successfully update!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                EditAccountRistoranteActivity.this,
                                MainRistoranteActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("nome", nome);
                params.put("address", address);
                params.put("partitaIva", partitaIva);
                params.put("email", email);
                params.put("telefono", tel);
                params.put("password", password);
                params.put("foto", foto);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
