import java.net.*;
import java.io.*;

public class SharedActionState2 {
	
    private int apples;
    private int oranges;
    private boolean accessing = false;
    private int threadsWaiting = 0;
    
    SharedActionState2(int apples, int oranges) {
        this.apples = apples;
        this.oranges = oranges;
    }

    public synchronized void acquireLock() throws InterruptedException{
        Thread me = Thread.currentThread();
        System.out.println(me.getName()+" is attempting to acquire a lock!");
        ++threadsWaiting;
        while (accessing) {
            System.out.println(me.getName()+" waiting to get a lock as someone else is accessing...");
            wait();
        }
        --threadsWaiting;
        accessing = true;
        System.out.println(me.getName()+" got a lock!");
    }

    public synchronized void releaseLock() {
        accessing = false;
        notifyAll();
        Thread me = Thread.currentThread();
        System.out.println(me.getName()+" released a lock!");
    }
    
    
    public synchronized String processInput(String myThreadName, String theInput) {
        System.out.println(myThreadName + " received " + theInput);
        String theOutput = null;
        
        String inputCommand = theInput.trim().toLowerCase();
        try {
            // --- 1. Client Actions (Buy/Check) - For Thread 1 and Thread 2 ---
            if (myThreadName.equals("ActionServerThread1") || myThreadName.equals("ActionServerThread2")) {
                
                if (inputCommand.equals("check_stock")) {
                    theOutput = myThreadName + " checked stock: Apples=" + apples + ", Oranges=" + oranges;
                } else if (inputCommand.startsWith("buy_apples")) {
                    theOutput = handlePurchase("apples", theInput.trim(), myThreadName);
                } else if (inputCommand.startsWith("buy_oranges")) {
                    // Pass the full input string for simplified number parsing
                    theOutput = handlePurchase("oranges", theInput.trim(), myThreadName);

                } else {
                    theOutput = myThreadName + " (Client) received unrecognised command. Try Check_stock, Buy_apples (N), or Buy_oranges (N).";
                }

            // --- 2. Supplier Actions (Add/Check) - For Thread 3 ---
            } else if (myThreadName.equals("ActionServerThread3")) {
                if (inputCommand.equals("check_stock")) {
                    theOutput = myThreadName + " checked stock: Apples=" + apples + ", Oranges=" + oranges;
                } else if (inputCommand.startsWith("add_apples")) {
                    theOutput = handleSupply("apples", theInput.trim(), myThreadName);
                } else if (inputCommand.startsWith("add_oranges")) {
                    theOutput = handleSupply("oranges", theInput.trim(), myThreadName); 
                } else {
                    theOutput = myThreadName + " (Supplier) received unrecognised command. Try Check_stock, Add_apples (N), or Add_oranges (N).";
                }

            } else { 
               
                theOutput = "Error: Thread " + myThreadName + " is not recognized to perform any action.";
            }

        } catch (NumberFormatException e) {
            theOutput = myThreadName + " received invalid number format in the command.";
        } catch (Exception e) {
            theOutput = myThreadName + " encountered an unexpected error: " + e.getMessage();
        }
    
        System.out.println(theOutput);
        return theOutput;
    }
    
   
    private String handlePurchase(String item, String theInput, String threadName) throws NumberFormatException {
        String quantityStr = theInput.replaceAll("[^0-9]", "");
        if (quantityStr.isEmpty()) {
            return threadName + " received malformed " + item + " buy command. Expected: Buy_" + item + " (N) or Buy_" + item + " N. No quantity found.";
        }
        int numToBuy = Integer.parseInt(quantityStr);
        int currentStock = item.equals("apples") ? this.apples : this.oranges;
        
        if (currentStock >= numToBuy && numToBuy > 0) {
            if (item.equals("apples")) this.apples -= numToBuy;
            else this.oranges -= numToBuy;
            
            return threadName + " bought " + numToBuy + " " + item + ". Remaining Apples=" + this.apples + ", Oranges=" + this.oranges;
        } else if (numToBuy <= 0) {
            return threadName + " FAILED to buy: Purchase quantity must be greater than zero.";
        } else {
            return threadName + " FAILED to buy " + numToBuy + " " + item + ": Not enough stock (only " + currentStock + " left).";
        }
    }

   
    
    private String handleSupply(String item, String theInput, String threadName) throws NumberFormatException {
        String quantityStr = theInput.replaceAll("[^0-9]", "");
        
        if (quantityStr.isEmpty()) {
            return threadName + " received malformed " + item + " add command. Expected: Add_" + item + " (N) or Add_" + item + " N. No quantity found.";
        }
        int numToAdd = Integer.parseInt(quantityStr);
        
        if (numToAdd > 0) {
            if (item.equals("apples")) this.apples += numToAdd;
            else this.oranges += numToAdd;
            
            return threadName + " added " + numToAdd + " " + item + ". New Stock: Apples=" + this.apples + ", Oranges=" + this.oranges;
        } else {
            return threadName + " FAILED to add: Supply quantity must be greater than zero.";
        }
    }
}
