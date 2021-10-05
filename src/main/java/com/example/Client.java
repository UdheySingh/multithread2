package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client
{
    private String serverIP = "localhost";
    private int serverPort = 6789;
    private Socket socket;
    private BufferedReader keyboard;
    private DataOutputStream outputStream;
    private BufferedReader inputStream;
    
    public void connect()
    {
        System.out.println("CLIENT partito in esecuzione");
        try
        {
            keyboard = new BufferedReader(new InputStreamReader(System.in));
            socket = new Socket(serverIP, serverPort);
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } 
        catch (UnknownHostException e)
        {
            System.out.println(e.getMessage());
            System.out.println("Host sconosciuto");
            System.exit(1);
        }
        catch (ConnectException e)
        {
            System.out.println(e.getMessage());
            System.out.println("Posti vuoti 0, mi spiace");
            System.exit(1);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    
    public void talk()
    {
        try
        {
            System.out.println("Inserisci il tuo nome");
            outputStream.writeBytes(keyboard.readLine() + "\n");
            System.out.println("Ora puoi chattare (termina scrivendo FINE)");
            
            ClientThreadIn receiverThread = new ClientThreadIn(this);
            ClientThreadOut senderThread = new ClientThreadOut(this);
        } 
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Errore durante la comunicazione con il server");
            System.exit(1);
        }
    }
    
    public void close()
    {
        try
        {
            System.out.println("CLIENT termina elaborazione e chiude connessione");
            socket.close();
            System.exit(0);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    
    class ClientThreadOut extends Thread
    {
        private Client parent;
        
        public ClientThreadOut(Client parent)
        {
            this.setParent(parent);
            start();
        }
        
        public Client getParent() {
            return parent;
        }

        public void setParent(Client parent) {
            this.parent = parent;
        }

        @Override
        public void run() 
        {
            try
            {    
                while(true)
                {
                    String msg = keyboard.readLine();
                    if(msg.equals("FINE"))
                    {
                        outputStream.writeBytes(msg + "\n");
                        break;
                    }

                    System.out.println("ME: " + msg);
                    outputStream.writeBytes(msg + "\n");
                }

                close();
            } 
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
    }
    
    class ClientThreadIn extends Thread
    {
        private Client parent;

        public ClientThreadIn(Client parent)
        {
            this.setParent(parent);
            start();
        }

        public Client getParent() {
            return parent;
        }

        public void setParent(Client parent) {
            this.parent = parent;
        }

        @Override
        public void run() 
        {
            try
            {    
                while(true)
                {
                    String msg = inputStream.readLine();
                    System.out.println(msg);
                }
            } 
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
    }
    
    public static void main(String[] args)
    {
        Client client = new Client();
        client.connect();
        client.talk();
    }
}