# InnoSelfCars

**InnoSelfCars** is a client-server desktop application based on JavaFX and PostgreSQL for car sharing company database management. It provides GUI which covers all core queries and furhermore in addition an ability to run custom queries in SQL language.

## 1. User interface

Main window consists of action buttons, choice boxes, terminal and table view.
- After starting the application user should all actions are locked, so user should wait until connection will be established. If connection is failed user should check its Internet connection and press **Reconnect** button.
- Result tables of custom queries are displayed in the table view. 
- Table choice box provides an ability to select needed table whereas click on **Show** button displays the result in table view.
- **Run** button click prints into terminal a result of execution of method selected in method choice box.
- User can revert all changes by clicking on **Discard changes** button which in its turn will drop all database tables, create them again and insert prepared rows. Logs of execution will be printed into terminal.
**Note**: the operation may take a long time to proceed.
- Terminal can be cleared by clicking on **Clear** button.

## 2. Implementation details

The architecture follows MVC *(Model View Controller)* principles. Project's hierarchy consists of three corresponding namespaces: models, controllers, views.

Here is a brief description of the main classes:
- **MainFormView** - initializes main window and customizes its view.
- **MainFormController** - handles all callbacks of the main window, implements logic of GUI and connects requests with SQL controllers.
- **RequestController** - provides an ability to make SQL requests and does it in background thread to keep GUI thread responding and sends request results in its callbacks.
- **SqlController** - creates a connection with database and provides raw SQL requests.
- **SequentialRequestController** - parses files containing sql queries and gives an interface to run them sequentially in terminal.
