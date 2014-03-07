// $Id: chatter.java,v 1.1 2013-08-08 14:10:22-07 - - $
//CMPS 109 Summer 2013 Assignment 5
//Starter code provided by: Wesley Mackey (mackey@soe.ucsc.edu)
//Edited and added to by: Robert Calef (rcalef@ucsc.edu)
import static java.lang.System.*;

class chatter {

   static class options {
      String traceflags;
      boolean is_server;
      String server_hostname;
      int server_portnumber;
      String username;
      options (String[] args) {
         if(args.length > 3 || args.length < 1){
            err.println("Usage: chatter [-@flags] port");
            err.println("       chatter [-@flags] " +
                  "hostname:port username");
            exit(1);
         }
         int optind = 0;
         if (args[0].charAt(0) == '-'){
            if(args[0].charAt(1) == '@') traceflags = args[0].substring(2);
            else err.println("Invalid option: " + args[0]);
            ++optind;
         }
         //If only argument other than options, then server is 
         //being specified
         if(args.length - optind == 1){
            is_server = true;
            server_portnumber = Integer.parseInt(args[optind]);
         }else{
            is_server = false;
            String[] server_info = new String[2];
            server_info = args[optind].split(":");
            server_hostname = server_info[0];
            server_portnumber = Integer.parseInt(server_info[1]);
            username = args[optind + 1];
         }
      }
   }

   public static void main (String[] args) {
      options opts = new options (args);
      if(opts.is_server){
         server serve = new server(opts);
      }else{
         client user = new client(opts);
      }
   }

}
