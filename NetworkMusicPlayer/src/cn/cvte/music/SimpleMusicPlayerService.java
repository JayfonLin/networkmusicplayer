package cn.cvte.music;

import java.io.IOException;
import java.util.List;

import cn.cvte.activities.PlayerActivity;
import cn.cvte.network.TCPClient;
import cn.cvte.network.TCPServer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

public class SimpleMusicPlayerService extends Service 
implements MediaPlayer.OnCompletionListener, MPSubject{
	
	public enum STATE{
        IDLE, STOP, PALYING, PAUSE;
        @Override
        public String toString(){
        	switch(this){
        	case IDLE:
        		return "IDLE";
        	case STOP:
        		return "STOP";
        	case PALYING:
        		return "PLAYING";
        	case PAUSE:
        		return "PAUSE";
        	}
        	return "IDLE";
        }
        public static STATE stringToState(String str){
        	if ("IDLE".equalsIgnoreCase(str)){
        		return STATE.IDLE;
        	}else if ("STOP".equalsIgnoreCase(str)){
        		return STATE.STOP;
        	}else if ("PLAYING".equalsIgnoreCase(str)){
        		return STATE.PALYING;
        	}else if ("PAUSE".equalsIgnoreCase(str)){
        		return STATE.PAUSE;
        	}
        	return STATE.IDLE;
        }
    }
	
    MediaPlayer mediaPlayer = new MediaPlayer();
    STATE state = STATE.IDLE;
    private final IBinder binder = new SMPlayerBinder();
    String curPath;
    List<MusicInfo> musicList;
    Handler secondsHandler = new Handler();
    
    @Override
    public IBinder onBind(Intent intent) {
        
        state = STATE.IDLE;
        mediaPlayer.setOnCompletionListener(this);
        return binder;
        
    }
    
    public class SMPlayerBinder extends Binder{
        public SimpleMusicPlayerService getService(){
            return SimpleMusicPlayerService.this;
        }
    }
    private Runnable r = new Runnable() {
        
        public void run() {
            notifyObserver();
            secondsHandler.postDelayed(r, 1000);
        }
    };
    public void notifyRemoteDevices(){
    	if (TCPServer.pt != null){
    		synchronized (TCPServer.pt) {
    			TCPServer.pt.unblockWrite(TCPServer.pt.getState());
			}
    	}
    }
    public void setMusicList(List<MusicInfo> list){
    	musicList = list;
    }
    public String getCurPath(){
    	return curPath;
    }
    public void playOrPause(String path){
    	if (path != null){
    		try{
                mediaPlayer.reset();
                mediaPlayer.setDataSource(path);
                curPath = path;
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
    	notifyRemoteDevices();
    }
    public void stop(){
    	mediaPlayer.stop();
        try {
        	mediaPlayer.prepare();
            
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.seekTo(0);
        state = STATE.STOP;
        notifyRemoteDevices();
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
    	secondsHandler.removeCallbacks(r);
        mediaPlayer.release();
        super.onDestroy();
    }
    public void onCompletion(MediaPlayer mp) {
        stop();
        String path = null;
        for (int i = 0; i < musicList.size(); ++i){
        	if (musicList.get(i).data.equals(getCurPath())){
        		path = musicList.get((i+1)%musicList.size()).data;
        	}
        }
        playOrPause(path);
        notifyRemoteDevices();
    }
    @Override
    public void onCreate() {
    	secondsHandler.post(r);
        super.onCreate();
    }
    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
	@Override
	public void registerObserver(MPObserver observer) {
		observerList.add(observer);
	}
	@Override
	public void removeObserver(MPObserver observer) {
		observerList.remove(observer);
	}
	@Override
	public void notifyObserver() {
		for (MPObserver observer: observerList){
			observer.update(getCurPath(), getState(), getCurrentPosition(), getDuration());
		}
	}
}
