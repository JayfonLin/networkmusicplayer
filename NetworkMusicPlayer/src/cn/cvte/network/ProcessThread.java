package cn.cvte.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

import cn.cvte.music.MusicFile;
import cn.cvte.music.MusicInfo;

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
				if (clientSentence != null){
					if ("request_music_list".equals(clientSentence)){
						System.out.println("return musicList");
						outToClient.writeUTF("return_music_list\n");
						outToClient.writeUTF(MusicFile.musicInfoList.size()+"\n");
						ObjectOutputStream oos = new ObjectOutputStream(outToClient);
						for (Map<String, Object> map: MusicFile.musicInfoList)
							oos.writeObject(new MusicInfo(map));
						outToClient.flush();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
