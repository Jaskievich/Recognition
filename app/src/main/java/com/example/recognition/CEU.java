package com.example.recognition;

import android.content.Context;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class CEU extends CCtrlListValue {

    private  static CEU instance = null;


    private CEU(TextView textViewResult, Context context) {
        super(textViewResult, context);
        create();

    }


    public static CCtrlListValue getInstance(TextView textViewResult, Context context){
        if( instance == null) instance = new CEU(textViewResult, context);
        instance.selectItem(0);
        return instance;

    }


    @Override
    public void create() {

        try {
            XmlPullParser xpp = context.getResources().getXml(R.xml.eu);
            boolean inEntry = false;
            CValueItem value = null;
            int id = 0;
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {

                    // начало тэга
                    case XmlPullParser.START_TAG:
                        if(xpp.getName().equals("eu") && xpp.getAttributeCount() > 0){
                            inEntry = true;
                            id++;
                            value = new CValueItem();
                            value.setId(id);
                            value.setName(xpp.getAttributeValue(0));
                        }
                        break;
                    // конец тэга
                    case XmlPullParser.END_TAG:
                        if(inEntry){
                            inEntry = false;
                            list.add(value);
                        }
                        break;
                    // содержимое тэга
                    case XmlPullParser.TEXT:
                        if(inEntry){
                            value.setCoeff(Float.parseFloat(xpp.getText()));
                        }
                        break;

                    default:
                        break;
                }
                // следующий элемент
                xpp.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
