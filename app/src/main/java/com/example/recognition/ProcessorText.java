package com.example.recognition;

import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

/**
 * Created by victor on 09.02.2018.
 */


class ProcessorText implements Detector.Processor<TextBlock> {


    private IMyService myServise;


    public ProcessorText( IMyService myServise){
        this.myServise = myServise;
    }

    public void  setMyServise(IMyService myServise)
    {
        this.myServise = myServise;
    }


    @Override
    public void release() {

    }

    private SparseArray<TextBlock> items = null;

    @Override

    public void receiveDetections(Detector.Detections<TextBlock> detections)
    {
        Log.d("Main", "receiveDetections");
        items = detections.getDetectedItems();

        StringBuilder value = new StringBuilder();

        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            for (Text currentLine : item.getComponents()) {

                for (Text currentText : currentLine.getComponents()) {

                    value.append(currentText.getValue());
                    value.append(" ");
                }
            }
        }
        myServise.handleValue(value);

    }
  //  */

}

