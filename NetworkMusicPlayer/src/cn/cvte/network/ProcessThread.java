package cn.cvte.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ProcessThread implements Runnable{
	Socket socket;
	public boolean online;
	BufferedReader inFromClient;
	DataOutputStream outToClient;
	String clientSentence;
	public ProcessThread(Socket pSocket){
		socket = pSocket;
		online = true;
		try {
			inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outToClient = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void run() {
		while (online){
			try {
				clientSentence = inFromClient.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (clientSentence != null){
				if ("musicList".equals(clientSentence)){
					System.out.println("return musicList");
				}
			}
		}
	}

}
