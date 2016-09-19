package my.assignment.serviceexample;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button playBtn,stopBtn;
    private MyService serviceReference;
    private int REQUEST_CODE = 101;
    private int NOTIFICATION_ID = 102;
    private boolean isBound;


    private String TAG = "bound";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playBtn=(Button)findViewById(R.id.startbtn);
        stopBtn=(Button)findViewById(R.id.stopbtn);

        //        start the service
        Log.i(TAG, "Service starting...");
        Intent start = new Intent(MainActivity.this, MyService.class);
        startService(start);


    }

    private ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // called when the connection with the service has been
            // established. gives us the service object to use so we can
            // interact with the service.we have bound to a explicit
            // service that we know is running in our own process, so we can
            // cast its IBinder to a concrete class and directly access it.
            Log.i(TAG, "Bound service connected");
            serviceReference = ((MyService.MyLocalBinder) service).getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // called when the connection with the service has been
            // unexpectedly disconnected -- its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            Log.i(TAG, "Problem: bound service disconnected");
            serviceReference = null;
            isBound = false;
        }
    };

    //    unbind from the service
    private void doUnbindService() {
        Toast.makeText(this, "Unbinding...", Toast.LENGTH_SHORT).show();
        unbindService(myConnection);
        isBound = false;
    }

    //    bind to the service
    private void doBindToService() {
        Toast.makeText(this, "Binding...", Toast.LENGTH_SHORT).show();
        if (!isBound) {
            Intent bindIntent = new Intent(this, MyService.class);
            isBound = bindService(bindIntent, myConnection,
                    Context.BIND_AUTO_CREATE);
        }
    }

    //    activity starting
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "MainActivity - onStart - binding...");
//        bind to the service
        doBindToService();
    }

    //    activity stopping
    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "MainActivity - onStop - unbinding...");
//        unbind from the service
        doUnbindService();
    }

    @Override
    public void onBackPressed() {
       /* we customise the back button so that the activity pauses
        instead of finishing*/
        moveTaskToBack(true);
    }

    //    the activity is being destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Destroying activity...");
       /* it's not just being destroyed to rebuild due to orientation
        change but genuinely being destroyed...for ever*/
        if (isFinishing()) {
            Log.i(TAG, "activity is finishing");
//            stop service as activity being destroyed and we won't use it any more
            Intent intentStopService = new Intent(this, MyService.class);
            stopService(intentStopService);
        }
    }




    public void playMusic(View view){
        if (isBound) {
            serviceReference.startPlay();
        }

    }

    public void stopMusic(View view){
        if (isBound) {
            serviceReference.stopPlay();
        }

    }
}
