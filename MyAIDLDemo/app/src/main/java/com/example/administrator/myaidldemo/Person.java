package com.example.administrator.myaidldemo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/5/10.
 */
public class Person implements Parcelable{

    private String name;
    private String telNumber;
    private int age;

    public Person() {}

    public Person(Parcel pl){
        name = pl.readString();
        telNumber = pl.readString();
        age = pl.readInt();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(telNumber);
        dest.writeInt(age);
    }

    public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {

        @Override
        public Person createFromParcel(Parcel source) {
            return new Person(source);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }

    };
}
