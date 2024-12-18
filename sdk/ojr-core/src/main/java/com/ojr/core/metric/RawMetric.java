package com.ojr.core.metric;

import com.ojr.core.DcUtil;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.LongHistogram;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Represents a raw metric with various configurations and data points.
 */
public class RawMetric {
    private static final Logger logger = Logger.getLogger(RawMetric.class.getName());

    private final String name; // Name of the metric
    private final MetricInstrumentType instrumentType; // Type of the metric instrument
    private final String description; // Description of the metric
    private final String unit; // Unit of measurement for the metric
    private final boolean isInteger; // Indicates if the metric is an integer type
    private final String attributeKey; // Key for attributes associated with the metric

    private MetricCalculationMode calculationMode; // Mode of calculation for the metric
    private static final double DEFAULT_RATE_UNIT = 1000; // Default rate unit for calculations
    private double rateUnit = DEFAULT_RATE_UNIT; // Rate unit for calculations
    private final Map<String, DataPoint> dps = new ConcurrentHashMap<>(); // Data points associated with the metric
    private static final long DEFAULT_OUTDATED_TIME = 125000L; // Default time after which data points are considered outdated
    private long outdatedTime = DEFAULT_OUTDATED_TIME; // Time after which data points are considered outdated
    private boolean clearDps = false; // Flag to indicate whether to clear data points
    private final String meterName; // Name of the meter associated with the metric
    private List<Long> longBucketBoundaries = null; // Bucket boundaries for long histograms
    private List<Double> doubleBucketBoundaries = null; // Bucket boundaries for double histograms
    private LongHistogram longHistogram = null; // Long histogram for recording long values
    private DoubleHistogram doubleHistogram = null; // Double histogram for recording double values

    /**
     * Constructs a new RawMetric with specified parameters.
     *
     * @param instrumentType Type of the metric instrument
     * @param name           Name of the metric
     * @param description    Description of the metric
     * @param unit           Unit of measurement for the metric
     * @param isInteger      Indicates if the metric is an integer type
     * @param attributeKey   Key for attributes associated with the metric
     * @param meterName      Name of the meter associated with the metric
     */
    public RawMetric(MetricInstrumentType instrumentType, String name, String description, String unit, boolean isInteger, String attributeKey, String meterName) {
        this.instrumentType = instrumentType;
        this.name = name;
        this.description = description;
        this.unit = unit;
        this.isInteger = isInteger;
        this.attributeKey = attributeKey;

        this.calculationMode = MetricCalculationMode.DIRECT;
        this.meterName = meterName;
    }

    /**
     * Constructs a new RawMetric with specified parameters and default meter name.
     *
     * @param instrumentType Type of the metric instrument
     * @param name           Name of the metric
     * @param description    Description of the metric
     * @param unit           Unit of measurement for the metric
     * @param isInteger      Indicates if the metric is an integer type
     * @param attributeKey   Key for attributes associated with the metric
     */
    public RawMetric(MetricInstrumentType instrumentType, String name, String description, String unit, boolean isInteger, String attributeKey) {
        this(instrumentType, name, description, unit, isInteger, attributeKey, DcUtil.DEFAULT);
    }

    /**
     * Returns the name of the meter associated with the metric.
     *
     * @return Name of the meter
     */
    public String getMeterName() {
        return meterName;
    }

    /**
     * Returns the name of the metric.
     *
     * @return Name of the metric
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type of the metric instrument.
     *
     * @return Type of the metric instrument
     */
    public MetricInstrumentType getInstrumentType() {
        return instrumentType;
    }

    /**
     * Returns the description of the metric.
     *
     * @return Description of the metric
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the unit of measurement for the metric.
     *
     * @return Unit of measurement for the metric
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Checks if the metric is an integer type.
     *
     * @return True if the metric is an integer type, false otherwise
     */
    public boolean isInteger() {
        return isInteger;
    }

    /**
     * Returns the key for attributes associated with the metric.
     *
     * @return Key for attributes associated with the metric
     */
    public String getAttributeKey() {
        return attributeKey;
    }

    /**
     * Returns the mode of calculation for the metric.
     *
     * @return Mode of calculation for the metric
     */
    public MetricCalculationMode getCalculationMode() {
        return calculationMode;
    }

    /**
     * Sets the mode of calculation for the metric.
     *
     * @param calculationMode Mode of calculation for the metric
     * @return This RawMetric instance
     */
    public RawMetric setCalculationMode(MetricCalculationMode calculationMode) {
        this.calculationMode = calculationMode;
        return this;
    }

    /**
     * Returns the rate unit for calculations.
     *
     * @return Rate unit for calculations
     */
    public double getRateUnit() {
        return rateUnit;
    }

    /**
     * Sets the rate unit for calculations.
     *
     * @param rateUnit Rate unit for calculations
     * @return This RawMetric instance
     */
    public RawMetric setRateUnit(double rateUnit) {
        this.rateUnit = rateUnit;
        return this;
    }

    /**
     * Returns the bucket boundaries for long histograms.
     *
     * @return Bucket boundaries for long histograms
     */
    public List<Long> getLongBucketBoundaries() {
        return longBucketBoundaries;
    }

    /**
     * Sets the bucket boundaries for long histograms.
     *
     * @param longBucketBoundaries Bucket boundaries for long histograms
     */
    public void setLongBucketBoundaries(List<Long> longBucketBoundaries) {
        this.longBucketBoundaries = longBucketBoundaries;
    }

    /**
     * Returns the bucket boundaries for double histograms.
     *
     * @return Bucket boundaries for double histograms
     */
    public List<Double> getDoubleBucketBoundaries() {
        return doubleBucketBoundaries;
    }

    /**
     * Sets the bucket boundaries for double histograms.
     *
     * @param doubleBucketBoundaries Bucket boundaries for double histograms
     */
    public void setDoubleBucketBoundaries(List<Double> doubleBucketBoundaries) {
        this.doubleBucketBoundaries = doubleBucketBoundaries;
    }

    /**
     * Returns the long histogram for recording long values.
     *
     * @return Long histogram for recording long values
     */
    public LongHistogram getLongHistogram() {
        return longHistogram;
    }

    /**
     * Sets the long histogram for recording long values.
     *
     * @param longHistogram Long histogram for recording long values
     */
    public void setLongHistogram(LongHistogram longHistogram) {
        this.longHistogram = longHistogram;
    }

    /**
     * Returns the double histogram for recording double values.
     *
     * @return Double histogram for recording double values
     */
    public DoubleHistogram getDoubleHistogram() {
        return doubleHistogram;
    }

    /**
     * Sets the double histogram for recording double values.
     *
     * @param doubleHistogram Double histogram for recording double values
     */
    public void setDoubleHistogram(DoubleHistogram doubleHistogram) {
        this.doubleHistogram = doubleHistogram;
    }

    /**
     * Purges outdated data points from the metric.
     */
    public void purgeOutdatedDps() {
        long tm = System.currentTimeMillis();
        dps.entrySet().removeIf(entry -> tm - entry.getValue().getCurrentTime() > outdatedTime);
    }

    /**
     * Returns the data points associated with the metric.
     *
     * @return Data points associated with the metric
     */
    public Map<String, DataPoint> getDataPoints() {
        return dps;
    }

    /**
     * Returns the time after which data points are considered outdated.
     *
     * @return Time after which data points are considered outdated
     */
    public long getOutdatedTime() {
        return outdatedTime;
    }

    /**
     * Sets the time after which data points are considered outdated.
     *
     * @param outdatedTime Time after which data points are considered outdated
     * @return This RawMetric instance
     */
    public RawMetric setOutdatedTime(long outdatedTime) {
        this.outdatedTime = outdatedTime;
        return this;
    }

    /**
     * Sets a value for the metric.
     *
     * @param value Value to be set for the metric
     * @return This RawMetric instance
     */
    public RawMetric setValue(Number value) {
        if (longHistogram != null) {
            longHistogram.record(value.longValue());
        } else if (doubleHistogram != null) {
            doubleHistogram.record(value.doubleValue());
        } else {
            getDataPoint(null).setValue(value);
        }
        return this;
    }

    /**
     * Sets a value for the metric with associated attributes.
     *
     * @param value      Value to be set for the metric
     * @param attributes Attributes associated with the value
     * @return This RawMetric instance
     */
    public RawMetric setValue(Number value, Map<String, Object> attributes) {
        if (longHistogram != null) {
            longHistogram.record(value.longValue(), DcUtil.convertMapToAttributes(attributes));
        } else if (doubleHistogram != null) {
            doubleHistogram.record(value.doubleValue(), DcUtil.convertMapToAttributes(attributes));
        } else {
            getDataPoint(null).setValue(value, attributes);
        }
        return this;
    }

    /**
     * Sets a value for the metric based on a MetricQueryResult.
     *
     * @param result MetricQueryResult containing the value and attributes
     * @return This RawMetric instance
     */
    public RawMetric setValue(MetricQueryResult result) {
        if (result != null) {
            if (longHistogram != null) {
                longHistogram.record(result.getValue().longValue(), DcUtil.convertMapToAttributes(result.getAttributes()));
            } else if (doubleHistogram != null) {
                doubleHistogram.record(result.getValue().doubleValue(), DcUtil.convertMapToAttributes(result.getAttributes()));
            } else {
                getDataPoint(result.getKey()).setValue(result);
            }
        }
        return this;
    }

    /**
     * Sets values for the metric based on a list of MetricQueryResults.
     *
     * @param results List of MetricQueryResults containing the values and attributes
     * @return This RawMetric instance
     */
    public RawMetric setValue(List<MetricQueryResult> results) {
        if (results != null) {
            for (MetricQueryResult result : results) {
                if (longHistogram != null) {
                    longHistogram.record(result.getValue().longValue(), DcUtil.convertMapToAttributes(result.getAttributes()));
                } else if (doubleHistogram != null) {
                    doubleHistogram.record(result.getValue().doubleValue(), DcUtil.convertMapToAttributes(result.getAttributes()));
                } else {
                    getDataPoint(result.getKey()).setValue(result);
                }
            }
        }
        return this;
    }

    /**
     * Checks if data points should be cleared.
     *
     * @return True if data points should be cleared, false otherwise
     */
    public boolean isClearDps() {
        return clearDps;
    }

    /**
     * Sets the flag to indicate whether to clear data points.
     *
     * @param clearDps Flag to indicate whether to clear data points
     * @return This RawMetric instance
     */
    public RawMetric setClearDps(boolean clearDps) {
        this.clearDps = clearDps;
        return this;
    }

    /**
     * Returns a data point for the specified key.
     *
     * @param key Key for the data point
     * @return Data point for the specified key
     */
    public DataPoint getDataPoint(String key) {
        if (key == null) {
            key = DcUtil.DEFAULT;
        }
        return dps.computeIfAbsent(key, k -> new DataPoint(this, k));
    }

    /**
     * Represents a data point within a RawMetric.
     */
    public static class DataPoint {
        private final RawMetric rawMetric; // RawMetric associated with the data point
        private final String key; // Key for the data point

        private Number currentValue, previousValue; // Current and previous values of the data point
        private long currentTime, previousTime; // Current and previous times of the data point
        private final Map<String, Object> attributes = new ConcurrentHashMap<>(); // Attributes associated with the data point

        /**
         * Constructs a new DataPoint with specified parameters.
         *
         * @param rawMetric RawMetric associated with the data point
         * @param key       Key for the data point
         */
        public DataPoint(RawMetric rawMetric, String key) {
            this.rawMetric = rawMetric;
            this.key = key;

            this.currentValue = null;
            this.previousValue = null;
            this.currentTime = 0;
            this.previousTime = 0;
        }

        /**
         * Returns the RawMetric associated with the data point.
         *
         * @return RawMetric associated with the data point
         */
        public RawMetric getRawMetric() {
            return rawMetric;
        }

        /**
         * Returns the key for the data point.
         *
         * @return Key for the data point
         */
        public String getKey() {
            return key;
        }

        /**
         * Returns the value of the data point based on the calculation mode.
         *
         * @return Value of the data point
         */
        public Number getValue() {
            if (rawMetric.getCalculationMode() == MetricCalculationMode.DIRECT) {
                return currentValue;
            }
            if (currentValue == null || previousValue == null || currentTime <= previousTime) {
                return null;
            }

            long longDelta = 0;
            double doubleDelta = 0;
            if (rawMetric.isInteger()) {
                longDelta = currentValue.longValue() - previousValue.longValue();
            } else {
                doubleDelta = currentValue.doubleValue() - previousValue.doubleValue();
            }

            long timeDelta = currentTime - previousTime;
            if (rawMetric.isInteger()) {
                return rawMetric.getRateUnit() * longDelta / timeDelta;
            } else {
                return rawMetric.getRateUnit() * doubleDelta / timeDelta;
            }
        }

        /**
         * Returns the double value of the data point.
         *
         * @return Double value of the data point
         */
        public Double getDoubleValue() {
            Number number = getValue();
            if (number == null) {
                return null;
            }
            return number.doubleValue();
        }

        /**
         * Returns the long value of the data point.
         *
         * @return Long value of the data point
         */
        public Long getLongValue() {
            Number number = getValue();
            if (number == null) {
                return null;
            }
            return number.longValue();
        }

        /**
         * Sets the value of the data point.
         *
         * @param value Value to be set for the data point
         */
        public void setValue(Number value) {
            if (value == null) {
                return;
            }
            this.previousValue = this.currentValue;
            this.previousTime = this.currentTime;
            this.currentValue = value;
            this.currentTime = System.currentTimeMillis();
            logger.info("New metric value: " + rawMetric.getName() + '/' + key + '=' + value);
        }

        /**
         * Sets the value of the data point with associated attributes.
         *
         * @param value      Value to be set for the data point
         * @param attributes Attributes associated with the value
         */
        public void setValue(Number value, Map<String, Object> attributes) {
            if (value == null) {
                return;
            }
            setValue(value);
            this.attributes.clear();
            if (attributes != null) {
                this.attributes.putAll(attributes);
            }
        }

        /**
         * Sets the value of the data point based on a MetricQueryResult.
         *
         * @param result MetricQueryResult containing the value and attributes
         */
        public void setValue(MetricQueryResult result) {
            if (result != null) {
                setValue(result.getValue(), result.getAttributes());
            }
        }

        /**
         * Returns the attributes associated with the data point.
         *
         * @return Attributes associated with the data point
         */
        public Map<String, Object> getAttributes() {
            return attributes;
        }

        /**
         * Returns the current time of the data point.
         *
         * @return Current time of the data point
         */
        public long getCurrentTime() {
            return currentTime;
        }
    }
}