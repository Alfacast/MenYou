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
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.alfacast.menyou.login.R;
import com.alfacast.menyou.login.app.AppConfig;
import com.alfacast.menyou.login.app.AppController;
import com.alfacast.menyou.login.helper.SQLiteHandlerRestaurant;
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

public class EditPortataActivity extends AppCompatActivity {

    private static final String TAG = InsertPortataActivity.class.getSimpleName();
    private Button btnEditPortata;
    private EditText namePortata;
    private Spinner categoriaPortata;
    private EditText descrizionePortata;
    private EditText prezzoPortata;
    private EditText opzioniPortata;
    private EditText disponibilePortata;
    private ImageView viewImage;
    private Button btnSelectPhoto;
    private Switch switchButton;
    private String switchOn = "Si";
    private String switchOff = "No";
    private ProgressDialog pDialog;
    private SQLiteHandlerPortata db;
    private SQLiteHandlerRestaurant dbr;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_portata_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        namePortata = (EditText) findViewById(R.id.namePortata);
        btnEditPortata = (Button) findViewById(R.id.btnEditPortata);
        categoriaPortata = (Spinner) findViewById(R.id.categoriaPortata);
        descrizionePortata = (EditText) findViewById(R.id.descrizionePortata);
        prezzoPortata = (EditText) findViewById(R.id.prezzoPortata);
        opzioniPortata = (EditText) findViewById(R.id.opzioniPortata);
        disponibilePortata = (EditText) findViewById(R.id.disponibilePortata);
        btnSelectPhoto=(Button)findViewById(R.id.btnSelectPhoto);
        viewImage=(ImageView)findViewById(R.id.viewImage);
        switchButton = (Switch) findViewById(R.id.switchButton);

        Intent intent=getIntent();
        Bundle c=intent.getExtras();
        Bundle d=intent.getExtras();
        Bundle e=intent.getExtras();
        Bundle f=intent.getExtras();
        Bundle g=intent.getExtras();
        Bundle h=intent.getExtras();

        final String repNomePortata = c.getString("nomeportata");
        final String repDescrizionePortata = d.getString("descrizioneportata");
        final String repPrezzoPortata = e.getString("prezzoportata");
        final String repOpzioni = f.getString("opzioni");
        final String repFoto = g.getString("decodedStringFoto");
        final String repIdPortata = h.getString("idportata");

        namePortata.setText(repNomePortata);
        descrizionePortata.setText(repDescrizionePortata);
        prezzoPortata.setText(repPrezzoPortata.replaceFirst("Prezzo: € ",""));
        opzioniPortata.setText(repOpzioni);

        //decodifica immagine da db
        byte[] decodedString = Base64.decode(String.valueOf(repFoto), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        viewImage.setImageBitmap(decodedByte);

        //Imposta portata disponibile si/no
        switchButton.setChecked(true);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    disponibilePortata.setText(switchOn);
                } else {
                    disponibilePortata.setText(switchOff);
                }
            }
        });

        if (switchButton.isChecked()) {
            disponibilePortata.setText(switchOn);
        } else {
            disponibilePortata.setText(switchOff);
        }


        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandlerPortata(getApplicationContext());

        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        // Insert Button Click event
        btnEditPortata.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String nome = namePortata.getText().toString().trim();
                String categoria = categoriaPortata.getSelectedItem().toString();
                String descrizione = descrizionePortata.getText().toString().trim();
                String prezzo = prezzoPortata.getText().toString().trim();
                String opzioni = opzioniPortata.getText().toString().trim();
                String disponibile = disponibilePortata.getText().toString().trim();
                String idPortata = repIdPortata;

                viewImage.buildDrawingCache();
                Bitmap bitmap = viewImage.getDrawingCache();
                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream);
                final byte[] image=stream.toByteArray();

                //Codifica foto su db
                String foto = Base64.encodeToString(image, Base64.NO_WRAP);

                // recupero id dalla tabella ristorante
                dbr = new SQLiteHandlerRestaurant(getApplicationContext());
                HashMap<String, String> a = dbr.getUserDetails();
                final String id_ristorante = a.get("id_ristorante");

                // recupero id menu dalla activity precedente
                Intent i=getIntent();
                Bundle b=i.getExtras();

                final String idMenu = b.getString("idmenu");

                if (!nome.isEmpty() && !categoria.isEmpty() && !descrizione.isEmpty() && !prezzo.isEmpty() && !disponibile.isEmpty()) {
                    editPortata(nome, categoria, descrizione, prezzo, opzioni, disponibile, foto, idPortata);

                    Bundle c= new Bundle();
                    c.putString("idportata", idPortata);

                    //Lancio PortataActivityRistorante
                    Intent intent = new Intent(
                            EditPortataActivity.this,
                            PortataDettaglioRistoranteActivity.class);
                    intent.putExtras(c);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter portata data!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

    }

    private void selectImage() {

        final CharSequence[] options = { "Scatta una foto", "Seleziona dalla galleria","Annulla" };

        AlertDialog.Builder builder = new AlertDialog.Builder(EditPortataActivity.this);
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
                try {
                    //Imposta orientamento automatico foto da dati exif
                    ExifInterface exif = new ExifInterface(picturePath);
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

                    Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                    Log.w("path gallery...", picturePath+"");
                    viewImage.setImageBitmap(thumbnail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void editPortata(final String nome, final String categoria, final String descrizione, final String prezzo, final String opzioni, final String disponibile, final String foto, final String idPortata) {
        // Tag used to cancel the request
        String tag_string_req = "req_insert";

        pDialog.setMessage("Modifica ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_EDITPORTATA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject portata = jObj.getJSONObject("portata");
                        String id_ristorante = portata.getString("id_ristorante");
                        String nome = portata.getString("nome");
                        String categoria = portata.getString("categoria");
                        String descrizione = portata.getString("descrizione");
                        String prezzo = portata.getString("prezzo");
                        String opzioni = portata.getString("opzioni");
                        String disponibile = portata.getString("disponibile");
                        String foto = portata.getString("foto");

                        String created_at = portata.getString("created_at");

                        // Inserting row in users table (commentata per id_ristorante)
                        db.updatePortata(id_ristorante, nome, uid, categoria, descrizione, prezzo, opzioni, disponibile, foto, created_at);

                        Toast.makeText(getApplicationContext(), "Portata successfully update.", Toast.LENGTH_LONG).show();

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
                Log.e(TAG, "Insert Error: " + error.getMessage());
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
                params.put("categoria", categoria);
                params.put("descrizione", descrizione);
                params.put("prezzo", prezzo);
                params.put("opzioni", opzioni);
                params.put("disponibile", disponibile);
                params.put("foto", foto);
                params.put("idportata", idPortata);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}