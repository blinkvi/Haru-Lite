package cc.unknown.util.client.system;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomLogger {
    public final Logger logger = LogManager.getLogger("Haru");

    public void info(String message) {
        logger.info(formatMessage(message));
    }

    public void info(String message, String message2) {
        logger.info(formatMessage(message, message2));
    }

    public void info(String message, String message2, String message3) {
        logger.info(formatMessage(message, message2, message3));
    }

    public void error(String message) {
        logger.error(formatMessage(message));
    }

    public void error(String message, Throwable t) {
        logger.error(formatMessage(message) + "\n" + t.getMessage(), t);
    }

    public void warn(String message) {
        logger.warn(formatMessage(message));
    }

    private String formatMessage(String message) {
        return "[INFO]: " + message;
    }

    private String formatMessage(String message, String message2) {
        return "[INFO]: " + String.format(message, message2);
    }

    private String formatMessage(String message, String message2, String message3) {
        return "[INFO]: " + String.format(message, message2, message3);
    }
}
