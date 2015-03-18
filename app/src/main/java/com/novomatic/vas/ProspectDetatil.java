package com.novomatic.vas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class ProspectDetatil extends Activity {

    public static Prospect prospect = null;
    private int prospect_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prospect_detatil);

        Intent intent = getIntent();
        prospect_id = intent.getIntExtra("prospect_id",0);

        this.PupulateProspect();

    }

    private void PupulateProspect(){
        StoreManager storeManager = new StoreManager();

        if (prospect == null) {
            prospect = new ParseResponseWebAPI
                    (storeManager.GetData(this)).GetProspectById(prospect_id);
        }

        TextView denominazione = (TextView) findViewById(R.id.textView_profile_name);
        TextView indirizzo = (TextView) findViewById(R.id.textView_profile_ind);
        TextView telefono = (TextView) findViewById(R.id.textView_profile_tel);

        indirizzo.setText(prospect.getIndirizzo());
        telefono.setText(prospect.getTelefono());
        denominazione.setText(prospect.getDenominazione());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_prospect_detatil, menu);

        int contratti = prospect.getContratti();

        if (contratti > 0) {
            menu.add(0,0,0, "Visualizza contratto").setIcon(R.drawable.ic_action_paste)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }


        if (contratti == 0) {
            menu.add(0,1,0, "Genera contratto").setIcon(R.drawable.ic_action_new_label)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            menu.add(0,2,0, "Modifica dati").setIcon(R.drawable.ic_shopping)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        */

        switch (id){
            case 0:
                Toast.makeText(this,"Visualizza contratto",Toast.LENGTH_LONG).show();
                break;
            case 1:
                Toast.makeText(this,"Genera contratto",Toast.LENGTH_LONG).show();
                break;
            case 2:
                //Toast.makeText(this,"Modifica dati",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, ProspectEdit.class);
                intent.putExtra("prospect_id", prospect.getProspectID());
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
