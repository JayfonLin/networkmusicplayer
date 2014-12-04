package cn.cvte.music;

import java.io.IOException;

import cn.cvte.activities.PlayerActivity;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

public class SimpleMusicPlayerService extends Service implements MediaPlayer.OnCompletionListener{
	
	public enum STATE{
        IDLE, STOP, PALYING, PAUSE
    }
	
    MediaPlayer mediaPlayer = new MediaPlayer();
    STATE state = STATE.IDLE;
    private final IBinder binder = new SMPlayerBinder();
    @Override
    public IBinder onBind(Intent intent) {
        try {
            mediaPlayer.reset();
            String path = intent.getStringExtra("path");
            if (path != null){
	            mediaPlayer.setDataSource(path);
	            mediaPlayer.prepare();
            }
            state = STATE.STOP;
            mediaPlayer.setOnCompletionListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return binder;
    }
    
    public class SMPlayerBinder extends Binder{
        public SimpleMusicPlayerService getService(){
            return SimpleMusicPlayerService.this;
        }
    }
    public void playOrPause(String path){
        if (state == STATE.IDLE){
            try{
                mediaPlayer.reset();
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.start();
                state = STATE.PALYING;
            }catch(IOException e){
                e.printStackTrace();
            }
        }else if (state == STATE.PALYING){
                mediaPlayer.pause();
                state = STATE.PAUSE;
        }else{
            mediaPlayer.start();
            state = STATE.PALYING;
        }
    }
    public void stop(){
        mediaPlayer.stop();
        try {
            mediaPlayer.reset();
            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //mediaPlayer.seekTo(0);
    }
    public int getDuration(){
        if (state == STATE.IDLE){
            return 0;
        }
        return mediaPlayer.getDuration();
    }
    public int getCurrentPosition(){
        if (state == STATE.IDLE){
            return 0;
        }
        return mediaPlayer.getCurrentPosition();
    }
    public void setCurrentPosition(float percentage){
        if (state != STATE.STOP){
                mediaPlayer.seekTo((int)(percentage*mediaPlayer.getDuration()));
        }
    }
    public void setState(STATE pState){
        state = pState;
    }
    public STATE getState(){
        return state;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
        super.onDestroy();
    }
    public void onCompletion(MediaPlayer mp) {
        mp.stop();
        try {
            mp.prepare();
            
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mp.seekTo(0);
        state = STATE.STOP;
    }
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }
    @Override
    public void onRebind(Intent intent) {
        // TODO Auto-generated method stub
        super.onRebind(intent);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        return super.onUnbind(intent);
    }
}
