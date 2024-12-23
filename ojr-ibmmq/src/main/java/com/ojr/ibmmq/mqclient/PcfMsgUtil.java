package com.ojr.ibmmq.mqclient;

import com.ibm.mq.headers.pcf.PCFMessage;
import com.ojr.core.DcUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class PcfMsgUtil {

    /**
     * Converts a PCF message string parameter to a trimmed string.
     *
     * @param message   The PCF message
     * @param parameter The integer identifier for the parameter.
     * @return The trimmed string value of the parameter or DcUtil.N_A if an exception occurs.
     */
    public static String getString(PCFMessage message, int parameter) {
        try {
            return message.getStringParameterValue(parameter).trim();
        } catch (Exception ignored) {
            // Ignored
        }
        return DcUtil.N_A;
    }

    /**
     * Converts a PCF message integer parameter to an integer.
     *
     * @param message      The PCF message
     * @param parameter    The integer identifier for the parameter.
     * @param defaultValue The default value to return if an exception occurs.
     * @return The integer value of the parameter or the default value if an exception occurs.
     */
    public static int getInt(PCFMessage message, int parameter, int defaultValue) {
        try {
            return message.getIntParameterValue(parameter);
        } catch (Exception ignored) {
            // Ignored
        }
        return defaultValue;
    }

    /**
     * Converts a PCF message 64-bit integer parameter to a long.
     *
     * @param message      The PCF message
     * @param parameter    The integer identifier for the parameter.
     * @param defaultValue The default value to return if an exception occurs.
     * @return The long value of the parameter or the default value if an exception occurs.
     */
    public static long getInt64(PCFMessage message, int parameter, long defaultValue) {
        try {
            return message.getInt64ParameterValue(parameter);
        } catch (Exception ignored) {
            // Ignored
        }
        return defaultValue;
    }

    /**
     * Converts a date parameter and a time parameter into an Epoch long integer.
     *
     * @param message   The PCF message
     * @param paramDate The integer identifier for the parameter of Date.
     * @param paramTime The integer identifier for the parameter of Time.
     * @return An Epoch long integer.
     */
    public static DateTime getDateTime(PCFMessage message, int paramDate, int paramTime) {
        try {
            String dateString = message.getStringParameterValue(paramDate).trim();
            String timeString = message.getStringParameterValue(paramTime).trim();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH.mm.ss");

            LocalDate date = LocalDate.parse(dateString, dateFormatter);
            LocalTime time = LocalTime.parse(timeString, timeFormatter);

            ZonedDateTime zonedDateTime = ZonedDateTime.of(date, time, ZoneId.systemDefault());

            return new DateTime(zonedDateTime);
        } catch (Exception e) {
            //Ignored
        }
        return new DateTime(null);
    }

    public static class DateTime {
        private final ZonedDateTime zonedDateTime;

        public DateTime(ZonedDateTime zonedDateTime) {
            this.zonedDateTime = zonedDateTime;
        }

        public long getEpochSecond() {
            if (zonedDateTime == null)
                return -1;

            return zonedDateTime.toEpochSecond();
        }

        public String format(String format) {
            if (zonedDateTime == null)
                return DcUtil.N_A;

            return zonedDateTime.format(DateTimeFormatter.ofPattern(format));
        }

        public double formatAsNumber() {
            if (zonedDateTime == null)
                return -1;

            return Double.parseDouble(format("yyyyMMdd.HHmmss"));
        }
    }

    /**
     * Inner class defining various status constants.
     */
    public static class Status {
        public final static String STARTING = "STARTING";
        public final static String RUNNING = "RUNNING";
        public final static String QUIESCING = "QUIESCING";
        public final static String STANDBY = "STANDBY";
        public final static String STOPPED = "STOPPED";
        public final static String STOPPING = "STOPPING";
        public final static String RETRYING = "RETRYING";
        public final static String INACTIVE = "INACTIVE";
        public final static String BINDING = "BINDING";
        public final static String REQUESTING = "REQUESTING";
        public final static String PAUSED = "PAUSED";
        public final static String INITIALIZING = "INITIALIZING";
        public final static String SWITCHING = "SWITCHING";
        public final static String N_A = DcUtil.N_A;
    }

    /**
     * Converts a queue manager status code to its corresponding string representation.
     *
     * @param status The status code.
     * @return The string representation of the status code or Status.N_A if unknown.
     */
    public static String convertQMStatus(int status) {
        switch (status) {
            case 1:
                return Status.STARTING;
            case 2:
                return Status.RUNNING;
            case 3:
                return Status.QUIESCING;
            case 4:
                return Status.STANDBY;
            default:
                return Status.N_A;
        }
    }

    /**
     * Converts a channel initiator status code to its corresponding string representation.
     *
     * @param status The status code.
     * @return The string representation of the status code or Status.N_A if unknown.
     */
    public static String convertChannelInitiatorStatus(int status) {
        switch (status) {
            case 0:
                return Status.STOPPED;
            case 1:
                return Status.STARTING;
            case 2:
                return Status.RUNNING;
            case 3:
                return Status.STOPPING;
            case 4:
                return Status.RETRYING;
            default:
                return Status.N_A;
        }
    }

    /**
     * Converts a platform code to its corresponding string representation.
     *
     * @param platform The platform code.
     * @return The string representation of the platform code or DcUtil.N_A if unknown.
     */
    public static String convertPlatform(int platform) {
        switch (platform) {
            case 1:
                return "z/OS";
            case 2:
                return "OS2";
            case 3:
                return "AIX/UNIX/LINUX";
            case 4:
                return "IBM";
            case 5:
                return "Windows";
            case 11:
                return "Windows NT";
            case 13:
                return "HP Integrity NonStop Server";
            case 28:
                return "IBM MQ Appliance";
            default:
                return DcUtil.N_A;
        }
    }

    /**
     * Converts a queue type code to its corresponding string representation.
     *
     * @param type The queue type code.
     * @return The string representation of the queue type code or DcUtil.N_A if unknown.
     */
    public static String getQueueType(int type) {
        switch (type) {
            case 1:
                return "Local";
            case 2:
                return "Alias";
            case 3:
                return "Remote";
            case 4:
                return "Queue manager alias";
            default:
                return DcUtil.N_A;
        }
    }

    /**
     * Converts a queue inhibit code to its corresponding string representation.
     *
     * @param type The queue inhibit code.
     * @return The string representation of the queue inhibit code or DcUtil.N_A if unknown.
     */
    public static String getQueueInhibit(int type) {
        switch (type) {
            case 0:
                return "Allow";
            case 1:
                return "Inhibit";
            default:
                return DcUtil.N_A;
        }
    }

    /**
     * Converts a queue delivery code to its corresponding string representation.
     *
     * @param type The queue delivery code.
     * @return The string representation of the queue delivery code or DcUtil.N_A if unknown.
     */
    public static String getQueueDelivery(int type) {
        switch (type) {
            case 0:
                return "Priority";
            case 1:
                return "Fifo";
            default:
                return DcUtil.N_A;
        }
    }

    /**
     * Converts a queue usage code to its corresponding string representation.
     *
     * @param type The queue usage code.
     * @return The string representation of the queue usage code or DcUtil.N_A if unknown.
     */
    public static String getQueueUsage(int type) {
        switch (type) {
            case 0:
                return "Normal";
            case 1:
                return "Transmission";
            default:
                return DcUtil.N_A;
        }
    }

    /**
     * Converts a queue monitoring code to its corresponding string representation.
     *
     * @param type The queue monitoring code.
     * @return The string representation of the queue monitoring code or DcUtil.N_A if unknown.
     */
    public static String getQueueMonitoring(int type) {
        switch (type) {
            case -3:
                return "Queue manager";
            case -1:
                return "Not available";
            case 0:
                return "Disabled";
            case 1:
                return "Enabled";
            case 17:
                return "Low";
            case 33:
                return "Medium";
            case 65:
                return "High";
            default:
                return DcUtil.N_A;
        }
    }

    /**
     * Converts a queue default binding code to its corresponding string representation.
     *
     * @param type The queue default binding code.
     * @return The string representation of the queue default binding code or DcUtil.N_A if unknown.
     */
    public static String getQueueDefaultBinding(int type) {
        switch (type) {
            case 0:
                return "On open";
            case 1:
                return "Not fixed";
            case 2:
                return "On group";
            default:
                return DcUtil.N_A;
        }
    }

    /**
     * Converts a queue usage MQQSO code to its corresponding string representation.
     *
     * @param type The queue usage MQQSO code.
     * @return The string representation of the queue usage MQQSO code or DcUtil.N_A if unknown.
     */
    public static String getQueueUsageMQQSO(int type) {
        switch (type) {
            case 0:
                return "No";
            case 1:
                return "Yes";
            case 2:
                return "Exclusive";
            default:
                return DcUtil.N_A;
        }
    }

    /**
     * Converts a queue usage handle type code to its corresponding string representation.
     *
     * @param type The queue usage handle type code.
     * @return The string representation of the queue usage handle type code or DcUtil.N_A if unknown.
     */
    public static String getQueueUsageHandleType(int type) {
        switch (type) {
            case 0:
                return "Inactive";
            case 1:
                return "Active";
            default:
                return DcUtil.N_A;
        }
    }

    /**
     * Converts a listener status code to its corresponding string representation.
     *
     * @param status The status code.
     * @return The string representation of the status code or DcUtil.N_A if unknown.
     */
    public static String getListenerStatus(int status) {
        switch (status) {
            case 0:
                return Status.STOPPED;
            case 1:
                return Status.QUIESCING;
            case 2:
                return Status.RUNNING;
            case 3:
                return Status.STARTING;
            case 4:
                return Status.RETRYING;
            default:
                return DcUtil.N_A;
        }
    }

    /**
     * Converts a topic type code to its corresponding string representation.
     *
     * @param type The topic type code.
     * @return The string representation of the topic type code or DcUtil.N_A if unknown.
     */
    public static String getTopicType(int type) {
        switch (type) {
            case 0:
                return "Local";
            case 1:
                return "Cluster";
            case 2:
                return "All";
            default:
                return DcUtil.N_A;
        }
    }

    /**
     * Converts a subscription type code to its corresponding string representation.
     *
     * @param type The subscription type code.
     * @return The string representation of the subscription type code or DcUtil.N_A if unknown.
     */
    public static String convertSubType(int type) {
        switch (type) {
            case 1:
                return "API";
            case 2:
                return "ADMIN";
            case 3:
                return "PROXY";
            case -1:
                return "ALL";
            case -2:
                return "USER";
            default:
                return DcUtil.N_A;
        }
    }

    /**
     * Converts a durable flag to its corresponding string representation.
     *
     * @param durable The durable flag.
     * @return The string representation of the durable flag or DcUtil.N_A if unknown.
     */
    public static String convertDurable(int durable) {
        switch (durable) {
            case 1:
                return "YES";
            case 2:
                return "NO";
            case -1:
                return "ALL";
            default:
                return DcUtil.N_A;
        }
    }

    /**
     * Converts a channel status code to its corresponding string representation.
     *
     * @param status The status code.
     * @return The string representation of the status code or Status.N_A if unknown.
     */
    public static String convertChannelStatus(int status) {
        switch (status) {
            case 0:
                return Status.INACTIVE;
            case 1:
                return Status.BINDING;
            case 2:
                return Status.QUIESCING;
            case 3:
                return Status.RUNNING;
            case 4:
                return Status.STOPPING;
            case 5:
                return Status.RETRYING;
            case 6:
                return Status.STOPPED;
            case 7:
                return Status.REQUESTING;
            case 8:
                return Status.PAUSED;
            case 13:
                return Status.INITIALIZING;
            case 14:
                return Status.SWITCHING;
            default:
                return DcUtil.N_A;
        }
    }

    /**
     * Converts a channel in doubt flag to its corresponding string representation.
     *
     * @param status The in doubt flag.
     * @return The string representation of the in doubt flag or DcUtil.N_A if unknown.
     */
    public static String convertChannelInDoubt(int status) {
        switch (status) {
            case 0:
                return "No";
            case 1:
                return "Yes";
            default:
                return DcUtil.N_A;
        }
    }

    /**
     * Converts a channel sub-status code to its corresponding string representation.
     *
     * @param status The sub-status code.
     * @return The string representation of the sub-status code or DcUtil.N_A if unknown.
     */
    public static String convertChannelSubStatus(int status) {
        switch (status) {
            case 0:
                return "Other";
            case 100:
                return "End of batch";
            case 200:
                return "Sending";
            case 300:
                return "Receiving";
            case 400:
                return "Serializing";
            case 500:
                return "Resynching";
            case 600:
                return "Heartbeating";
            case 700:
                return "IN SCYEXIT";
            case 800:
                return "IN RCVEXIT";
            case 900:
                return "IN SENDEXIT";
            case 1000:
                return "IN MSGEXIT";
            case 1100:
                return "IN MREXIT";
            case 1200:
                return "IN CHADEXIT";
            case 1250:
                return "NET Connecting";
            case 1300:
                return "SSL Handshaking";
            case 1400:
                return "Name server";
            case 1500:
                return "IN MQPUT";
            case 1600:
                return "IN MQGET";
            case 1700:
                return "IN MQI_CALL";
            case 1800:
                return "Compressing";
            default:
                return DcUtil.N_A;
        }
    }

    /**
     * Converts a protocol code to its corresponding string representation.
     *
     * @param protocol The protocol code.
     * @return The string representation of the protocol code or DcUtil.N_A if unknown.
     */
    public static String convertProtocol(int protocol) {
        switch (protocol) {
            case 1:
                return "MQTTV3";
            case 2:
                return "HTTP";
            case 3:
                return "AMQP";
            case 4:
                return "MQTTV311";
            default:
                return DcUtil.N_A;
        }
    }
}
