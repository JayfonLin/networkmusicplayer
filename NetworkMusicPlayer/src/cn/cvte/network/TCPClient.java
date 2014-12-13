package cn.cvte.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.WriteAbortedException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;

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
	static TCPReaderThread readThread;
	private static TCPClient instance;
	
	public static TCPClient getInstance(){
		return instance;
	}
	public static TCPClient getInstance(String address){
		if (instance == null){
			synchronized (TCPClient.class) {
				if (instance == null)
					instance = new TCPClient(address);
			}
		}
		return instance;
	}
	
	private TCPClient(String address){
		try {
			close();
			clientSocket = new Socket(InetAddress.getByName(address),
					cn.cvte.activities.MPApplication.TCPSERVER_PORT);
			outToServer = new PrintWriter(clientSocket.getOutputStream());
			inFromServer = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
			if (readThread != null){
				readThread.interrupt();
				readThread = null;
			}
			readThread = new TCPReaderThread(inFromServer, outToServer);
			readThread.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void write(final String str){
		if (outToServer != null){
			synchronized (outToServer) {
				Thread t = new Thread(new Runnable() {
					
					@Override
					public void run() {
						outToServer.print(str);
						outToServer.flush();
					}
				});
				t.start();
			}
		}
	}
	
	public void transportFile(String filePath){
		write("transport_file\n");
		File file = new File(filePath);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			int bufferSize = 1024;
			char[] bufArray = new char[bufferSize];
			write(file.getName()+"\n");
			write(file.length()+"\n");
			
			while (true){
				int read = 0;
				if (reader != null){
					read = reader.read(bufArray);
				}
				if (read == -1) break;
				synchronized (outToServer) {
					outToServer.write(bufArray,0,read);
				}
			}
			outToServer.flush();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try{
				if (reader != null)
					reader.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	public void setHandler(Handler pHandler){
		if (readThread != null)
			readThread.setHandler(pHandler);
	}
	public void removeHandler(Handler pHandler){
		if (readThread != null)
			readThread.removeHandler(pHandler);
	}
	
	public void getMusicList(){
		write("request_music_list\n");
	}
	public void getState(){
		write("request_state\n");
	}
	
	public void selectMusic(String path){
		if (outToServer != null){
			StringBuilder outStr = new StringBuilder("select_music\n");
			outStr.append(path);
			outStr.append("\n");
			write(outStr.toString());
		}
	}
	
	public void simpleControl(String cm){
		if (outToServer != null){
			write(cm+"\n");
		}
	}
	
	
	public void close(){
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
