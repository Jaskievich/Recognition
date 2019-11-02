package com.example.recognition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by victor on 19.11.2018.
 */

public class ListViewActivity extends Activity {


    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        // находим список
        ListView lvMain = (ListView) findViewById(R.id.lvMain);

     //   list = new ArrayList<Currency>();
        Bundle extras = getIntent().getExtras();
        ArrayList<CValueItem> list = extras.getParcelableArrayList("arraylist");

        if(list == null) return;

        // создаем адаптер
        CurrencyAdapter adapter = new CurrencyAdapter(this, list);

        // присваиваем адаптер списку
        lvMain.setAdapter(adapter);

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
             //   CValueItem item = (CValueItem)parent.getAdapter().getItem(position);
                Intent intent = new Intent();
            //    intent.putExtra("currency", item);
                intent.putExtra("position", position);
                setResult(RESULT_OK, intent);
                finish();

            }
        });

    }


    public class CurrencyAdapter extends ArrayAdapter<CValueItem> {
        public CurrencyAdapter(Context context, ArrayList<CValueItem> list) {
            super(context, android.R.layout.simple_list_item_1, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            CValueItem item = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.simple_list_item_1, parent, false);
            }
            // Lookup view for data population
            TextView tvName = (TextView) convertView.findViewById(R.id.textView1);
            TextView tvDescr = (TextView) convertView.findViewById(R.id.textView2);
            TextView tvCoeff = (TextView) convertView.findViewById(R.id.textView3);
            // Populate the data into the template view using the data object
            tvName.setText(item.getName());
            tvDescr.setText(item.getDescript());
            tvCoeff.setText(Float.toString(item.getCoeff()));
            // Return the completed view to render on screen
            return convertView;
        }
    }
}
