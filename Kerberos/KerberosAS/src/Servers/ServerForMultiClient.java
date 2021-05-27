package Servers;

import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;


public class ServerForMultiClient {
    private int port=7777;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private final int POOL_SIZE=10;
    
    public ServerForMultiClient() throws IOException{
        serverSocket=new ServerSocket(port);

        executorService=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*POOL_SIZE);
        System.out.println("AS服务器启动");
    }
    public void service(){
        while(true){
            Socket socket=null;
            try {

                socket=serverSocket.accept();
                executorService.execute(new AS(socket));
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    public static void main(String[] args) throws IOException {
        new ServerForMultiClient().service();
    }
}