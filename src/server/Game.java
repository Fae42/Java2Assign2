package server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class Game implements Runnable{
	private Socket player1;
	private Socket player0;
	private static final int PLAY_0 = 1;
	private static final int PLAY_1 = -1;
	private static final int EMPTY = 0;
	private int gameId;
	private boolean TURN = true;
	private int[][] chessBoard = new int[3][3];
	public Game(){
		super();
	}
	public Game(Socket player0, Socket player1, int gameId) {
		super();
		this.player0 = player0;
		this.player1 = player1;
		this.gameId = gameId;
	}
	
	private int checkState() {
		boolean gameEnd = true;
		int[] row = new int[3];
		int[] col = new int[3];
		int pie = chessBoard[0][0] + chessBoard[1][1] + chessBoard[2][2];
		int na = chessBoard[0][2] + chessBoard[1][1] + chessBoard[2][0];
		for (int i=0 ; i<3 ; i++) {
			for (int j=0 ; j<3 ; j++) {
				row[i] += chessBoard[i][j];
				col[j] += chessBoard[i][j];
				if(chessBoard[i][j] == EMPTY) {
					gameEnd = false;
				}
			}
		}
		for (int rowI : row) {
			if(rowI==3) {
				return PLAY_0;
			} else if (rowI==-3) {
				return 2;
			}
		}
		for (int colI : col) {
			if(colI==3) {
				return PLAY_0;
			} else if (colI==-3) {
				return 2;
			}
		}
		if (pie==3||na==3) {
			return PLAY_0;
		}
		if(pie==-3||na==-3) {
			return 2;
		}
		if(gameEnd) {
			return 0;
		}
		return -1;
		/*
		0：平局 1：Player0赢 2：Player1赢 -1：没下完
		 */
	}
	
	@Override
	public void run() {
		try {
			System.out.println("game found");
			InputStream in0 = player0.getInputStream();
			BufferedReader br0 = new BufferedReader(new InputStreamReader(in0));
			OutputStream out0 = player0.getOutputStream();
			PrintWriter pw0 = new PrintWriter(out0);
			
			InputStream in1 = player1.getInputStream();
			BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));
			OutputStream out1 = player1.getOutputStream();
			PrintWriter pw1 = new PrintWriter(out1);
			
			pw0.print("match found, id="+ gameId + "\r\n" + "go" + "\r\n");
			pw0.flush();
			pw1.print("match found, id="+ gameId + "\r\n");
			pw1.flush();
			System.out.println("game start");
			String message;
			int x,y;
			while(true) {
				if (TURN) {
					message = br0.readLine();
					System.out.println(gameId + ":" +  message);
					x = (int)message.charAt(0) - '0';
					y = (int)message.charAt(2) - '0';
					chessBoard[x][y] = PLAY_0;
					pw1.print("read\n" + message + "\n");
					pw1.flush();
				} else {
					message = br1.readLine();
					System.out.println(gameId + ":" +  message);
					x = (int)message.charAt(0) - '0';
					y = (int)message.charAt(2) - '0';
					chessBoard[x][y] = PLAY_1;
					pw0.print("read\n" + message + "\n");
					pw0.flush();
				}
				if(checkState()!=-1) {
					int state = checkState();
					if(state==0) {
						pw0.print("tie"+ "\r\n");
						pw1.print("tie"+ "\r\n");
					} else if (state==1) {
						pw0.print("win"+ "\r\n");
						pw1.print("lose"+ "\r\n");
					} else {
						pw0.print("lose"+ "\r\n");
						pw1.print("win"+ "\r\n");
					}
					pw0.flush();
					pw1.flush();
					break;
				} else {
					if(TURN) {
						pw1.println("go");
						pw1.flush();
					} else {
						pw0.println("go");
						pw0.flush();
					}
				}
				TURN = !TURN;
			}
		} catch (SocketException e) {
			try{
				player0.sendUrgentData(0xFF);
			} catch (Exception ex){
				try {
					System.out.println("game "+gameId+" player0 exit");
					OutputStream out = player1.getOutputStream();
					PrintWriter pw = new PrintWriter(out);
					pw.print("another client disconnected\n");
					pw.flush();
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
			try{
				player1.sendUrgentData(0xFF);
			} catch (Exception ex){
				try {
					System.out.println("game "+gameId+" player1 exit");
					OutputStream out = player0.getOutputStream();
					PrintWriter pw = new PrintWriter(out);
					pw.print("another client disconnected\n");
					pw.flush();
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				player1.close();
				player0.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
