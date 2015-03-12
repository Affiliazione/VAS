package com.novomatic.vas;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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


public class LogIn extends Activity {

    ProgressDialog progress=null;

    String userName = "";
    String password = "";

    String uuid = "";
    String version = "";
    String model = "";

    EditText editText_userName;
    EditText editText_password;

    public static Activity Login_act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (new StoreManager().DataExists(this) == true){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_log_in);

        //Toast.makeText(this, "File: " + new StoreManager().DataExists(this),Toast.LENGTH_LONG).show();

        editText_userName = (EditText) findViewById(R.id.editText_name);
        editText_password = (EditText) findViewById(R.id.editText_password);

        TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        uuid = tManager.getDeviceId();
        version = BuildConfig.VERSION_NAME;
        model = Build.MODEL;

        Login_act = this;



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void button_login_click(View view){

        userName = editText_userName.getText().toString();
        password = editText_password.getText().toString();

        if(userName != "" && password != "") {
            new NetworkAdapter(this).execute();
        }else{
            Toast.makeText(view.getContext(), "Inserisci le tue credenziali", Toast.LENGTH_LONG).show();
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
            HttpPost httppost = new HttpPost("http://vas-val.gmatica.it/api/App");

            String postResult = "";

            try
            {

                JSONObject jsonObject = new JSONObject();

                jsonObject.put("UserName",userName);
                jsonObject.put("Password",password);
                jsonObject.put("UUID",uuid);
                jsonObject.put("Model",model);
                jsonObject.put("Version", version);

                StringEntity entityObj = new StringEntity("=" + jsonObject.toString(), HTTP.UTF_8);

                httppost.setEntity(entityObj);

                httppost.setHeader("User-Agent", "Fiddler");
                httppost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

                httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Custom user agent");

                HttpResponse response = httpclient.execute(httppost);


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
            //EditText et = (EditText)findViewById(R.id.my_edit);
            //et.setText("In Esecuzione");

            //super.onPreExecute();
            //progress.setProgress(0);
            //progress.show();
            //Toast.makeText(mContext, "Inizio",   Toast.LENGTH_SHORT).show();
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
