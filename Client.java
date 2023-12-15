package cs310;
import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String username;
	Random rand = new Random();
	
	public Client(Socket socket, String username)
	{
		try
		{
			this.socket = socket;
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.username = username;
		}
		catch(IOException e)
		{
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}
	
	public void sendMessage()
	{
		try
		{
			bufferedWriter.write(username);
			bufferedWriter.newLine();
			bufferedWriter.flush();
			
			Scanner scan = new Scanner(System.in);
			while(socket.isConnected())
			{
				String messageToSend = scan.nextLine();
				bufferedWriter.write(username + ": "+ messageToSend);
				bufferedWriter.newLine();
				bufferedWriter.flush();
				
				/*boolean validEntries = false;
				String messageToSend = scan.nextLine();
				while(!validEntries)
				switch(messageToSend)
				{
				case "/roll":
				//nString message = scan.nextLine();
				//String messageToSend = scan.nextLine();
				int dice = rand.nextInt(20)+1;
				messageToSend = Integer.toString(dice);
				bufferedWriter.write(username + ": "+ messageToSend);
				bufferedWriter.newLine();
				bufferedWriter.flush();
				break;
				
				default:
				bufferedWriter.write(username + ": "+ messageToSend);
				bufferedWriter.newLine();
				bufferedWriter.flush();
				break;

				/*if(message != "/roll")
				{
				message = messageToSend;
				bufferedWriter.write(username + ": "+ messageToSend);
				bufferedWriter.newLine();
				bufferedWriter.flush();
				}
				else
				{
					dice = rand.nextInt(20)+1;
					messageToSend = Integer.toString(dice);
					bufferedWriter.write(username + ": "+ messageToSend);
					bufferedWriter.newLine();
					bufferedWriter.flush();
				}
		*/
			}
		}
		catch(IOException e)
		{
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}
	public void roll() 
	{
		int dice = rand.nextInt(20)+1;
		Integer.toString(dice);
	}

	public void listenForMessage()
	{
		new Thread()
		{
			public void run()
			{
				String msgFromGroupChat;
				
				while(socket.isConnected())
				{
					try
					{
					msgFromGroupChat = bufferedReader.readLine();
					System.out.println(msgFromGroupChat);
					}
					catch(IOException e)
					{
						closeEverything(socket, bufferedReader, bufferedWriter);
					}
				}
			}
		}.start();
	}

	public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter)
	{
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
	
	public static void main(String[] args) throws IOException
	{
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter your username for the chat below: ");
		String username = scan.nextLine();
		Socket socket = new Socket("localhost", 1234);
		Client client = new Client(socket, username);
		client.listenForMessage();
		client.sendMessage();
		
	}

}
