package pk.com.habsoft.robosim.utils;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class RobotLogger {
    
    private RobotLogger() {
        
    }
    
	public static Logger getLogger(String name) {

		// very simple configuration to print on console
		// Its patterns is = "%-4r [%t] %-5p %c %x - %m%n".
		if (name != null && name.length() > 0) {
			PropertyConfigurator.configure("log4j.properties");
			// BasicConfigurator.configure();
		} else {
			BasicConfigurator.configure();
		}

		// get a logger instance named "com.foo"
		// Logger logger = Logger.getLogger("com.foo");
		// ApacheLogTest.class.getResource("log4j.properties");
		Logger logger = Logger.getLogger(name);

		// Now set its level. Normally you do not need to set the
		// level of a logger programmatically. This is usually done
		// in configuration files.
		logger.setLevel(Level.ALL);

		// Logger barlogger = Logger.getLogger("com.foo.Bar");

		// The logger instance barlogger, named "com.foo.Bar",
		// will inherit its level from the logger named
		// "com.foo" Thus, the following request is enabled
		// because INFO >= INFO.
		// barlogger.info("Located nearest gas station.");

		// This request is disabled, because DEBUG < INFO.
		// barlogger.debug("Exiting gas station search");

		// ///////////// Appenders ////////////////////////

		// Log4j allows logging requests to print to multiple destinations. In
		// log4j speak, an output destination is called an appender.

		// Currently, appenders exist for the console, files, GUI components,
		// remote socket servers, JMS, NT Event Loggers, and remote UNIX Syslog
		// daemons. It is also possible to log asynchronously.

		// ///////////// Layouts ////////////////////////

		// More often than not, users wish to customize not only the output
		// destination but also the output format. This is accomplished by
		// associating a layout with an appender. The layout is responsible for
		// formatting the logging request according to the user's wishes,
		// whereas an appender takes care of sending the formatted output to its
		// destination
		return logger;
	}

}
