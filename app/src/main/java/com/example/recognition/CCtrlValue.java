package com.example.recognition;

import android.content.Context;

/**
 *
 */

public abstract class CCtrlValue{

    protected IMyService myServise;
    protected Context context;

    CCtrlValue( Context context){
        this.context = context;
    }

    CCtrlValue(IMyService myServise, Context context){
        this.setMyServise(myServise);
        this.context = context;
    }


    public IMyService getMyServise() {
        return myServise;
    }

    public void setMyServise(IMyService myServise) {
        this.myServise = myServise;
    }
}
