package cn.cvte.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import cn.cvte.activities.MPApplication;
import cn.cvte.activities.SearchDevicesActivity;
import cn.cvte.music.MusicInfo;

public class TCPClient{
	/**
	 * static field
	 */
	public static Socket clientSocket;
	static DataOutputStream outToServer;
	static BufferedReader inFromServer;
	static ObjectInputStream oi;
	//static ObjectOutputStream oo;
	
	public TCPClient(String address){
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
			clientSocket = new Socket(InetAddress.getByName(address),
					cn.cvte.activities.MPApplication.TCPSERVER_PORT);
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			inFromServer = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public List<MusicInfo> getMusicList(){
		List<MusicInfo> list = new ArrayList<MusicInfo>();
		try {
			outToServer.writeBytes("request_music_list\n");
			outToServer.flush();
			String command = inFromServer.readLine();
			System.out.println("command: "+command);
			if ("return_music_list".equals(command)){
				String str = inFromServer.readLine();
				int size = Integer.parseInt(str);
				ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
				for (int i = 0; i < size; ++i){
					MusicInfo mi;
					try {
						mi = (MusicInfo) ois.readObject();
						list.add(mi);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

}
