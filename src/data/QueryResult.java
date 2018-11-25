package data;

import java.util.ArrayList;
import java.util.List;

public class QueryResult extends Result{

    public ArrayList<ArrayList<String>> getData() {
        return data;
    }

    private ArrayList<ArrayList<String>> data;

    public QueryResult(boolean isSuccessful, ArrayList<ArrayList<String>> data) {
        super(isSuccessful);
        this.data = data;
    }
}
