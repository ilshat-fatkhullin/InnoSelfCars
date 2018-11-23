import pyodbc


class SqlController:
    connection = None

    def __init__(self):
        server = 'innoselfcar.database.windows.net'
        database = 'innoselfcardatabase'
        username = 'innoselfcaradmin'
        password = 'zvWiZcGNcTn6hy54'
        driver = '{ODBC Driver 13 for SQL Server}'
        cnxn = pyodbc.connect(
            'DRIVER=' + driver + ';SERVER=' + server + ';PORT=1433;DATABASE=' + database + ';UID=' + username + ';PWD=' + password)

    def execute(self, query):
        cursor = self.connection.cursor()
        cursor.execute(query)
        return cursor
