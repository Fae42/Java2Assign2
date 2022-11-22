package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{
	public static final int port = 10001;
	public Server() {
	
	}
	@Override
	public void run() {
		ServerSocket server;
		Socket socket;
		Socket socketWaiting = null;
		PrintWriter pw ;
		int gameCount = 0;
		try {
			server = new ServerSocket(port);
			while (true) {
				socket = server.accept();
				System.out.println(socket.getLocalAddress());
				System.out.println(socket.getPort());
				if(socketWaiting == null) {
					socketWaiting = socket;
					pw = new PrintWriter(socketWaiting.getOutputStream());
					pw.print("wait"+ "\r\n");
					pw.flush();
				} else {
					new Thread(new Game(socket, socketWaiting, ++gameCount)).start();
					socketWaiting = null;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
