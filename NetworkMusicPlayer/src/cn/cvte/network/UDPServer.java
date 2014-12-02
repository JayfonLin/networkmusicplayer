package cn.cvte.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import cn.cvte.activities.SearchDevicesActivity;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class UDPServer implements Runnable{
	final static int RECEIVE_LENGTH = 1024;
	public final static int SERVER_PORT = 30000;
	public final static String BROADCAST_STR = "255.255.255.255";
	public static int TTLTime = 1;
	
	DatagramSocket socket;
	Context mContext;
	public UDPServer(Context context){
		mContext = context;
		try {
			socket = new DatagramSocket(SERVER_PORT);
			socket.setSoTimeout(0);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException");
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Exception");
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		
		try {
			
			DatagramPacket dp = new DatagramPacket(new byte[RECEIVE_LENGTH], RECEIVE_LENGTH);
			System.out.println("bb");
			socket.receive(dp);
			System.out.println("aa");
			String msg = new String(dp.getData()).trim();
			System.out.println(msg);
			String command = msg.split("#")[0];
			String address = msg.split("#")[1];
			if ("hello".equals(command)){
				DatagramPacket packet;
				String serverAddress;
				serverAddress = BroadcastClient.getLocalIpAddress(mContext).toString();
				System.out.println("ip:"+serverAddress);
				byte[] sendMsg = ("welcome"+"#"+serverAddress).getBytes();
				packet = new DatagramPacket(sendMsg,sendMsg.length,
						InetAddress.getByName(address), BroadcastClient.CLIENT_PORT);
				socket.send(packet);//·¢ËÍ±¨ÎÄ
			}else if ("welcome".equals(command)){
				
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
		
		}
		
	}
	
	void tcpConnect(String address){
		Socket s = new Socket("10.140.86.98", 5858);  
        
        System.out.println( "remote socket " + s.getRemoteSocketAddress());  
          
        InputStream in = s.getInputStream();  
          
        InputStreamReader reader = new InputStreamReader(in);  
          
        char [] cbuf = new char[100];  
        int len = reader.read(cbuf);  
        StringBuilder sb = new StringBuilder(100);  
          
        sb.append(cbuf, 0, len);  
        System.out.println(sb.toString());  
          
        OutputStreamWriter writer = new OutputStreamWriter(s.getOutputStream());  
          
        writer.write("from client");  
          
        writer.close();  
        reader.close();  
        s.close();  
	}
	
}
