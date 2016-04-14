package application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SploitParse {
    private HashMap        mapSploit   = new HashMap();
    private List<String>   listeClient = new ArrayList<String>();
    private String         line;
    private BufferedReader br;

    public SploitParse() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("sploit.txt"));

            try {
                while ((line = br.readLine()) != null) {
                    String arr[] = line.split(":");

                    listeClient.add(arr[0]);
                    mapSploit.put(arr[0], arr[1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println(mapSploit);
        System.out.println(listeClient);
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public List<String> getListeClient() {
        return listeClient;
    }

    public void setListeClient(List<String> listeClient) {
        this.listeClient = listeClient;
    }

    public HashMap getMapSploit() {
        return mapSploit;
    }

    public void setMapSploit(HashMap mapSploit) {
        this.mapSploit = mapSploit;
    }
}