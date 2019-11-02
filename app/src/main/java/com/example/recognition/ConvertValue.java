package com.example.recognition;


import android.widget.TextView;

import java.util.Locale;

public class ConvertValue implements IMyService {

    private final StringBuilder stringBuilder;
    private CValueItem valueItem = null;
    private final TextView textViewResult;
    private boolean isHandle = false;

    private static ConvertValue instance = null;

    private ConvertValue(TextView textViewResult){

        stringBuilder = new StringBuilder();

        this.textViewResult = textViewResult;
    }

    public static ConvertValue getInstance(TextView textViewResult){
        if( instance == null) instance = new ConvertValue(textViewResult);
        return instance;
    }


    private double selectNumberFromStr(final String val)
    {
        double res = 0.0, sp = 1.0;
        boolean isPoint = false;
        final char [] arV = val.toCharArray();
        for(int i = 0; i < arV.length ; ++i) {
            if (arV[i] >= '0' && arV[i] <= '9') {
                int symbol = Character.getNumericValue(arV[i]);
                if( isPoint ) {
                    sp/=10;
                    sp*= symbol;
                    res += sp;
                }
                else
                    res =res * 10 + symbol;
            }
            else if(arV[i] == '.' || arV[i]==',') isPoint = true;
            else if(res > 0.0) break;
        }
        return  res;
    }

  //  @Override
    public void handleValue(StringBuilder val) {

        printTextBegin();
        if(val.length() == 0){
            isHandle = true;
            textViewResult.append("\n Не распознано");
            return;
        }
        final String sVal = val.toString().trim();

        stringBuilder.setLength(0);
        stringBuilder.append("=>");
        stringBuilder.append(val);
        stringBuilder.append("<=\n");

        double dVal = selectNumberFromStr(sVal);
        if (dVal > 0.0) {
            stringBuilder.append("=>").append(dVal).append("<=\n");
            stringBuilder.append("=>").append(valueItem.toString()).append("<=\n");
            dVal /= valueItem.getCoeff();
            stringBuilder.append("=>").append(String.format(Locale.ENGLISH, "%.2f", dVal)).append("<=\n");
        }
        printTextResult();
        isHandle = true;
    }

  //  @Override
    public boolean isHandle() {
        return isHandle;
    }

 //   @Override
    public void printTextResult() {
        if(valueItem == null) return ;
        textViewResult.append(stringBuilder.toString());
    }

 //   @Override
    public void printTextBegin() {
        isHandle = false;
        //   textViewResult.setText(rate.toString()+"\n");
        textViewResult.setText(valueItem.toString());
    }


    public void SetValueItem(CValueItem valueItem )
    {
        this.valueItem = valueItem;
        printTextBegin();
    }



}