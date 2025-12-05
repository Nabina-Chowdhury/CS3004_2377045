import java.net.*;
import java.io.*;

public class ActionServer {
	public static void main(String[] args) throws IOException {

		ServerSocket ActionServerSocket = null;
		String ActionServerName = "ActionServer";
		int ActionServerNumber = 4540;
		
        
		int startingApples = 1000;
		int startingOranges = 1000;

		
		SharedActionState2 ourSharedActionStateObject = new SharedActionState2(startingApples, startingOranges);
		
		
		try {
			ActionServerSocket = new ServerSocket(ActionServerNumber);
		} catch (IOException e) {
			System.err.println("Could not start " + ActionServerName + " on specified port " + ActionServerNumber + ".");
			System.exit(-1);
		}
		System.out.println(ActionServerName + " started on port " + ActionServerNumber);
		
		
        System.out.println("Waiting for 3 clients to connect (Client A, Client B, Supplier)...");
        
       
        
        
        new ActionServerThread(ActionServerSocket.accept(), "ActionServerThread1", ourSharedActionStateObject).start();
        System.out.println("New ActionServer thread started for Client A (ActionServerThread1 - Customer A).");

        
        new ActionServerThread(ActionServerSocket.accept(), "ActionServerThread2", ourSharedActionStateObject).start();
        System.out.println("New ActionServer thread started for Client B (ActionServerThread2 - Customer B).");

        
        new ActionServerThread(ActionServerSocket.accept(), "ActionServerThread3", ourSharedActionStateObject).start();
        System.out.println("New ActionServer thread started for Supplier (ActionServerThread3 - Supplier).");
        
       
        ActionServerSocket.close();
	}
}