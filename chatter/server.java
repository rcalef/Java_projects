// $Id: server.java,v 1.1 2013-08-08 14:10:22-07 - - $
//CMPS 109 Summer 2013 Assignment 5
//Starter code provided by: Wesley Mackey (mackey@soe.ucsc.edu)
//Edited and added to by: Robert Calef (rcalef@ucsc.edu)
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import static java.lang.System.*;

class server {
   private ServerSocket socket;
   private chatter.options info;
   private static long next_usr_nr = 0;
   private static LinkedBlockingQueue<String> input_queue;
   private static HashMap<Long, LinkedBlockingQueue<String>> user_map;

   server (chatter.options opts) {
      info = opts;
      input_queue = new LinkedBlockingQueue<String>();
      user_map = new HashMap<Long, LinkedBlockingQueue<String>>();
      out.println("Server started, port: " + info.server_portnumber);
      Thread manager = new Thread(new queue_manager());
      manager.start();
      try{
         socket = new ServerSocket(info.server_portnumber);
         for(;;){
            Socket client = socket.accept();
            out.println("Server: new client accepted");
            LinkedBlockingQueue<String> out_queue = 
                  new LinkedBlockingQueue<String>();
            user_map.put(++next_usr_nr, out_queue);
            Thread receiver = 
                     new Thread(new receiver(next_usr_nr, client));
            Thread sender = new Thread(new sender(client, out_queue));
            receiver.start();
            sender.start();
         }
      }catch(IOException exn){
         err.printf("%s%n", exn);
      }catch(IllegalArgumentException exn){
         err.printf ("%s%n", exn);
      }
   }
   
   static class queue_manager implements Runnable{
      public void run(){
          try{
             for(;;){
                String message = input_queue.take();
                out.println("Manager, message received: " + message);
                for(Map.Entry<Long, LinkedBlockingQueue<String>> entry:
                   user_map.entrySet()){
                   entry.getValue().put(message);
                }
             }
          }catch(InterruptedException exn){
             err.printf("%s%n", exn);
          }
      }
   }

   static class receiver implements Runnable {
      Socket client;
      long user_nr;
      receiver(long ID, Socket target){
         user_nr = ID;
         client = target;
      }
      public void run () {
         try{
            Scanner reader = new Scanner(client.getInputStream());
            while(reader.hasNextLine()){
               if(client.isInputShutdown()){
                  out.printf("Client disconnected");
                  user_map.remove(user_nr);
                  break;
               }
               String line = reader.nextLine();
               input_queue.put(line);
               out.println("Server, Message received: " + line);
            }reader.close();
         }catch(IOException exn){
            err.printf("%s%n", exn);
         }catch(InterruptedException exn){
            err.printf("%s%n", exn);
         }user_map.remove(user_nr);
          out.println("Client " + user_nr + " disconnected" );
      }
   }

   static class sender implements Runnable {
      Socket client;
      LinkedBlockingQueue<String> out_queue;
      
      sender(Socket target, LinkedBlockingQueue<String> output){
         client = target;
         out_queue = output;
      }
      public void run () {
         try{
            PrintWriter sender = 
                  new PrintWriter(client.getOutputStream());
            for(;;){
               if(client.isOutputShutdown()){
                 out.println("Client disconnected");
                 break;
               }
               String message = out_queue.take();
               sender.printf("%s%n",message);
               sender.flush();
               out.println("Server, message sent: " + message);
            }
         }catch(InterruptedException exn){
            err.printf("%s%n", exn);
         }catch(IOException exn){
            err.printf("%s%n", exn);
         }
      }
   }

}
