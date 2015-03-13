package com.novomatic.vas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class ProspectDetatil extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prospect_detatil);

        Intent intent = getIntent();
        int prospect_id = intent.getIntExtra("prospect_id",0);

        StoreManager storeManager = new StoreManager();

        Prospect prospect = new ParseResponseWebAPI
                (storeManager.GetData(this)).GetProspectById(prospect_id);

        TextView denominazione = (TextView) findViewById(R.id.textView_profile_name);

        denominazione.setText(prospect.getDenominazione());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_prospect_detatil, menu);
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
}
