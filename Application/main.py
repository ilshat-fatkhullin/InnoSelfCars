from sql_controller import SqlController

sql_controller = SqlController()

while True:
    input_line = input()
    execution_result = sql_controller.execute(input_line)
