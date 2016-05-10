# MyALDLDemo
一.首先介绍下Service 的类别，他包括本地服务和远程服务两种；

Local Service :Local Service 是运行在主进程的 main 线程上的。如：onCreate，onStart 这些函数在被系统调用的时候都是在主进程的 main 线程上运行的。

        优点：在应用的主进程中运行，没有向系统要求开启额外进程，一定程度上节约了系统资源，它与Activity在同一进程中，他们的通信不需要IPC(进程间通信)，不需要AIDL(Android接口定义语言),使用bindService 进行通信会方便很多。

        缺点：主进程被Kill, Local服务也就停止了。

        应用：一些音乐播放的软件

Remote Service:Remote Service 则是运行在独立进程的 main 线程上。

        优点：运行于独立进程，Activity所在进程被kill 的时候，该服务还在运行不受其他进程影响。利于为多个进程提供服务，灵活性高。

        缺点：独立进程，会占用一定的系统资源，与其他进程的通信需使用AIDL。

      应用：提供一些系统服务的service ,该service 常驻在系统。


二.Service 的使用方法

        1.startService 启动一个服务

         <1.定义一个类继承Service
         <2.在Manifest.xml文件中配置该Service（与Activity在同级标签下）
         <3.使用Context的startService(Intent)方法启动该Service
         <4.不再使用时，调用stopService(Intent)方法停止该服务

        service的基本调用方式，重写onCreate()、onStartCommand()和onDestroy()方法。
onCreate()只启动一次，onStartCommand()会在每次启动activity时候运行，onDestroy()只在service关闭时候运行。

       说明：如果服务已经开启，不会重复的执行onCreate()， 而是会调用onStart()和onStartCommand()。
服务停止的时候调用 onDestory()。服务只会被停止一次。
       特点：一旦服务开启跟调用者(开启者)就没有任何关系了。开启者退出了，开启者挂了，服务还在后台长期的运行。开启者不能调用服务里面的方法。

        2.binderService 启动一个服务

        <1.定义一个类继承Service
        <2.在Manifest.xml文件中配置该Service
        <3.使用Context的bindService(Intent, ServiceConnection, int)方法启动该Service
        <4.不再使用时，调用unbindService(ServiceConnection)方法停止该服务

使用这种start方式启动的Service的生命周期如下：
onCreate() --->onBind()--->onunbind()--->onDestory()

注意：绑定服务不会调用onstart()或者onstartcommand()方法
特点：bind的方式开启服务，绑定服务，调用者挂了，服务也会跟着挂掉。
绑定者可以调用服务里面的方法。

三. Service与Activity的通信

  1.Service 与Activity 在同一个进程中

      <1.通过binder实现通信

       下面是一个使用bindService 启动的Service ,让Activity 获取Service里的一个字符串设置到界面一个TextView 里面，当然，实际开发过程中不会这个做，不过这是一个使用Binder类来进行Activity与service数据通信的很好例子。

MainActivity 类：

public class MainActivity extends AppCompatActivity {

    private MyServiceConn mconn;
    private MyService.IConnecter iConnecter;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.helloworld_tv);
        Intent serviceIntent = new Intent(MainActivity.this,MyService.class);
        mconn = new MyServiceConn();
        bindService(serviceIntent,mconn,BIND_AUTO_CREATE);

    }

    private class MyServiceConn implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
                //service 是IBinder的一个引用
               //Binder 实现了IBinder 
              //MyBinder是Binder的子类 ,这里涉及了一个父类引用指向子类对象的问题
             //  所以可以进行强制类型转换
            iConnecter = (MyService.IConnecter) service;
            String mymsg = iConnecter.invokeMethodInMyService();
            textView.setText(mymsg);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}

MyService 类：

public class MyService extends Service {

    private String TAG = "MyService";
    private String dataString ="This is a message in Service";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG,"onBind");
        return new MyBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,"onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public String getServiceData(){
        int i = 100;
        Log.e(TAG,"getServiceData"+dataString);
        return dataString+"all message"+i;

    }

    /**
     * 该类用于在onBind方法执行后返回的对象，
     * 该对象对外提供了该服务里的方法
    */



 private class MyBinder extends Binder implements IConnecter{


        @Override
        public String invokeMethodInMyService() {
           String msg = getServiceData();
            return msg;
        }
    }

    public interface IConnecter{

         String invokeMethodInMyService();
    }


}

就这么简单，甚至可以通过MyBinder 返回MyService 的实例，Activitiy中直接使用这个实例来调用MyService 中的方法。

绑定本地服务调用方法的步骤：
    1.在服务的内部创建一个内部类 提供一个方法，可以间接调用服务的方法
    2.实现服务的onbind方法，返回的就是这个内部类
    3.在activity 绑定服务。bindService();
    4.在服务成功绑定的回调方法onServiceConnected， 会传递过来一个 IBinder对象
    5.强制类型转化为自定义的接口类型，调用接口里面的方法。

    <2.通过广播，这里不再赘述。

    <3.自定义接口回调

总结：可是归根到底仍然只是一种方式，都是借助于IBinder暴露Service中的相应操作。网上还有些文章中提到用观察者模式，观察者模式也是接口回调的一种方式，xUtils框架中的下载直接使用Service中的静态方法来获取了下载管理器DownloadManager，它的这种方式是直接在Activity或者Fragment中使用了DownloadService，然后在获取下载管理器DownloadManager的方法中再启动Service，简单来说就是调用一个类的静态方法，这种方式跟面向对象中在一个类中调用另一个类的静态方法一样，不需要借助任何纽带桥梁直接创建调用，应用程序之间通信，大概就这么两种形式，一种是通过一个桥梁纽带来传递数据，另外一种就是直接在调用者中使用被调用者。

2.Service 与Activity 不再同一进程中，即Service是一个远程服务。

       远程服务的申明：AndroidMainifest.xml中

<service android:name="com.example.administrator.myservicetestdemo.MyService"
    android:process=":remoteTest">
    <intent-filter>
        <action android:name="com.example.administrator.myservicetestdemo.MyService" />
    </intent-filter>
</service>

原先使用binderService 绑定服务成功后 回调onServiceConnected 方法获取的IBinder对象将不能进行强制类型转换。

下面来看使用AIDL来实现跨进程通信：

1.首先来了解下AIDL：

       AIDL (Android Interface Definition Language) 是一种IDL 语言，用于生成可以在Android设备上两个进程之间进行进程间通信(interprocess communication, IPC)的代码。如果在一个进程中（例如Activity）要调用另一个进程中（例如Service）对象的操作，就可以使用AIDL生成可序列化的参数。

        AIDL接口文件，和普通的接口内容没有什么特别，只是它的扩展名为.aidl。保存在src目录下。如果其他应用程序需要IPC，则那些应用程序的src也要带有这个文件。Android SDK tools就会在gen目录自动生成一个IBinder接口文件。service必须适当地实现这个IBinder接口。那么客户端程序就能绑定这个service并在IPC时从IBinder调用方法。
每个aidl文件只能定义一个接口，而且只能是接口的声明和方法的声明。

 2.使用AIDL的场合：
       官方文档特别提醒我们何时使用AIDL是必要的：只有你允许客户端从不同的应用程序为了进程间的通信而去访问你的service，以及想在你的service处理多线程。如果不需要进行不同应用程序间的并发通信(IPC)，you should create your interface by implementing a Binder；或者你想进行IPC，但不需要处理多线程的，则implement your interface using a Messenger。

3.开始实现Activity 与远程 service的通信

<1.创建.aidl文件

       android studio 中aidl 文件需放在java 同级目录的一个aidl 文件夹里，文件路劲与AndroidMainifast.xml 的包名一致

                                          

然后点击build-->Make Project 这样就在D:\astestdemo\MyAIDLDemo\app\build\generated\source\aidl\debug\com\example\administrator\myaidldemo路劲下编译生成了IMyService.java 文件。

<2.实现aidl 接口

         创建一个类实现刚才那个aidl的接口：

public class RemoteService extends Service {

    private LinkedList<Person> personList = new LinkedList<Person>();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IMyService.Stub mBinder = new IMyService.Stub(){

        @Override
        public void savePersonInfo(Person person) throws RemoteException {

            if (person != null){
                personList.add(person);
            }

        }

        @Override
        public List<Person> getAllPerson() throws RemoteException {
            return personList;
        }
    };

}


这里会看到有一个名为IMyService.Stub类，查看aidl文件生成的Java文件源代码就能发现有这么一段代码

public static abstract class Stub extends android.os.Binder implements com.example.administrator.myaidldemo.IMyService

原来Stub类就是继承于Binder类，也就是说RemoteService类和普通的Service类没什么不同，只是所返回的IBinder对象比较特别，是一个实现了AIDL接口的Binder。

<3.创建需要传输的数据 Bean——

       Person类  是一个序列化的类，这里使用Parcelable 接口来序列化，是Android提供的一个比Serializable 效率更高的序列化类。

 对于实现AIDL接口，官方还提醒我们：
    1. 调用者是不能保证在主线程执行的，所以从一调用的开始就需要考虑多线程处理，以及确保线程安全；
    2. IPC调用是同步的。如果你知道一个IPC服务需要超过几毫秒的时间才能完成地话，你应该避免在Activity的主线程中调用。也就是IPC调用会挂起应用程序导致界面失去响应，这种情况应该考虑单独开启一个线程来处理。
    3. 抛出的异常是不能返回给调用者（跨进程抛异常处理是不可取的）。

<4.客户端获取接口

       也就是MainActivity 如何获取到启动远程服务成功后返回的IBinder 对象呢,通IMyService.Stub.asInterface(service)来得到IMyService对象,获取到这个远程服务的接口对象后就可以使用这个接口的方法来传递数据。

实例代码实现功能是这样的，点击ADD DATA ,创建一个Person对象，通过

iMyService.savePersonInfo(person);

保存这个对象（这里实现了MainActivity 把数据传递到RemoteService 去save），点击SHOW DATA ,可以(让RemoteService 读取数据并传递到MainActivity 中显示，从而模拟了Activity与service 的双向通信)。

直接上源码吧，点击下载！

尊重原创参考：http://android.blog.51cto.com/268543/537684/
