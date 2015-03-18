package com.novomatic.vas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


public class ProspectEdit extends Activity {

    private Prospect prospect = null;

    String _denominazione = "";
    String _indirizzo = "";
    String _CAP = "";
    String _civico = "";
    String _telefono = "";
    int prospect_id;
    int indirizzo_id;

    EditText denominazione;
    EditText indirizzo;
    EditText CAP;
    EditText civico;
    EditText telefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prospect_edit);

        Intent intent = getIntent();
        prospect_id = intent.getIntExtra("prospect_id",0);

        StoreManager storeManager = new StoreManager();

        prospect = new ParseResponseWebAPI
                (storeManager.GetData(this)).GetProspectById(prospect_id);

        denominazione = (EditText) findViewById(R.id.editText_denominazione);
        denominazione.setText(prospect.getDenominazione());

        indirizzo = (EditText) findViewById(R.id.editText_indirizzo);
        indirizzo.setText(prospect.getIndirizzo());

        indirizzo_id = prospect.getIndirizzo_id();

        CAP = (EditText) findViewById(R.id.editText_CAP);
        CAP.setText(prospect.getCAP());

        civico = (EditText) findViewById(R.id.editText_civico);
        civico.setText(prospect.getnCivico());

        telefono = (EditText) findViewById(R.id.editText_tel);
        telefono.setText(prospect.getTelefono());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_prospect_edit, menu);

        menu.add(0,0,0,"Accetta").setIcon(R.drawable.ic_action_accept).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case 0:
                //Toast.makeText(this,"Ok",Toast.LENGTH_LONG).show();
                SaveProspect();
                break;
        }

        /*
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        */

        return super.onOptionsItemSelected(item);
    }

    private void SaveProspect(){

        _denominazione = denominazione.getText().toString();
        _CAP = CAP.getText().toString();
        _civico = civico.getText().toString();
        _indirizzo = indirizzo.getText().toString();
        _telefono = telefono.getText().toString();

        try {
           new  NetworkAdapter(this).execute();
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    private class NetworkAdapter extends AsyncTask<Void, Integer, String> {

        private ProgressDialog dialog;


        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {

            InputStream in = entity.getContent();
            StringBuffer out = new StringBuffer();

            int n = 1;
            while (n>0) {
                byte[] b = new byte[4096];
                n =  in.read(b);
                if (n>0) out.append(new String(b, 0, n));
            }
            return out.toString();
        }

        Activity mActivity;

        public NetworkAdapter(Activity activity){

            mActivity = activity;
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected String doInBackground(Void... params) {

            String appResponse = "";

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://vas-val.gmatica.it/api/AppProspect");

            String postResult = "";

            try
            {
                JSONObject jsonProspect = new JSONObject();

                jsonProspect.put("ProspectID", prospect_id);
                jsonProspect.put("Denominazione", _denominazione);
                jsonProspect.put("Indirizzo_id", indirizzo_id);
                jsonProspect.put("Telefono", _telefono);
                jsonProspect.put("CAP", _CAP);
                jsonProspect.put("NCivico", _civico);

                JSONObject indirizzo_inserito = new JSONObject();

                indirizzo_inserito.put("Via", _indirizzo);
                indirizzo_inserito.put("NCivico", _civico);
                indirizzo_inserito.put("CAP", _CAP);

                jsonProspect.put("indirizzo_inserito", indirizzo_inserito);

                StringEntity entityObj = new StringEntity("=" + jsonProspect.toString(), HTTP.UTF_8);

                httppost.setEntity(entityObj);

                httppost.setHeader("User-Agent", "Fiddler");
                httppost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

                httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Custom user agent");

                HttpResponse response = httpclient.execute(httppost);

                //TODO: verificare il corretto invio dell'oggetto JSON
                if(response.getStatusLine().getStatusCode() == 200) {

                    HttpEntity entity = response.getEntity();
                    appResponse = getASCIIContentFromEntity(entity);

                    JSONObject reader = new JSONObject(appResponse);

                    String errore = (String) reader.get("Errore");
                    boolean isPasswordScaduta = (boolean) reader.get("IsPasswordScaduta");

                    JSONObject apk = reader.getJSONObject("apk");

                    String versione = (String) apk.get("versione");

                    JSONObject profile = reader.getJSONObject("Profile");

                    StoreManager storeManager = new StoreManager();
                    storeManager.SaveData(mActivity.getBaseContext(),appResponse);

                    postResult = String.valueOf(response.getStatusLine().getStatusCode());


                }else {
                    postResult =  "Result: " + response.getStatusLine().getStatusCode();
                }

            }catch (Exception ex){
                postResult = ex.getMessage();
            }

            return postResult;

        }

        @Override
        protected void onPreExecute(){

            dialog.setMessage("Recupero informazioni in corso...");
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values ){
            //super.onProgressUpdate(values);
            //progress.setProgress(values[0].intValue());
        }

        @Override
        protected void onPostExecute(String result) {

            //super.onPostExecute(result);
            //progress.dismiss();
            //Toast.makeText(mContext, "Fine",   Toast.LENGTH_SHORT).show();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            Intent intent = new Intent(mActivity, MainActivity.class);
            mActivity.startActivity(intent);

        }
    }

}
