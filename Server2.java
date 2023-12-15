package cs310;
import java.io.*;
import java.net.*;
import java.util.*;
public class Server2 
{
	private ServerSocket serverSocket;
	
	public Server2(ServerSocket serverSocket)
	{
		this.serverSocket = serverSocket;
	}
	
	public void startServer()
	{
		try {
			while(!serverSocket.isClosed())
			{
				Socket socket= serverSocket.accept();
				System.out.println("a new user has connected.");
				ClientHandler clientHandler = new ClientHandler(socket);
				
				Thread thread = new Thread(clientHandler);
				thread.start();
			}
			
		}
		catch (IOException e) {
			
		}
	}
	
	public void closeServerSocket()
	{
		try {
			if(serverSocket != null)
			{
				serverSocket.close();
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		ServerSocket serverSocket = new ServerSocket(1234);
		Server2 server = new Server2(serverSocket);
		server.startServer();
	}
	
	public class ClientHandler implements Runnable
	{
		public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
		private Socket socket;
		private BufferedReader bufferedReader;
		private BufferedWriter bufferedWriter;
		private String clientUsername;
		private int dice;
		Random rand = new Random();
		
		public ClientHandler(Socket socket)
		{
			try {
				this.socket = socket;
				this.bufferedWriter= new BufferedWriter (new OutputStreamWriter(socket.getOutputStream()));
				this.bufferedReader = new BufferedReader (new InputStreamReader(socket.getInputStream()));
				this.clientUsername = bufferedReader.readLine();
				clientHandlers.add(this);
				broadcastMessage("SERVER: "+ clientUsername+ " has entered the chat.");
				
			}
			catch(IOException e)
			{
				closeEverything(socket, bufferedReader, bufferedWriter);
			}
		}
		
		public void run()
		{
			String messageFromClient;
			
			while(socket.isConnected())
			{
				try
				{
					messageFromClient = bufferedReader.readLine();
					broadcastMessage(messageFromClient);
				}
				catch(IOException e)
				{
					closeEverything(socket, bufferedReader, bufferedWriter);
					break;
				}
			}
		}
		
		public void broadcastMessage(String messageToSend)
		{
			for(ClientHandler clientHandler: clientHandlers)
			{
				try
				{
					if(!clientHandler.clientUsername.equals(clientUsername))
					{
						
						clientHandler.bufferedWriter.write(messageToSend);
						clientHandler.bufferedWriter.newLine();
						clientHandler.bufferedWriter.flush();
						/*if(messageToSend == "/roll")
						{
							roll();
						}*/
					}
				
				}
				catch(IOException e)
				{
					closeEverything(socket, bufferedReader, bufferedWriter);
				}
			}
		}
		
		/*public void roll() 
		{
			int dice = rand.nextInt(20)+1;
			Integer.toString(dice);
			broadcastMessage(clientUsername + ": rolled "+ Integer.toString(dice));
		}*/
		public void removeClientHandler()
		{
			clientHandlers.remove(this);
			broadcastMessage("SERVER: "+ clientUsername +" has left the chat.");
			
		}
		
		public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter)
		{
			removeClientHandler();
			try
			{
				if(bufferedReader != null)
				{
					bufferedReader.close();
				}
				if(bufferedWriter != null)
				{
					bufferedWriter.close();
				}
				if(socket != null)
				{
					socket.close();
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
}
