@echo off
java -Dfile.encoding=UTF-8 -classpath %~dp0\lib\javafx-controls-21.0.2.jar;%~dp0\lib\javafx-graphics-21.0.2.jar;%~dp0\lib\javafx-base-21.0.2.jar;%~dp0\lib\javafx-fxml-21.0.2.jar -p %~dp0\classes;%~dp0\lib\javafx-fxml-21.0.2-win.jar;%~dp0\lib\javafx-graphics-21.0.2-win.jar;%~dp0\lib\javafx-controls-21.0.2-win.jar;%~dp0\lib\postgresql-42.7.1.jar;%~dp0\lib\mysql-connector-j-8.3.0.jar;%~dp0\lib\javafx-base-21.0.2-win.jar;%~dp0\lib\commons-csv-1.10.0.jar -m com.coretempparser/com.coretempparser.MainClass -p
pause
