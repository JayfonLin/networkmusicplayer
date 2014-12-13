package cn.cvte.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;


import cn.cvte.activities.MPApplication;
import cn.cvte.music.MusicFile;
import cn.cvte.music.MusicInfo;
import cn.cvte.music.SimpleMusicPlayerService.STATE;

public class ProcessThread implements Runnable{
	
	Socket socket;
	BufferedReader inFromClient;
	PrintWriter outToClient;
	String clientSentence;
	Handler mHandler;
	
	public ProcessThread(Socket pSocket){
		socket = pSocket;
		try {
			inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outToClient = new PrintWriter(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while (MPApplication.online){
			try {
				clientSentence = inFromClient.readLine();
				if (clientSentence != null){
					System.out.println(clientSentence);
					
					if ("request_music_list".equals(clientSentence)){
						requestMusicList();
					}else if ("select_music".equals(clientSentence)){
						clientSentence = inFromClient.readLine();
						System.out.println("will play "+clientSentence);
						selectMusic(clientSentence);
					}else if ("pause".equals(clientSentence)){
						pause();
					}else if ("stop".equals(clientSentence)){
						stop();
					}else if ("play".equals(clientSentence)){
						play();
					}else if ("request_state".equals(clientSentence)){
						
						blockWrite(getState());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getState(){
		STATE state = MPApplication.smpService.getState();
		StringBuilder sb = new StringBuilder("return_state_result\n");
		sb.append("success\n");
		sb.append(MPApplication.smpService.getCurPath());
		sb.append("\n");
		sb.append(state);
		sb.append("\n");
		return sb.toString();
	}
	private void stop(){
		if (MPApplication.smpService.getState() != STATE.STOP){
			if (mHandler != null){
				mHandler.sendEmptyMessage(2);
			}else{
				MPApplication.smpService.stop();
			}
		}
		StringBuilder sb = new StringBuilder("stop_result\n");
		sb.append("success\n");
		blockWrite(sb.toString());
	}
	private void play(){
		if (MPApplication.smpService.getState() != STATE.PALYING){
			if (mHandler != null){
				mHandler.sendEmptyMessage(1);
			}else{
				MPApplication.smpService.playOrPause(null);
			}
		}
		StringBuilder sb = new StringBuilder("play_result\n");
		sb.append("success\n");
		blockWrite(sb.toString());
	}
	private void requestMusicList(){
		StringBuilder returnStr = new StringBuilder();
		returnStr.append("return_music_list\n");
		returnStr.append(constructMusicList().toString());
		returnStr.append("\n");
		blockWrite(returnStr.toString());
	}
	private void selectMusic(String path){
		StringBuilder returnStr = new StringBuilder();
		if (mHandler != null){
			Message msg = new Message();
			msg.what = 0;
			msg.obj = path;
			mHandler.sendMessage(msg);
		}else{
			MPApplication.smpService.playOrPause(path);
		}
		returnStr.append("select_music_result\n");
		
		returnStr.append("success\n");
		returnStr.append(path);
		returnStr.append("\n");
		blockWrite(returnStr.toString());
	}
	
	private void pause(){
		if (MPApplication.smpService.getState() == STATE.PALYING){
			if (mHandler != null){
				mHandler.sendEmptyMessage(1);
			}else{
				MPApplication.smpService.playOrPause(null);
			}
		}
		StringBuilder sb = new StringBuilder("pause_result\n");
		sb.append("success\n");
		blockWrite(sb.toString());
	}
	
	private void blockWrite(String str){
		if (outToClient != null){
			synchronized (outToClient) {
				outToClient.print(str);
				outToClient.flush();
			}
		}
	}
	
	public void unblockWrite(final String str){
		if (outToClient != null){
			synchronized (outToClient) {
				Thread t = new Thread(new Runnable() {
					
					@Override
					public void run() {
						outToClient.print(str);
						outToClient.flush();
					}
				});
				t.start();
			}
		}
	}
	
	public void setHandler(Handler pHandler){
		mHandler = pHandler;
	}
	public void unRegisterHandler(Handler pHandler){
		if (mHandler == pHandler)
			mHandler = null;
	}
	
	private JSONArray constructMusicList(){
		List<JSONObject> list = new ArrayList<JSONObject>();
		for (MusicInfo mi: MusicFile.musicInfoList){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", mi.id);
			map.put("name", mi.name);
			map.put("artist", mi.artist);
			map.put("duration", mi.duration);
			map.put("size", mi.size);
			map.put("data", mi.data);
			
			JSONObject jo = new JSONObject(map);
			list.add(jo);
			
		}
		JSONArray ja = new JSONArray(list);
		return ja;
	}

}
