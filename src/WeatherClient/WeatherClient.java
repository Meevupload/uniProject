
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class WeatherClient {

    public static void main(String args[]) throws IOException{
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                WeatherClient clientObject = new WeatherClient();
                try{
                    clientObject.start();
                } catch(Exception e) { 
                   System.out.println("Error starting weather client.");
                }
            }
        });
    }

    public void start() throws Exception {
        /* establish a weather client
        then continuosly send data to server's "weatherData" queue */
        Socket clientSocket = new Socket("localhost", 9090); 
        
        PrintWriter pw = new PrintWriter(clientSocket.getOutputStream()); 

        try { 
            establishType(clientSocket, pw);
        } catch (Exception e) { 
            System.out.println("Error establishing type of client.");
        }

        Random rand = new Random();
        int upperBound = 1000;
        int randomNumber;

        while (true) {
            try {
                Thread.sleep(2000);
            } 
            catch(InterruptedException e) {
                 System.out.println("Sleep error occurred.");
            }

            randomNumber = rand.nextInt(upperBound);

            Integer myNumber = new Integer(randomNumber);
            pw.println("Humidity Reading - " + String.valueOf(myNumber));  
            pw.flush(); 
            
            InputStreamReader in = new InputStreamReader(clientSocket.getInputStream()); 
            BufferedReader bf = new BufferedReader(in); 
            
            String str = bf.readLine(); 
            System.out.println("Server : " + str); 
        }
    }

    public void establishType(Socket _connection, PrintWriter _pw) throws Exception { 
        /* send the client type to server */

        _pw.println("weatherClient");
        _pw.flush();

        InputStreamReader in = new InputStreamReader(_connection.getInputStream());
        BufferedReader bf = new BufferedReader(in); 
                
        String str = bf.readLine();
        System.out.println("Server : " + str); 

    }
    
}
