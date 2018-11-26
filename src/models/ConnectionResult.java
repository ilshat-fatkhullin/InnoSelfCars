package models;

import controllers.SqlController;

public class ConnectionResult extends Result {

    public SqlController getSqlController() {
        return sqlController;
    }

    private SqlController sqlController;

    public ConnectionResult(boolean isSuccessful, SqlController sqlController) {
        super(isSuccessful);
        this.sqlController = sqlController;
    }
}
