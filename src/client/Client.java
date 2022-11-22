package client;

import client.controller.Controller;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable{
	private Controller controller;
	private int x;
	private int y;
	public Client() {
		super();
	}
	public Client(Controller controller) {
		super();
		this.controller = controller;
	}
	synchronized public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
		notifyAll();
	}
	@Override
	synchronized public void run() {
		try {
			Socket socket = new Socket("127.0.0.1", 10001);
			InputStream in = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			OutputStream out = socket.getOutputStream();
			PrintWriter pw = new PrintWriter(out);
			String message;
			label:
			while(true) {
				System.out.println("111");
				message = br.readLine();
				System.out.println("222");
				System.out.println(message);
				switch (message) {
					case "go":
						controller.setPermission();
						System.out.println("wait4CTRL");
						wait();
						System.out.println(x);
						System.out.println(y);
						pw.print(x + "," + y + "\r\n");
						pw.flush();
						break;
					case "read":
						message = br.readLine();
						int x = (int) message.charAt(0) - '0';
						int y = (int) message.charAt(2) - '0';
						Platform.runLater(() -> controller.refreshBoard(x, y));
						break;
					case "win":
					case "lose":
					case "tie":
						break label;
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Server is closed.");
			e.printStackTrace();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
