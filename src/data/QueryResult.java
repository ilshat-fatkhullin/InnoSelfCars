package data;

public class QueryResult extends Result{

    public Object[] getResult() {
        return result;
    }

    private Object[] result;

    public QueryResult(boolean status, Object[] result) {
        this.isSuccessful = status;
        this.result = result;
    }
}
