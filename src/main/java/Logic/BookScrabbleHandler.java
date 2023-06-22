package Logic;

import java.io.*;
import java.net.Socket;


public class BookScrabbleHandler implements ClientHandler{
    DictionaryManager dm;
    private String[] fileNames;
    private  BooksDirectoryReader books= new BooksDirectoryReader();

    private boolean stop;

    public BookScrabbleHandler(){ // Need to get names of books
        this.dm = DictionaryManager.get();
        fileNames = new String[3];
        for(int i=0; i<2;i++)
        {
            fileNames[i]= books.getBooks()[i];
        }
        stop = false;
    }

    @Override
    public void handleClient(Socket client)  {
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(),true);
            while(!stop){
                String word;
                word=in.readLine();
                if(word==null)
                    continue;
                fileNames[2]=word;
                if( this.dm.query(fileNames)||this.dm.challenge(fileNames)){
                    out.print("true\n");
                    out.flush();
                    continue;
                }
                out.print("false\n");
                out.flush();
            }
        }catch (IOException e){

        }

    }

    @Override
    public void close() {
        stop = true;
    }


}