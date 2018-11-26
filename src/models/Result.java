package models;

public class Result {
    public boolean isSuccessful() {
        return isSuccessful;
    }

    protected boolean isSuccessful;

    public Result(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }
}
