package cn.cvte.network;

import java.net.Socket;

public class ProcessThread implements Runnable{
	Socket socket;
	public ProcessThread(Socket pSocket){
		socket = pSocket;
	}
	@Override
	public void run() {
		
	}

}
