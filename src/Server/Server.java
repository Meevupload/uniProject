import javax.swing.*;
import java.awt.*;
//import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
	/* file class */


	public static void main(String args[]) throws IOException {     
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Interface1 iObject = new Interface1();
				iObject.mainWindowGUI(null);
				Server serverObject = new Server();
				serverObject.start();
			}
		});

	}

	public void start() { 
		/* instantiated main */


		ServerSocket serverSocket = null;
		DataHandler dataHandlerObject = new DataHandler();
		ClientCounter cc = new ClientCounter();
		try {
			serverSocket = new ServerSocket(9090);
		} catch (IOException e) {
			System.out.println("Error on accept socket!");
		}
		while (true) {
			while (cc.numClients < cc.MAXCLIENTS) {
				try {
					Socket socket = serverSocket.accept();
					System.out.println("Client Connected");
					//iObject.mainWindowGUI("Client Connected"); 
					cc.addClient(socket, cc, dataHandlerObject);			    
				} catch (IOException e) {
					System.out.println("Error spawning client thread! (client may have closed or restarted)");
				}
			}
		}
	}
}

class ClientCounter { 
	/* class to confirm client add and remove - 
	handles vector management by threads */

	volatile int numClients = 0;
	int MAXCLIENTS = 5;
	volatile Vector<Socket> clientList = null;
	volatile Vector<Thread> threadList = null;
	volatile Vector<Integer> idList = null;
	
	public ClientCounter() { 
		/* constructor */

		numClients = 0;
		MAXCLIENTS = 5;
		clientList = new Vector<Socket>(MAXCLIENTS);
		threadList = new Vector<Thread>(MAXCLIENTS);
		idList = new Vector<Integer>(MAXCLIENTS);

	}

	public synchronized void addClient(Socket con, ClientCounter cc, DataHandler dh) { 
    	/* add a socket, thread and id to vectors.
    	start new thread to handle each new connection */

    	numClients++;
    	clientList.add(con);
    	threadList.add(new Thread(new clientHandler(con, dh, cc)));
    	idList.add(Integer.valueOf(numClients));
    	Thread currentThread = threadList.get(numClients-1);
    	currentThread.start();

    }

    public synchronized void removeClient(int clientID) {
    	/* remove client from vectors based off id
    	to make room for a new connection
    	note: thread automatically terminated when reaches end of run() */ 

    	numClients--;
    	int position = idList.indexOf(Integer.valueOf(clientID));
    	clientList.remove(position);
    	threadList.remove(position);
    	idList.remove(position);

    }

}

class DataHandler { 
	/* Class to handle data traffic between threads */

	protected static int MAXDATA = 25;
	public volatile Queue<String> weatherDataQueue = null;

	public DataHandler() { 
		/* constructor */

		weatherDataQueue = new LinkedList<String>();
	}

	public synchronized void writeWeatherData(String newData) { 
		/* writes new data to the weather data queue
		if the queue is full then it removes head value and
		places a new one at tail */

		try { 
			weatherDataQueue.add(newData);
		} catch (IllegalStateException e) { 
			try { 
				String queueFull = weatherDataQueue.remove();
				weatherDataQueue.add(newData);
			} catch (NoSuchElementException e2) {;}
		}
	}

	public synchronized String readWeatherData() { 
		/* reads top line in weather data
		returns the string "none" if queue is empty. */

		String weatherData = new String("none");

		try { 
			weatherData = weatherDataQueue.remove();
		} catch(NoSuchElementException e) {;}

		return weatherData;
	}
}

class clientHandler implements Runnable { 
	/* main thread executable class */

	int clientNumber = 0;
	Socket connection = null;	
	Integer clientNumberINT = null;
	String clientType = null;
	DataHandler dataHandler = null;
	ClientCounter cc = null;
	boolean connected = true;

	public clientHandler(Socket _connection, DataHandler _dataHandler, ClientCounter _clientCounter) {
		/* constructor */ 

		connection = _connection;
		dataHandler = _dataHandler;
		cc = _clientCounter;
		clientNumber = cc.numClients;
		clientNumberINT = new Integer(clientNumber);

		try {
			clientType = resolveClientType(connection);
		} catch (Exception e) {
			System.out.println("Could not resolve client.");
			connected = false;
		}

	}

	public void run() { 
		/* this is what the thread will be doing */

		while(connected) {
			try { 
				try {
					if (clientType.equals("weatherClient")) {
						getWeatherNumber(connection, clientNumberINT);
					} else if (clientType.equals("farmerClient")) { 
						farmerDisplay(connection);
					}
				} catch(Exception e) {
					connected = false;
				}
				Thread.sleep(50);
			} catch(InterruptedException e) {
				connected = false;
			}
		}
		System.out.println("Connection dropped - closing thread.");
		cc.removeClient(clientNumber);
		// thread stops upon reaching the end of run()
	}


	public String resolveClientType(Socket socket) throws Exception { 
		/* method to get first input from client 
		and determine what type of client it is.
		(hadshake procedure) */

		String type = new String("none");

		InputStreamReader in =   new InputStreamReader(socket.getInputStream());
		BufferedReader bf = new BufferedReader(in); 
		String str = bf.readLine(); 
		System.out.println("Client" + String.valueOf(clientNumber) + " : " + str); 
		if (str.equals("weatherClient")) { 
			type = new String("weatherClient");
		} else if (str.equals("farmerClient")) { 
			type = new String("farmerClient");
		}

		PrintWriter pr = new PrintWriter(socket.getOutputStream()); 
		pr.println("Connection Established. [client type = " + type + "]");
		pr.flush();

		return type;
	}

	public void farmerDisplay(Socket socket) throws Exception { 
		/* if weather data is not empty then 
		send to farmer the latest weather data
		recieves "none" if the farmerclient behaves normally
		- this is for checking if peer client has been shutdown
		so the connection socket can be aborted*/

		
		String weatherData = dataHandler.readWeatherData();
		PrintWriter pr = new PrintWriter(socket.getOutputStream());
		pr.println(weatherData);
		pr.flush();

		InputStreamReader in =   new InputStreamReader(socket.getInputStream()); 
		BufferedReader bf = new BufferedReader(in); 
		String str = bf.readLine();
		if (!str.equals("none")) {
			System.out.println(str);
		}
	}

	public void getWeatherNumber(Socket socket, Integer clientNumber) throws Exception { 
		/* for weather clients:
		waits for incoming data from client and 
		collects it to the data handler 
		sends data to weather client as confirmation */

		InputStreamReader in =   new InputStreamReader(socket.getInputStream()); 
		BufferedReader bf = new BufferedReader(in); 
		String str = bf.readLine(); 
		String weatherData = new String("Weather_Client_" + String.valueOf(clientNumberINT) + ": " + str);
		System.out.println("Recieved data from Weather_Client_" + String.valueOf(clientNumberINT));
		dataHandler.writeWeatherData(weatherData);
		PrintWriter pr = new PrintWriter(socket.getOutputStream()); 
		pr.println("Number recieved."); 
		pr.flush(); 
	}

}

class Interface1 { 

	public Interface1() { 

	}

	JFrame frame;

	public void mainWindowGUI(String displayedMessage){

		if (displayedMessage != null){
			frame.getContentPane().removeAll();
		}
		if (displayedMessage == null){
			frame = new JFrame("Main Window");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(600, 600);
		}


		JMenuBar mb = new JMenuBar(); 
		JMenu m1 = new JMenu("Current Connections"); 
		mb.add(m1); 

		JTextArea ta = new JTextArea();

		frame.getContentPane().add(BorderLayout.NORTH, mb);
		frame.getContentPane().add(BorderLayout.CENTER, ta);

		if (displayedMessage != null){
			ta.append(displayedMessage);
		}

		frame.setVisible(true);

	}



}