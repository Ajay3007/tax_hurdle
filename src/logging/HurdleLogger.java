package logging;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.RolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.layout.PatternLayout;

public class HurdleLogger {
	private static final String LOGGER_NAME = null;
	private Logger logger;
	private Level logLevel = Level.INFO;
	private String logFilePath = "./logs";
	private String maxBackupIndex = "5";
	private String maxFileSize = "10KB";
	private Property propertyFileName;
	
	public HurdleLogger() {
		
	}
	
	public void initializeLogger() throws FileNotFoundException, IOException {
		System.out.println("init started " + logLevel);
		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		final Configuration config = ctx.getConfiguration();
		
		PatternLayout layout = PatternLayout.newBuilder()
				.withPattern("%d{HH:mm:ss.SSS} %-5level (%F:%L) - %msg%n%throwable{short.lineNumber}")
				.withConfiguration(config).build();
		ConsoleAppender console = ConsoleAppender.newBuilder().setLayout(layout).setName("console").build();
		console.start();
		
		TriggeringPolicy sizePolicy = SizeBasedTriggeringPolicy.createPolicy(maxFileSize);
		RolloverStrategy strategy = DefaultRolloverStrategy.newBuilder().withMax(maxBackupIndex).withMin("1")
				.withFileIndex(null).withCompressionLevelStr(null).withCustomActions(null)
				.withStopCustomActionsOnError(false).withConfig(config).build();
		
		RollingFileAppender fileAppender = RollingFileAppender.newBuilder().setLayout(layout)
				.withFileName(logFilePath + "/" + propertyFileName.getValue() + ".log").setName("rolling")
				.withFilePattern(logFilePath + "/" + propertyFileName.getValue() + "_%d{yyyy-MM-dd}_%i.log")
				.withPolicy(sizePolicy).withStrategy(strategy).build();
		fileAppender.start();
		config.addAppender(fileAppender);
		AppenderRef consoleRef = AppenderRef.createAppenderRef("console", logLevel, null);
		AppenderRef fileRef = AppenderRef.createAppenderRef("rolling", logLevel, null);
		AppenderRef[] refs = new AppenderRef[] {consoleRef, fileRef};
		@SuppressWarnings("deprecation")
		LoggerConfig loggerConfig = LoggerConfig.createLogger(false, logLevel, LOGGER_NAME, "true", refs, null, config, null);
		loggerConfig.addAppender(fileAppender, null, null);
		loggerConfig.addAppender(console, null, null);
		config.addLogger(LOGGER_NAME, loggerConfig);
		ctx.updateLoggers();
		logger = LogManager.getLogger(LOGGER_NAME);
		System.out.println("init ended");
		
	}
	
	public Logger getLogger() {
		return logger;
	}
	
}
