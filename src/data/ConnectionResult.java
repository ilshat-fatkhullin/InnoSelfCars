package data;

import controllers.SqlController;

public class ConnectionResult extends Result {

    public SqlController getSqlController() {
        return sqlController;
    }

    private SqlController sqlController;

    public ConnectionResult(boolean isSuccessful, SqlController sqlController) {
        this.isSuccessful = isSuccessful;
        this.sqlController = sqlController;
    }
}
