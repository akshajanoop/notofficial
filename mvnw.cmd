@echo off
setlocal
set "DIRNAME=%~dp0"
if "%DIRNAME:~-1%"=="\" set "DIRNAME=%DIRNAME:~0,-1%"

set "MVN_CMD=C:\Users\aksha\.m2\wrapper\dists\apache-maven-3.9.6-bin\3311e1d4\apache-maven-3.9.6\bin\mvn.cmd"
if exist "%MVN_CMD%" (
    call "%MVN_CMD%" %*
    exit /b %ERRORLEVEL%
)

set JAVACMD=java
if not "%JAVA_HOME%" == "" (
    if exist "%JAVA_HOME%\bin\java.exe" set "JAVACMD=%JAVA_HOME%\bin\java.exe"
)

"%JAVACMD%" -Dmaven.multiModuleProjectDirectory="%DIRNAME%" -cp "%DIRNAME%\.mvn\wrapper\maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain %*
exit /b %ERRORLEVEL%
