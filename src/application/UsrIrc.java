package application;

import java.util.ArrayList;
import java.util.List;

public class UsrIrc {
    private String nick;
    private String client;
    private String clientSimplified;
    private String exploit;
    private String thisclient;

    public UsrIrc(String aNick, String aClient, SampleController sController) {
        String[] partsClient = aClient.split(" ");

        this.nick             = aNick;
        this.client           = aClient;
        this.clientSimplified = partsClient[0];
        this.exploit          = getSploit(aClient, sController.sploit);

        // System.out.println(this.exploit);
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getClientSimplified() {
        return clientSimplified;
    }

    public void setClientSimplified(String clientSimplified) {
        this.clientSimplified = clientSimplified;
    }

    public String getExploit() {
        return exploit;
    }

    public void setExploit(String exploit) {
        this.exploit = exploit;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getSploit(String client, SploitParse sploit) {
        String       thisClient     = null;
        String       resultCve      = "";
        List<String> possibleClient = new ArrayList<String>();

        for (String object : sploit.getListeClient()) {
            if (client.contains(object)) {
                thisClient = object;
                possibleClient.add(thisClient);
            }
        }

        if (possibleClient.size() > 0) {
            for (String possibleClientI : possibleClient) {
                resultCve += " " + sploit.getMapSploit().get(possibleClientI);
            }

            return resultCve;
        } else {
            return "no";
        }
    }
}