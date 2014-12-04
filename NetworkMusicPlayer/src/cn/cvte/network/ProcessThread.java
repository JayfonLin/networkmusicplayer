package cn.cvte.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

import cn.cvte.activities.MPApplication;
import cn.cvte.music.MusicFile;
import cn.cvte.music.MusicInfo;

public class ProcessThread implements Runnable{
	
	Socket socket;
	BufferedReader inFromClient;
	DataOutputStream outToClient;
	String clientSentence;
	
	public ProcessThread(Socket pSocket){
		socket = pSocket;
		try {
			inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outToClient = new DataOutputStream(socket.getOutputStream());
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
						outToClient.writeBytes("return_music_list\n");
						outToClient.writeBytes(MusicFile.musicInfoList.size()+"\n");
						ObjectOutputStream oos = new ObjectOutputStream(outToClient);
						for (MusicInfo mi: MusicFile.musicInfoList)
							oos.writeObject(mi);
						oos.flush();
						//outToClient.flush();
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

}
