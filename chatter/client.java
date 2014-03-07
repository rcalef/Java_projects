// $Id: client.java,v 1.1 2013-08-08 14:10:22-07 - - $
//CMPS 109 Summer 2013 Assignment 5
//Starter code provided by: Wesley Mackey (mackey@soe.ucsc.edu)
//Edited and added to by: Robert Calef (rcalef@ucsc.edu)
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.*;

import static java.lang.System.*;

class client {
   private static Socket server;
   private static chatter.options info;
   private static boolean connected;
   client(chatter.options opts){
      info = opts;
      out.println("Client started: " + info.username);
      try {
         Socket socket = new Socket (info.server_hostname, 
               info.server_portnumber);
         out.println("Connection accepted: " + 
               info.server_hostname + ":" + info.server_portnumber);
         server = socket;
         connected = true;
         Scanner sockin = new Scanner(socket.getInputStream());
         Thread receiver = new Thread (new receiver (sockin));
         Thread sender = new Thread (new sender (
               new PrintWriter (socket.getOutputStream ())));
         receiver.start();
         sender.start();
         //Wait for sender to exit, signalling that the client
         //is read to exit, then closes the receiving scanner,
         //causing the receiver thread to exit its read loop.
         //Not really sure why the blank print statement was
         //needed to make this loop stay active...but it didn't
         //work without it.
         while(connected){
          out.print("");
         }
         sockin.close();
         socket.close();
      }catch(IOException exn){
         err.printf("%s%n", exn);
      }catch(IllegalArgumentException exn){
         err.printf("%s%n", exn);
      }
   }

   static class receiver implements Runnable {

      Scanner reader;
      receiver(Scanner input){
         reader = input;
      }
      public void run () {
         //Reads input until scanner is closed, or server is closed,
         //causing reader.nextLine() to throw a NoSuchElementException 
         try{
            while(true){
               String line = "";
               if(!connected) break; 
               line += reader.nextLine();
               out.printf("%s%n", line);
            } 
         }catch(IllegalStateException exn){
         }catch(NoSuchElementException exn){
            connected = false;
            out.println("Connection to server lost.");
         }
      }
   }


   static class sender implements Runnable {
      PrintWriter sender;
      sender(PrintWriter out){
         this.sender = out;
      }

      public void run () {
         Scanner input = new Scanner(System.in);
         while(input.hasNextLine()){
            if(!connected){
               err.println("Server connection lost.");
               break;
            }
            String line = input.nextLine();
            sender.printf("%s: %s%n", info.username, line);
            sender.flush();
            if(sender.checkError()){
               connected = false;
               err.printf("sender: %s%n", sender);
               break;
            }
         }
         sender.close();
         input.close();
         connected = false;
      }
   }
}
