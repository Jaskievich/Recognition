package com.example.recognition;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;


public class Valuta extends CCtrlListValue implements IDataConvert
{

    private static Valuta instance = null;

    private Valuta(TextView textViewResult, Context context) {

        super(textViewResult, context);
        create();
        selectItem(0);

    }



    public static CCtrlListValue getInstance(TextView textViewResult, Context context){
        if(instance == null) instance = new Valuta(textViewResult, context);
        instance.selectItem(0);
        return instance;
    }




    private boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnectedOrConnecting());

    }


    @Override
    public void create() {


        if (isOnline(context)) {

            final AsyncTask<String, Void, String> execute = new ProgressTask(this, context).execute("http://www.nbrb.by/API/ExRates/Rates?Periodicity=0");

        } else {
            CValueItem dol = new CValueItem();
            dol.setName("USA");
            dol.setId(45);
            dol.setCoeff(2.0f);
            ((ConvertValue)getMyServise()).SetValueItem(dol);
            Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void setData(String str) {
        StringBuilder buf = new StringBuilder(str);
        //   Currency valute = null;
        CValueItem valute = null;

        CParser parser = new CParser();
        parser.buf = buf;
        int ind1 = 0;
        while(true) {
            ind1 = parser.getFromStr(ind1, "Cur_ID\":", 8, ",");
            if(ind1 == -1) break;
            valute = new CValueItem();
            valute.setId(Integer.parseInt(parser.str_c));
            ind1 = parser.getFromStr(ind1, "Cur_Abbreviation\":\"", 19, "\",");
            if(ind1 == -1) break;
            valute.setDescript( parser.str_c );
            ind1 = parser.getFromStr(ind1, "Cur_Name\":\"", 11, "\",");
            if(ind1 == -1) break;
            valute.setName(parser.str_c);

            ind1 = parser.getFromStr(ind1, "Cur_OfficialRate\":", 18, "}");
            if(ind1 == -1) break;
            valute.setCoeff( Float.valueOf(parser.str_c) );

            list.add(valute);
        }

        int ind = findIndexByDiscr("USA");
        if( ind > 0)   ((ConvertValue)getMyServise()).SetValueItem(list.get(ind));


    }



    static class CParser {

        public String str_c;

        StringBuilder buf;

        public int getFromStr(int ind_s, final String MASK1, final int LEN , final String MASK2) {
            ind_s = buf.indexOf(MASK1, ind_s);
            if (ind_s == -1) return -1;
            int ind2 = buf.indexOf(MASK2, ind_s);
            if (ind2 == -1) return -1;
            str_c = buf.substring(ind_s + LEN, ind2);
            return ind2;
        }

    }
}
