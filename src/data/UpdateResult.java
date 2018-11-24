package data;

public class UpdateResult extends Result {

    public int getCount() {
        return count;
    }

    private int count;

    public UpdateResult(boolean status, int count) {
        this.isSuccessful = status;
        this.count = count;
    }
}
