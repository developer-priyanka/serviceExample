package my.assignment.serviceexample;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by root on 9/19/16.
 */

public class MyService extends Service {

    private final IBinder myBinder = new MyLocalBinder();
    private Thread backgroundThread;
    private MediaPlayer player;
    private String TAG = "bound";


    public MyService() {

        super();
    }

    @Override
    public void onCreate() {

        super.onCreate();
        backgroundThread = new Thread(new Runnable() {
            @Override
            public void run() {
//          do the work in a separate thread so main thread is not blocked
                Log.i(TAG, "Thread running");
                playMusic();
            }
        });
        backgroundThread.start();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started by startService()");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        //release player and thread
        Log.i(TAG, "Destroying Service");
        Toast.makeText(this, "Destroying Service...", Toast.LENGTH_SHORT).show();
        player.release();
        player = null;
        Thread dummy = backgroundThread;
        backgroundThread = null;
        dummy.interrupt();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return myBinder;
    }


    //    the class used for the client Binder
    public class MyLocalBinder extends Binder {
        MyService getService() {
           /* return this instance of the BoundService
            so the client can access the public methods*/
            return MyService.this;
        }
    }
    private void playMusic() {
        if (player != null) {
            player.release();
        }
        //        sound clip in res/raw folder
        player = MediaPlayer.create(this,R.raw.ring_tone);
        player.setLooping(true);
    }

    //    start play
    public void startPlay() {
        if (!player.isPlaying()) {
            player.start();
        }
    }

    //    stop play
    public void stopPlay() {
        if (player.isPlaying()) {
            player.pause();
        }
    }
}
