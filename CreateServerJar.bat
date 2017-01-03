SET javaloc="C:\Program Files\Java\jdk1.8.0_92\bin"
%javaloc%\javac *.java
%javaloc%\jar -cefv ServerGUI HangmanServer.jar HangmanServer*.class ConnectionInfo.class ServerGUI.class icon.png
%javaloc%\jar -tvf HangmanServer.jar
pause