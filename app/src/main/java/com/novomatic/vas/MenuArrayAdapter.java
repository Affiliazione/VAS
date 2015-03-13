package com.novomatic.vas;

/**
 * Created by fpirazzi on 27/02/2015.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MenuArrayAdapter extends ArrayAdapter<MenuItemDef> {
    private final Context context;
    private final ArrayList<MenuItemDef> values;

    public MenuArrayAdapter(Context context, ArrayList<MenuItemDef> values) {
        super(context, R.layout.menu_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.menu_item, parent, false);

        TextView firstLine = (TextView) rowView.findViewById(R.id.firstLine);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.menuIcon);

        firstLine.setText(values.get(position).getName());
        imageView.setImageResource(values.get(position).getIcon());

        return rowView;
    }
}
