package com.example.administrator.myaidldemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private IMyService iMyService;
    private TextView info_tv;
    private Button add_btn;
    private Button show_btn;
    private Button exit_btn;
    private int index = 0;
    private String info_str;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iMyService = IMyService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iMyService = null;
        }
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        info_tv = (TextView) findViewById(R.id.info_tv);
        add_btn = (Button) findViewById(R.id.add_person_btn);
        show_btn = (Button) findViewById(R.id.show_data_btn);
        exit_btn = (Button) findViewById(R.id.exit_btn);
        Intent bindIntent = new Intent(MainActivity.this,RemoteService.class);
        bindService(bindIntent,serviceConnection,BIND_AUTO_CREATE);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Person person = new Person();
                index = index + 1;
                person.setName("Person" + index);
                person.setAge(20);
                person.setTelNumber("12345689");
                if (iMyService != null){
                    try {
                        iMyService.savePersonInfo(person);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        show_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iMyService != null){
                    try {
                        List<Person>  personList = iMyService.getAllPerson();
                        if ((personList.size()> 0)&&(personList != null));
                        for (int i = 0;i<personList.size();i++){
                             info_str = personList.get(i).getName()+"\n";
                        }
                        info_tv.setText(info_str);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
