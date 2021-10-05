package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

class ClientHandler extends Thread
{
    private Socket client;
    private DataOutputStream outputStream;
    private BufferedReader inputStream;
    private String clientName;
    
    private ArrayList<ClientHandler> allClients;

    public ClientHandler(Socket client, ArrayList<ClientHandler> allClients)
    {
        try 
        {
            this.client = client;
            this.allClients = allClients;
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
                    for (ClientHandler c : allClients)
                    {
                        if(c == this)
                            continue;

                        c.outputStream.writeBytes("SERVER: " + clientName + " si è disconnesso\n");
                    }
                    break;
                }

                if(allClients.size() > 1)
                {
                    for (ClientHandler c : allClients)
                    {
                        if(c == this)
                            continue;

                        c.outputStream.writeBytes(clientName + ": " + msg + "\n");
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
            System.out.println("Elaborazione terminata");
            allClients.remove(this);
            client.close();
        } 
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
