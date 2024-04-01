
<h1><p align="center">CTP (core temp parser)</p></h1>
<p align="center">
<h2>Disclaimer</h2>
This is an educational and experimental project, which does not imply real use and does not guarantee the absence of
  any errors.
  <br>You can use it at your own risk!
</p>
<p>
<h2> About</h2>
</p>
<p>
  Core Temp Parser (CTP) is the additional software for <a href="https://www.alcpu.com/CoreTemp/">Core Temp</a> - CPU temperature and load monitoring tool.
  <br> CTP allows you to parse csv-logs, which have been written with Core Temp, 
  <br>write them to the SQL-like database (PostgreSQL or MySQL) and see them on a graph to find out how the temperature and load changed over time, like this:
  <br><p align="center"><a href="https://ibb.co/J7GrZvX"><img src="https://i.ibb.co/9T1yPbR/CTP.png" alt="CTP" border="0"></a></p>
  <br>Records contains data about each CPU core.
  <br>All this can be doing on many computers of the web simultaneously, and collect data in one database.
  <br>There are two main modes, in which CTP can working: GUI for parcing and seeing the graphs, also defining settings,
  <br>and command line - for silent parsing only, without GUI.
</p>
<h2>What you'll need</h2>
<ul>
  <li>Java 21</li>
  <li><a href="https://www.alcpu.com/CoreTemp/">Core Temp</a></li>
  <li>CTP (Core Temp Parser) itself</li>
  <li>One of database servers: PostgreSQL or MySQL</li>
</ul>
<br>Java, Core Temp and CTP must be installed on every computer which you want to monitor, and one SQL-database on the server.
<br>You must be sure, that database server have open needed port, there is a user in database for this purpose,
<br>access has been granted to the computers from which records will be received.
<h2>How to use</h2>
<p>Although i'm sharing here all the project, for use only you'll need the directory CTP.
<br>For convenience i created a few starters - for different modes:
<ul>
  <li>start_GUI.bat - launch with GUI</li>
  <li>start_once.bat - start in command line and automatically parsing one time (with parameter -p)</li>
  <li>start_auto.bat - start in command line and infinite parsing (with parameter -a). To stopping the process you should press control+c and answer Y (yes)</li>
</ul>
For now it's only two parameters: -a and -p, for parsing once (-p) or parsing infinite (-a)
</p>
So:
<ul>
  <li>first you need to install and configure Core Temp on the desired computers so that it writes logs to csv files</li>
  <li>install java (if not installed previously) on the desired computers</li>
  <li>create a new database on your SQL-server, name it how you wish (remember that name)</li>
  <li>start CTP with GUI (start_GUI.bat) and on the tab "Parse" you must open properties with clicked on the "properties" button </li>
  <li>there is two panes: "user settings" and "system properties". System properties usually no need to change, 
  they tell you how the column names in the log correspond to the columns in the database.
  User settings must be specified. They contains the settings like Database IP - adress of your server SQL, Database name - which you came up with yourself,
  server port, type (PostgreSQL or MySQL), login and password of database user, directory, that contains logs, table name (you can give it any name - it doesn't matter), and some others, which you can leave as is. 
  Then press the button "Accept" for save changes</li>
  <li>you can press the button "check connection" for checking whether the connection to server is work or not</li>
  <li>after that you can copy the directory "properties" to other you computers, for use these settings on them.</li>
</ul>
Then, when all settings configured, you can use CTP some of these way:
<ul>
  <li>you must parsing the logs on every other computers, data of which you wanted to see. 
    It may be silence mode with auto-parsing, for example, if you dot'n need a GUI on that, or you can do it manually time after time (with or without GUI).</li>
  <li>on the tab "Chart" (screenshots is placed below) you can starting and stopped auto-parsing, which will parse logs constantly (button "Autoparse")</li>
  <li>also you can viewing the data on the charts in real-time (button "Auto refresh") 
    or find some specific period of time (choose day and time in fields "from" and "to" and press "Ok")</li>
    <li>to choose the computer, which data you need to explore, there is choiseBox "computer"</li>
    <li>button "clear" - is for clearing charts</li>
    <li>on the tab "Parse" you can start parsing once (button "Parse"), stop parsing immediately and forcefully (button "Stop"), 
    open properties (button "Properties"), clear database (button "Delete all base") 
    and also turn on or turn off parsing log (checkbox "Show log") and see the log itself in the field "Log"(don't use it for very long time because it uses memory)</li>
</ul>
<br>Also there is some user properties as:
<ul>
  <li>"Max count of points on charts" - it's only for non-automatic refresh mode: 
    if you looking for some period of time and count of records is very big in them, this setting will round them up to the desired number</li>
    <li>"Number of minutes viewed in the chart" - if you do autoparsing and autorefresh charts, this setting will point how many last minutes will be shown on the graph</li>
    <li>"Max count of thread for parsing" - describes, how many files can be parsed simultaneously</li>
</ul>
<br> Note: after starting, the programm will be continue the parsing files starting from the last date, finded in database for each computer apart, not each file every time.
<br><p align="center"><a href="https://ibb.co/rwpTJJ2"><img src="https://i.ibb.co/qrgXwwY/2024-04-01-13-44-58.png" alt="Tab Chart" border="0"></a>
<a href="https://ibb.co/sHv7ZVq"><img src="https://i.ibb.co/jRvtSrD/2024-04-01-13-43-58.png" alt="Tab Parse" border="0"></a>
<br>User settings and System properties pics are below:
<br>
<a href="https://ibb.co/zS791tM"><img src="https://i.ibb.co/zS791tM/2024-04-01-14-52-36.png" alt="User settings" border="0"></a>
<a href="https://ibb.co/Ns6Xs9P"><img src="https://i.ibb.co/Ns6Xs9P/2024-03-29-18-53-22.png" alt="System properties" border="0"></a>

</p>

<h2>Version history</h2>
- 1.0 - first version
<h2>Author</h2>
<a href="https://github.com/AlekseevALX">ALX</a>

