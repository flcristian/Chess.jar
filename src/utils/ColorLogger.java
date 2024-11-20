package utils;

import java.util.logging.*;

public class ColorLogger {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_WHITE = "\u001B[37m";  // For logger metadata (timestamp, level)

    private final Logger logger;

    public ColorLogger(Class<?> c) {
        this.logger = Logger.getLogger(c.getName());
        setupColorFormatter();
    }

    private void setupColorFormatter() {
        Logger rootLogger = Logger.getLogger("");
        for (Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                String color;

                switch (record.getLevel().getName()) {
                    case "INFO":
                        color = ANSI_GREEN;
                        break;
                    case "WARNING":
                        color = ANSI_YELLOW;
                        break;
                    case "SEVERE":
                        color = ANSI_RED;
                        break;
                    case "FINE":
                        color = ANSI_BLUE;
                        break;
                    default:
                        color = ANSI_RESET;
                }

                // Format the log with timestamp, level, and message
                return String.format(ANSI_WHITE + "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %2$-7s " + ANSI_RESET +
                                "%3$s%4$s" + ANSI_RESET + "%n",
                        record.getMillis(),
                        record.getLevel().getName(),
                        color,
                        record.getMessage());
            }
        });

        logger.setLevel(Level.FINE);
        consoleHandler.setLevel(Level.FINE);

        logger.addHandler(consoleHandler);
        logger.setUseParentHandlers(false);
    }

    public void info(String message) {
        logger.info(message);
    }

    public void warning(String message) {
        logger.warning(message);
    }

    public void severe(String message) {
        logger.severe(message);
    }

    public void debug(String message) {
        logger.fine(message);
    }
}
