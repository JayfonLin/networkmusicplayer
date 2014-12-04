package cn.cvte.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.cvte.activities.MPApplication;
import cn.cvte.activities.SearchDevicesActivity;
import cn.cvte.music.MusicInfo;

public class TCPClient{
	/**
	 * static field
	 */
	public static Socket clientSocket;
	static PrintWriter outToServer;
	static BufferedReader inFromServer;
	static ObjectInputStream oi;
	
	public TCPClient(String address){
		try {
			close();
			clientSocket = new Socket(InetAddress.getByName(address),
					cn.cvte.activities.MPApplication.TCPSERVER_PORT);
			outToServer = new PrintWriter(clientSocket.getOutputStream());
			inFromServer = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static List<MusicInfo> getMusicList(){
		List<MusicInfo> list = new ArrayList<MusicInfo>();
		try {
			outToServer.println("request_music_list");
			outToServer.flush();
			String command = inFromServer.readLine();
			System.out.println("command: "+command);
			if ("return_music_list".equals(command)){
				String jsonStr = inFromServer.readLine();
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
						mi.size = jo.getString("size");
						list.add(mi);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static boolean selectMusic(String path){
		if (outToServer != null && inFromServer != null){
			outToServer.println("select_music");
			outToServer.println(path);
			outToServer.flush();
			return judgeState();
		}
		return false;
	}
	
	
	
	public static boolean simpleControl(String cm){
		if (outToServer != null && inFromServer != null){
			outToServer.println(cm);
			outToServer.flush();
			return judgeState();
		}
		return false;
	}
	
	private static boolean judgeState(){
		String command;
		try {
			command = inFromServer.readLine();
			System.out.println("command: "+command);
			if ("success".equals(command)){
				return true;
			}else
				return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void close(){
		try {
			if (outToServer != null){
				outToServer.close();
				outToServer = null;
			}
			if (inFromServer != null){
				inFromServer.close();
				inFromServer = null;
			}
			if (clientSocket != null && !clientSocket.isClosed()){
				clientSocket.close();
				clientSocket = null;
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
