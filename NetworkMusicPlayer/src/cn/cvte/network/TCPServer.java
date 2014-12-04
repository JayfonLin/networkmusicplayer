package cn.cvte.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable{
	ServerSocket serverSocket;
	public TCPServer(ServerSocket socket){
		serverSocket = socket;
	}
	@Override
	public void run() {
		
		while(true){
			Socket mSocket;
			try {
				mSocket = serverSocket.accept();
				System.out.println("accept");
				/*BufferedReader inFromClient = new BufferedReader(
						new InputStreamReader(mSocket.getInputStream()));
				System.out.println(inFromClient.readLine());*/
				ProcessThread pt = new ProcessThread(mSocket);
	            Thread thread2 = new Thread(pt);   
	            thread2.start();
				//threadPool.execute(thread);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
