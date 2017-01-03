SET javaloc="C:\Program Files\Java\jdk1.8.0_92\bin"
%javaloc%\javac *.java
%javaloc%\jar -cefv HangmanGUI HangmanClient.jar HangmanGUI*.class HangmanClient*.class DesignPanel.class ConnectionInfo.class icon.png splash.png
%javaloc%\jar -tvf HangmanClient.jar
pause