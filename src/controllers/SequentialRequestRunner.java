package controllers;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;

public class SequentialRequestRunner {

    private final char divider = '#';

    private int currentRequestIndex;

    private ArrayList<String> requests;

    public SequentialRequestRunner(String path) {
        requests = new ArrayList<>();
        File requestFile = new File(path);

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(requestFile));

            StringBuilder request = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0)
                    continue;
                request.append(' ').append(line);
                if (line.matches(".*;")) {
                    requests.add(request.toString().trim());
                    request = new StringBuilder();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String nextRequest() {
        String request = requests.get(currentRequestIndex);
        currentRequestIndex++;
        return request;
    }

    public boolean isFinished() {
        return currentRequestIndex == requests.size();
    }

    public boolean isWaitingForInput() {
        return requests.get(currentRequestIndex).matches(".*" + divider + "[^#.]*" + divider + ".*");
    }

    public String getInputDescription() {
        return requests.get(currentRequestIndex).split(String.valueOf(divider))[1];
    }

    public void setInput(String input) {
        requests.set(currentRequestIndex, requests.get(currentRequestIndex).replaceAll(divider + getInputDescription() + divider, input));
    }
}
