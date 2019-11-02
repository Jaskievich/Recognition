package com.example.recognition;

import android.content.Context;
import android.widget.TextView;

import java.util.ArrayList;

public abstract class CCtrlListValue extends CCtrlValue {

    protected ArrayList<CValueItem> list;

    protected CCtrlListValue(TextView textViewResult, Context context) {
        super(context);
        myServise =  ConvertValue.getInstance(textViewResult);
        list = new ArrayList<>();
    }

    public void selectItem(int position) {

        if(position < list.size()) {
            ((ConvertValue)getMyServise()).SetValueItem(list.get(position));
        }
    }
    public abstract void create();
    public ArrayList<CValueItem> getList(){
        return list;
    }

    public int findIndexByDiscr(String discr){
        for(int i = 0; i < list.size(); ++i)
            if(list.get(i).getDescript()== discr) return i;
        return -1;
    }

}
