statusListener(OnConsoleStatusListener)

def appenderList = ["STDOUT"]

addInfo("Hostname is ${hostname}")

appender("STDOUT", ConsoleAppender) {
	encoder(PatternLayoutEncoder) {
		pattern = "%d{yyyy-MM-dd-HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
	}
}

appender("FILE", RollingFileAppender) {
    file = "log/virussim.log";
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "log/virussim.%d{yyyy-MM-dd}.log"
        maxHistory = 10
    }
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd-HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    }
}

root(DEBUG, appenderList)