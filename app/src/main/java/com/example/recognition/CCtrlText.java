package com.example.recognition;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.TextView;

class CServiseText implements IMyService{

    private static CServiseText instance = null;
    private final TextView textViewResult;
    private boolean isHandle = false;
    protected Context context;

    private CServiseText(TextView textViewResult, Context context)
    {
        this.textViewResult = textViewResult;
        this.context = context;
    }

    @Override
    public void handleValue(StringBuilder val) {
        isHandle = true;
        if(val.length() == 0){

            textViewResult.append("\n Не распознано");
            return;
        }
        textViewResult.setText(val.toString());
        ClipboardManager clipboard = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text", val.toString());
        clipboard.setPrimaryClip(clip);
    }

    @Override
    public boolean isHandle() {
        return isHandle;
    }

    @Override
    public void printTextResult() {

    }

    @Override
    public void printTextBegin() {
        isHandle = false;
        textViewResult.setText("");
    }

    public static CServiseText getInstance(TextView textViewResult, Context context){
        if( instance == null) instance = new CServiseText(textViewResult, context);
        return instance;
    }
}


public class CCtrlText extends CCtrlValue {

    private  static CCtrlText instance = null;


    private  CCtrlText(TextView textViewResult, Context context) {
        super(context);
        myServise =  CServiseText.getInstance(textViewResult, context);
    }

    public static CCtrlValue getInstance(TextView textViewResult, Context context){
        if( instance == null) instance = new CCtrlText(textViewResult, context);
        instance.getMyServise().printTextBegin();
        return instance;
    }
}
