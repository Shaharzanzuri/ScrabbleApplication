package Logic;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MyServer {
    private ClientHandler ch;
    private int port;
    private boolean stop;
    private ServerSocket server;
    private ExecutorService threadPool;

    public MyServer(int port, ClientHandler ch) {
        this.port = port;
        this.ch = ch;
        stop = false;
        threadPool = Executors.newCachedThreadPool(); // Set maximum of 3 threads in the pool
    }

    public void start() {
        new Thread(() -> startServer()).start();
    }

    private void startServer() {
        try {
            server = new ServerSocket(this.port);
            server.setSoTimeout(1000);
            while (!stop) {
                try {
                    Socket client = server.accept();
                    threadPool.execute(() -> ch.handleClient(client));
                } catch (SocketTimeoutException e) {
                }
            }
            // Delay in order to guest to recieve the message
            Thread.sleep(3000);
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() throws InterruptedException {
        stop = true;
        try {
            threadPool.shutdown();
            threadPool.awaitTermination(3, TimeUnit.SECONDS);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
