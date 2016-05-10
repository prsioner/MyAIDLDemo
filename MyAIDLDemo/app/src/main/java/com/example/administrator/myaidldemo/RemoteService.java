package com.example.administrator.myaidldemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/10.
 */
public class RemoteService extends Service {

    private LinkedList<Person> personList = new LinkedList<Person>();
    private String TAG = "RemoteService";

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG,"onBind");
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,"onCreate()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"onDestroy");
    }

    private final IMyService.Stub mBinder = new IMyService.Stub(){

        @Override
        public void savePersonInfo(Person person) throws RemoteException {

            if (person != null){
                Log.e(TAG,"save data");
                personList.add(person);
            }
        }
        @Override
        public List<Person> getAllPerson() throws RemoteException {
            Log.e(TAG,"get data");
            return personList;
        }
    };

}
