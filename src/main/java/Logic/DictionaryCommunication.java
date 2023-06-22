package Logic;

import java.io.*;
import java.net.Socket;

public class DictionaryCommunication {
    private static DictionaryCommunication dc = null;
    private Socket server;

    private DictionaryCommunication() {
        try {
            server = new Socket("localhost", 8887);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkIfWordValid(String word) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
            BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
            bw.write(word + "\n");
            bw.flush();
            return br.readLine().equals("true");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static DictionaryCommunication getInstance() {
        if (dc == null)
            dc = new DictionaryCommunication();
        return dc;
    }

}
