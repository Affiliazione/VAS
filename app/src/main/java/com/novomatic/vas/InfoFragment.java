package com.novomatic.vas;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class InfoFragment extends Fragment implements View.OnClickListener {

    public static InfoFragment newInstance() {
        InfoFragment fragment = new InfoFragment();
        return fragment;
    }

    public InfoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info, container,
                false);

        
        LogIn.Login_act.finish();

        StoreManager storeManager = new StoreManager();

        String jsonMessage = storeManager.GetData(getActivity().getBaseContext());

        ParseResponseWebAPI parseResponseWebAPI = new ParseResponseWebAPI(jsonMessage);

        ArrayList<Prospect> prospects = parseResponseWebAPI.GetProspects();
        Profile profile = parseResponseWebAPI.GetProfile();

        TextView textView = (TextView) rootView.findViewById(R.id.textView_profile_name);

        textView.setText(profile.getNome() + " " + profile.getCognome());

        Button buttonInfo = (Button) rootView.findViewById(R.id.button_info);

        buttonInfo.setText(prospects.size() + " Punti vendita da gestire");
        buttonInfo.setOnClickListener(this);


        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(2);

    }

    @Override
    public void onClick(View view){

        FragmentManager fragmentManager = getFragmentManager();

        /*
        fragmentManager.beginTransaction()
                .replace(R.id.container, Prospects.newInstance())
                .commit();
        */
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
