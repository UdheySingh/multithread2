package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server
{
    private ServerSocket server;
    private ArrayList<ServerThread> serverThreads;
    private int maxClients;
    
    public void listen()
    {
        try
        {
            System.out.println("SERVER partito in esecuzione");
            server = new ServerSocket(6789);
            
            serverThreads = new ArrayList<ServerThread>();
            maxClients = 2;
            
            for (int i = 0; i < maxClients; i++)
                serverThreads.add(new ServerThread(server.accept()));
            
            server.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    
    class ServerThread extends Thread
    {
        private Socket client;
        private DataOutputStream outputStream;
        private BufferedReader inputStream;
        private String clientName;
        
        public ServerThread(Socket s)
        {
            try 
            {
                client = s;
                outputStream = new DataOutputStream(client.getOutputStream());
                inputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
                
                start();
            } 
            catch (Exception e) 
            {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
       
        @Override
        public void run()
        {
            try
            {
                System.out.println("Connessione con il server stabilita");

                clientName = inputStream.readLine();

                while (true)
                {                        
                    String msg = inputStream.readLine();
                    if(msg.equals("FINE"))
                    {
                        for (ServerThread s : serverThreads)
                        {
                            if(s == this)
                                continue;

                            s.outputStream.writeBytes("SERVER: " + clientName + " si è disconnesso\n");
                        }
                        break;
                    }

                    if(serverThreads.size() > 1)
                    {
                        for (ServerThread s : serverThreads)
                        {
                            if(s == this)
                                continue;

                            s.outputStream.writeBytes(clientName + ": " + msg + "\n");
                        }
                    }
                    else
                    {
                        outputStream.writeBytes("SERVER: errore, nessuno è in linea\n");
                    }
                }
                close();
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
        
        public void close()
        {
            try
            {
                System.out.println("SERVER thread termina elaborazione");
                serverThreads.remove(this);
                client.close();
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
        Server server = new Server();
        server.listen();
    }
}