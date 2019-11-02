package com.example.recognition;

import android.os.Parcel;
import android.os.Parcelable;

public class CValueItem implements Parcelable
{

    private int id;
    private String name ;
    private String descript ;
    private float coeff;

    protected CValueItem(Parcel in) {
        id = in.readInt();
        name = in.readString();
        descript = in.readString();
        coeff = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(descript);
        dest.writeFloat(coeff);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CValueItem> CREATOR = new Creator<CValueItem>() {
        @Override
        public CValueItem createFromParcel(Parcel in) {
            return new CValueItem(in);
        }

        @Override
        public CValueItem[] newArray(int size) {
            return new CValueItem[size];
        }
    };

    public void setName(String name) {
        this.name = name;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }


    public CValueItem(){

    }

    public float getCoeff(){
        return coeff;
    }

    public void setCoeff(float coeff){
        this.coeff = coeff;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescript() {
        return descript;
    }


    public String toString(){
        StringBuilder strb = new StringBuilder();
        strb.append(getCoeff());
        strb.append(" ");
        strb.append(name);
        return strb.toString();
    }
}

