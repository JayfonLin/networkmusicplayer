package cn.cvte.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;

import cn.cvte.activities.MPApplication;
import cn.cvte.music.MusicInfo;

public class TCPReaderThread extends Thread{
	BufferedReader inFromServer;
	PrintWriter outToServer;
	Handler mlHandler;
	public TCPReaderThread(BufferedReader reader, PrintWriter pw){
		inFromServer = reader;
		outToServer = pw;
	}
	@Override
	public void run() {
		while(MPApplication.online){
			String command;
			try {
				command = inFromServer.readLine();
				System.out.println("command: "+command);
				if ("return_music_list".equals(command)){
					getMusicList();
				}else if ("play_result".equals(command)){
					simpleResult(0);
				}else if ("pause_result".equals(command)){
					simpleResult(1);
				}else if ("stop_result".equals(command)){
					simpleResult(2);
				}else if ("select_music_result".equals(command)){
					selectMusic();
				}else if ("return_state_result".equals(command)){
					getMusicState();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	private void getMusicState() throws IOException{
		String result = inFromServer.readLine();
		String path = inFromServer.readLine();
		String state = inFromServer.readLine();
		Message msg = new Message();
		msg.what = 5;
		msg.obj = result;
		Bundle b = new Bundle();
		b.putString("path", path);
		b.putString("state", state);
		msg.setData(b);
		if (mlHandler != null){
			mlHandler.sendMessage(msg);
		}
	}
	private void selectMusic() throws IOException{
		String result = inFromServer.readLine();
		String path = inFromServer.readLine();
		Message msg = new Message();
		msg.what = 3;
		msg.obj = result;
		Bundle b = new Bundle();
		b.putString("path", path);
		msg.setData(b);
		if (mlHandler != null){
			mlHandler.sendMessage(msg);
		}
	}
	private void simpleResult(int what) throws IOException{
		String result = inFromServer.readLine();
		Message msg = new Message();
		msg.what = what;
		msg.obj = result;
		if (mlHandler != null){
			mlHandler.sendMessage(msg);
		}
	}
	private void write(String str){
		if (outToServer != null){
			synchronized (outToServer) {
				outToServer.print(str);
				outToServer.flush();
			}
		}
	}
	public void setHandler(Handler pHandler){
		mlHandler = pHandler;
	}
	public void removeHandler(Handler pHandler){
		if (mlHandler == pHandler){
			mlHandler = null;
		}
	}
	
	private List<MusicInfo> getMusicList() throws IOException{
		List<MusicInfo> list = new ArrayList<MusicInfo>();
		String jsonStr = inFromServer.readLine();
		System.out.println("return json:"+jsonStr);
		JSONArray ja;
		try {
			ja = new JSONArray(jsonStr);
			for (int i = 0; i < ja.length(); ++i){
				JSONObject jo = ja.getJSONObject(i);
				MusicInfo mi = new MusicInfo();
				mi.id = jo.getString("id");
				mi.artist = jo.getString("artist");
				mi.data = jo.getString("data");
				mi.duration = jo.getString("duration");
				mi.name = jo.getString("name");
				System.out.println("mi.name:"+mi.name);
				mi.size = jo.getString("size");
				list.add(mi);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Message msg = new Message();
		msg.what = 4;
		Bundle b = new Bundle();
		b.putParcelableArrayList("musicList", (ArrayList<? extends Parcelable>) list);
		msg.setData(b);
		if (mlHandler != null){
			mlHandler.sendMessage(msg);
		}
		return list;
	}
}
