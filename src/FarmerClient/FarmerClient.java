import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.awt.GraphicsEnvironment;
import java.net.URISyntaxException;
import java.io.File; 
import java.io.FileNotFoundException; 
import java.util.Scanner; 

//vector<String> message = new Vector<String>()
public class FarmerClient{

    public static void main(String args[]) throws IOException{

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoginGUI login = new LoginGUI();
                MainWindowGUI mainWindow = new MainWindowGUI();
                FarmerClient clientObject = new FarmerClient();
                clientObject.login();
                try{
                    clientObject.start(mainWindow);
                } catch(Exception e) { 
                    System.out.println("Error starting farmer client.");
                    mainWindow.addMessage("Error starting farmer client.");
                }
            } 
      });
    }

    public void login() { 
        boolean loggedIN = false;

        String correctName = "";
        String correctPassword = "";

        try{
            File credFile = new File("creds.txt");
            Scanner credReader = new Scanner(credFile);
            correctName = credReader.nextLine();
            correctPassword = credReader.nextLine();
            credReader.close();
        } catch (FileNotFoundException e) { 
            System.out.println("error opening file.");
        }
        /*
        String correctName = "user";
        String correctPassword = "password";*/

        while (!loggedIN) { 
            System.out.println("\n\nEnter User name : ");
            String name = System.console().readLine();
            System.out.println("\n\nEnter Password : ");
            String password = System.console().readLine();

            if (name.equals(correctName)){
                    if (password.equals(correctPassword)){
                        loggedIN = true;
                    }
                    else{
                        System.out.println("\n wrong password");
                    }
                }
                else{
                   System.out.println("\n wrong username or password");
                }

        }
    }

    public void start(MainWindowGUI mainWindow) throws Exception{
        /* establish a farmer client
        then continuosly read the "weatherData" queue from server */

        Socket clientSocket = new Socket("localhost", 9090); 
        
        PrintWriter pw = new PrintWriter(clientSocket.getOutputStream()); 

        try { 
            establishType(clientSocket, pw, mainWindow);
        } catch (Exception e) { 
            System.out.println("Error establishing type of client.");
            mainWindow.addMessage("Error establishing type of client.");
        }

        while (true) {

            InputStreamReader in = new InputStreamReader(clientSocket.getInputStream()); 
            BufferedReader bf = new BufferedReader(in);

            
            String str = bf.readLine();
            if (!str.equals("none")) {
                System.out.println(str);
                mainWindow.addMessage(str);
                pw.println("FarmerClient recieved data");
           } else {
                pw.println("none");
           }
           pw.flush();
       }
   }

   public void establishType(Socket _connection, PrintWriter _pw, MainWindowGUI mainWindow) throws Exception { 
    /* send the client type to server */

    _pw.println("farmerClient");
    _pw.flush();

    InputStreamReader in = new InputStreamReader(_connection.getInputStream());
    BufferedReader bf = new BufferedReader(in); 

    String str = bf.readLine();
    mainWindow.addMessage("Server : " + str);
    System.out.println("Server : " + str);

}
}


class GUI { 

    JFrame frame;

    public GUI() { 

    }

    public void updateFrame() { 
        SwingUtilities.updateComponentTreeUI(frame);
    }

}

class LoginGUI extends JPanel{ 

    JFrame frame;
    boolean loggedIN = false;
    String correctUsername = "user"; 
    String correctPassword = "password";

    public LoginGUI() { 

        buildFrame();/*
        while (!loggedIN) {
            updateFrame();
        }
        frame.setVisible(false);*/
        updateFrame();
    }

    public void buildFrame() { 

        frame = new JFrame("Login Window");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150);

        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();

        JLabel username = new JLabel("Username:");
        JTextField tf1 = new JTextField(10);

        JLabel password = new JLabel("Password:");
        JTextField tf2 = new JTextField(10);

        JButton register = new JButton("Register");
        register.addActionListener(new ActionListener(){  
        public void actionPerformed(ActionEvent e){  
            System.out.println(tf1.getText()); 
            System.out.println(tf2.getText()); 
            }  
        }); 
        JButton enter = new JButton("Enter");
        enter.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){  
                String inputUsername = String.valueOf(tf1.getText());
                String inputPassword = String.valueOf(tf2.getText());

                if (inputUsername.equals(correctUsername)){
                    if (inputPassword.equals(correctPassword)){
                        loggedIN = true;
                    }
                    else{
                        System.out.println("\n wrong password");
                    }
                }
                else{
                   System.out.println("\n wrong username");
                   System.out.println(inputUsername);
                   System.out.println(correctUsername);
                }

            }  
        });

        panel1.add(username);
        panel1.add(tf1);
        panel2.add(password);
        panel2.add(tf2);
        panel3.add(register);
        panel3.add(enter);

        frame.getContentPane().add(BorderLayout.NORTH, panel1);
        frame.getContentPane().add(BorderLayout.CENTER, panel2);
        frame.getContentPane().add(BorderLayout.SOUTH, panel3);

        frame.setVisible(true);
    }

    public void updateFrame() { 
        SwingUtilities.updateComponentTreeUI(frame);
        frame.repaint();

    }

}

class MainWindowGUI extends JPanel {

    JFrame frame;
    Vector<String> messageList = null;
    int MAXMESSAGES = 25;
    JTextArea messages = new JTextArea();

    public MainWindowGUI(){

        int MAXMESSAGES = 25;   
        messageList = new Vector<String>(MAXMESSAGES);
        buildFrame();

    }       

    public void addMessage(String message) { 

        if (messageList.size() > MAXMESSAGES) { 
            messageList.remove(0);
        }
        messageList.add(message);

        this.messages = new JTextArea();
        for (String m : messageList) {
            this.messages.append(m);
        }

        updateFrame();
    }

    public void buildFrame() { 

        //frame.getContentPane().removeAll();
        //frame.dispose();

        frame = new JFrame("Main Window");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        JMenuBar mb = new JMenuBar(); 
        JMenu m1 = new JMenu("Weather Stations"); 
        mb.add(m1); 
        JMenuItem m11 = new JMenuItem("Station 1"); 
        JMenuItem m22 = new JMenuItem("Station 2");
        JMenuItem m33 = new JMenuItem("Station 3");
        JMenuItem m44 = new JMenuItem("Station 4");
        JMenuItem m55 = new JMenuItem("Station 5");
        m1.add(m11); 
        m1.add(m22);
        m1.add(m33);
        m1.add(m44);
        m1.add(m55);
        m11.addActionListener(new ActionListener(){ 
         public void actionPerformed(ActionEvent e){  
            System.out.println("WEATHER STATION 1"); 
        }  
    });
        m22.addActionListener(new ActionListener(){  
         public void actionPerformed(ActionEvent e){  
            System.out.println("WEATHER STATION 2"); 
        }  
    });
        m33.addActionListener(new ActionListener(){  
         public void actionPerformed(ActionEvent e){  
            System.out.println("WEATHER STATION 3"); 
        }  
    });
        m44.addActionListener(new ActionListener(){  
         public void actionPerformed(ActionEvent e){  
            System.out.println("WEATHER STATION 4"); 
        }  
    });
        m55.addActionListener(new ActionListener(){  
         public void actionPerformed(ActionEvent e){  
            System.out.println("WEATHER STATION 5"); 
        }  
    });

        JPanel panel = new JPanel(); 
        //JButton download = new JButton("Download");
        //panel.add(download); 

        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.getContentPane().add(BorderLayout.CENTER, this.messages);

        frame.setVisible(true);

    }

    public void updateFrame() { 
        //frame.remove(((BorderLayout)getLayout()).getLayoutComponent(BorderLayout.CENTER));
        //frame.getContentPane().removeAll();
        //frame.getContentPane().add(BorderLayout.CENTER, this.messages);
        //SwingUtilities.updateComponentTreeUI(frame);
        //frame.repaint();
    }
}
