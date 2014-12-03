package cn.cvte.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import cn.cvte.activities.SearchDevicesActivity;

public class TCPClient implements Runnable{
	static Socket clientSocket;
	static DataOutputStream outToServer;
	static BufferedReader inFromServer;
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
					SearchDevicesActivity.TCPSERVER_PORT);
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			inFromServer = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
			//outToServer.writeUTF("musicList\n");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void getMusicList(){
		try {
			outToServer.writeUTF("request_music_list\n");
			outToServer.flush();
			String command = inFromServer.readLine();
			System.out.println("command: "+command);
			if ("return_music_list".equals(command)){
				String str = inFromServer.readLine();
				int size = Integer.parseInt(str);
				ObjectInputStream ois = new ObjectInputStream(inFromServer);
				char[] buffer = new char[size];
				inFromServer.read(buffer, 0, size);
				buffer.
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
