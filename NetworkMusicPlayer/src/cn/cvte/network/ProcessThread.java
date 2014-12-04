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


import cn.cvte.activities.MPApplication;
import cn.cvte.music.MusicFile;
import cn.cvte.music.MusicInfo;

public class ProcessThread implements Runnable{
	
	Socket socket;
	BufferedReader inFromClient;
	PrintWriter outToClient;
	String clientSentence;
	
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
						System.out.println("return musicList");
						Charset c = Charset.forName("UTF-8");
						outToClient.println("return_music_list");
						String str = constructMusicList().toString();
						outToClient.println(str);
						outToClient.flush();
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
			System.out.println("jsonobject:"+jo.toString());
			list.add(jo);
			
		}
		JSONArray ja = new JSONArray(list);
		System.out.println("jsonarray:"+ja.toString());
		return ja;
	}

}
