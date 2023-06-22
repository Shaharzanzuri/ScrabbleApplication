package Logic;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public interface ClientHandler {
    void handleClient(Socket client);
    void close();
}