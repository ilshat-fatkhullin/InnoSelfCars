package models;

public class UpdateResult extends Result {

    public int getCount() {
        return count;
    }

    private int count;

    public UpdateResult(boolean isSuccessful, int count) {
        super(isSuccessful);
        this.count = count;
    }
}
