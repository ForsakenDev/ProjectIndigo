package co.forsaken.projectindigo.data.log;

import java.util.logging.Level;

public class Logger {

    private enum LogLevel {
        INFO, WARN, ERROR
    };

    private static java.util.logging.Logger _logger = java.util.logging.Logger.getLogger("launcher");

    public static void log(String message, LogLevel level, Throwable t) {
        switch (level) {
            case INFO:
                _logger.log(Level.INFO, message);
                break;
            case WARN:
                _logger.log(Level.WARNING, message);
                break;
            case ERROR:
                _logger.log(Level.SEVERE, message);
                if (t != null) {
                    t.printStackTrace();
                }
                break;
        }
    }

    public static void logInfo(String message) {
        logInfo(message, null);
    }

    public static void logWarn(String message) {
        logWarn(message, null);
    }

    public static void logError(String message) {
        logError(message, null);
    }

    public static void logInfo(String message, Throwable t) {
        log(message, LogLevel.INFO, t);
    }

    public static void logWarn(String message, Throwable t) {
        log(message, LogLevel.WARN, t);
    }

    public static void logError(String message, Throwable t) {
        log(message, LogLevel.ERROR, t);
    }

}
