package org.influxdb.dto;

import org.influxdb.BuilderException;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;
import org.influxdb.impl.Preconditions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * Representation of a InfluxDB database Point.
 *
 * @author stefan.majer [at] gmail.com
 *
 */
public class Point {
  private String measurement;
  private Map<String, String> tags;
  private Long time;
  private TimeUnit precision = TimeUnit.NANOSECONDS;
  private Map<String, Object> fields;
  private static final int MAX_FRACTION_DIGITS = 340;
  private static final ThreadLocal<NumberFormat> NUMBER_FORMATTER =
          ThreadLocal.withInitial(() -> {
            NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
            numberFormat.setMaximumFractionDigits(MAX_FRACTION_DIGITS);
            numberFormat.setGroupingUsed(false);
            numberFormat.setMinimumFractionDigits(1);
            return numberFormat;
          });

  private static final int DEFAULT_STRING_BUILDER_SIZE = 1024;
  private static final ThreadLocal<StringBuilder> CACHED_STRINGBUILDERS =
          ThreadLocal.withInitial(() -> new StringBuilder(DEFAULT_STRING_BUILDER_SIZE));

  Point() {
  }

  /**
   * Create a new Point Build build to create a new Point in a fluent manner.
   *
   * @param measurement
   *            the name of the measurement.
   * @return the Builder to be able to add further Builder calls.
   */

  public static Builder measurement(final String measurement) {
    return new Builder(measurement);
  }

  /**
   * Create a new Point Build build to create a new Point in a fluent manner from a POJO.
   *
   * @param clazz Class of the POJO
   * @return the Builder instance
   */

  public static Builder measurementByPOJO(final Class<?> clazz) {
    Objects.requireNonNull(clazz, "clazz");
    throwExceptionIfMissingAnnotation(clazz, Measurement.class);
    String measurementName = findMeasurementName(clazz);
    return new Builder(measurementName);
  }

  private static void throwExceptionIfMissingAnnotation(final Class<?> clazz,
      final Class<? extends Annotation> expectedClass) {
    if (!clazz.isAnnotationPresent(expectedClass)) {
      throw new IllegalArgumentException("Class " + clazz.getName() + " is not annotated with @"
          + Measurement.class.getSimpleName());
    }
  }

  /**
   * Builder for a new Point.
   *
   * @author stefan.majer [at] gmail.com
   *
   */
  public static final class Builder {
    private final String measurement;
    private final Map<String, String> tags = new TreeMap<>();
    private Long time;
    private TimeUnit precision;
    private final Map<String, Object> fields = new TreeMap<>();

    /**
     * @param measurement
     */
    Builder(final String measurement) {
      this.measurement = measurement;
    }

    /**
     * Add a tag to this point.
     *
     * @param tagName
     *            the tag name
     * @param value
     *            the tag value
     * @return the Builder instance.
     */
    public Builder tag(final String tagName, final String value) {
      Objects.requireNonNull(tagName, "tagName");
      Objects.requireNonNull(value, "value");
      if (!tagName.isEmpty() && !value.isEmpty()) {
        tags.put(tagName, value);
      }
      return this;
    }

    /**
     * Add a Map of tags to add to this point.
     *
     * @param tagsToAdd
     *            the Map of tags to add
     * @return the Builder instance.
     */
    public Builder tag(final Map<String, String> tagsToAdd) {
      for (Entry<String, String> tag : tagsToAdd.entrySet()) {
        tag(tag.getKey(), tag.getValue());
      }
      return this;
    }

    /**
     * Add a field to this point.
     *
     * @param field
     *            the field name
     * @param value
     *            the value of this field
     * @return the Builder instance.
     */
    @SuppressWarnings("checkstyle:finalparameters")
    @Deprecated
    public Builder field(final String field, Object value) {
      if (value instanceof Number) {
        if (value instanceof Byte) {
          value = ((Byte) value).doubleValue();
        } else if (value instanceof Short) {
          value = ((Short) value).doubleValue();
        } else if (value instanceof Integer) {
          value = ((Integer) value).doubleValue();
        } else if (value instanceof Long) {
          value = ((Long) value).doubleValue();
        } else if (value instanceof BigInteger) {
          value = ((BigInteger) value).doubleValue();
        }
      }
      fields.put(field, value);
      return this;
    }

    public Builder addField(final String field, final boolean value) {
      fields.put(field, value);
      return this;
    }

    public Builder addField(final String field, final long value) {
      fields.put(field, value);
      return this;
    }

    public Builder addField(final String field, final double value) {
      fields.put(field, value);
      return this;
    }

    public Builder addField(final String field, final Number value) {
      fields.put(field, value);
      return this;
    }

    public Builder addField(final String field, final String value) {
      Objects.requireNonNull(value, "value");

      fields.put(field, value);
      return this;
    }

    /**
     * Add a Map of fields to this point.
     *
     * @param fieldsToAdd
     *            the fields to add
     * @return the Builder instance.
     */
    public Builder fields(final Map<String, Object> fieldsToAdd) {
      this.fields.putAll(fieldsToAdd);
      return this;
    }

    /**
     * Add a time to this point.
     *
     * @param timeToSet the time for this point
     * @param precisionToSet the TimeUnit
     * @return the Builder instance.
     */
    public Builder time(final long timeToSet, final TimeUnit precisionToSet) {
      Objects.requireNonNull(precisionToSet, "precisionToSet");
      this.time = timeToSet;
      this.precision = precisionToSet;
      return this;
    }

    /**
     * Does this builder contain any fields?
     *
     * @return true, if the builder contains any fields, false otherwise.
     */
    public boolean hasFields() {
      return !fields.isEmpty();
    }

    /**
     * Adds field map from object by reflection using {@link Column}
     * annotation.
     *
     * @param pojo POJO Object with annotation {@link Column} on fields
     * @return the Builder instance
     */
    public Builder addFieldsFromPOJO(final Object pojo) {

      Class<? extends Object> clazz = pojo.getClass();

      for (Field field : clazz.getDeclaredFields()) {

        Column column = field.getAnnotation(Column.class);

        if (column == null) {
          continue;
        }

        field.setAccessible(true);
        String fieldName = column.name();
        addFieldByAttribute(pojo, field, column, fieldName);
      }

      if (this.fields.isEmpty()) {
        throw new BuilderException("Class " + pojo.getClass().getName()
            + " has no @" + Column.class.getSimpleName() + " annotation");
      }

      return this;
    }

    private void addFieldByAttribute(final Object pojo, final Field field, final Column column,
        final String fieldName) {
      try {
        Object fieldValue = field.get(pojo);

        TimeColumn tc = field.getAnnotation(TimeColumn.class);
        if (tc != null && Instant.class.isAssignableFrom(field.getType())) {
          Optional.ofNullable((Instant) fieldValue).ifPresent(instant -> {
            TimeUnit timeUint = tc.timeUnit();
            this.time = TimeUnit.MILLISECONDS.convert(instant.toEpochMilli(), timeUint);
            this.precision = timeUint;
          });
          return;
        }

        if (column.tag()) {
          this.tags.put(fieldName, (String) fieldValue);
        } else {
          this.fields.put(fieldName, fieldValue);
        }

      } catch (IllegalArgumentException | IllegalAccessException e) {
        // Can not happen since we use metadata got from the object
        throw new BuilderException(
            "Field " + fieldName + " could not found on class " + pojo.getClass().getSimpleName());
      }
    }

    /**
     * Create a new Point.
     *
     * @return the newly created Point.
     */
    public Point build() {
      Preconditions.checkNonEmptyString(this.measurement, "measurement");
      Preconditions.checkPositiveNumber(this.fields.size(), "fields size");
      Point point = new Point();
      point.setFields(this.fields);
      point.setMeasurement(this.measurement);
      if (this.time != null) {
          point.setTime(this.time);
          point.setPrecision(this.precision);
      }
      point.setTags(this.tags);
      return point;
    }
  }

  /**
   * @param measurement
   *            the measurement to set
   */
  void setMeasurement(final String measurement) {
    this.measurement = measurement;
  }

  /**
   * @param time
   *            the time to set
   */
  void setTime(final Long time) {
    this.time = time;
  }

  /**
   * @param tags
   *            the tags to set
   */
  void setTags(final Map<String, String> tags) {
    this.tags = tags;
  }

  /**
   * @return the tags
   */
  Map<String, String> getTags() {
    return this.tags;
  }

  /**
   * @param precision
   *            the precision to set
   */
  void setPrecision(final TimeUnit precision) {
    this.precision = precision;
  }

  /**
   * @return the fields
   */
  Map<String, Object> getFields() {
    return this.fields;
  }

  /**
   * @param fields
   *            the fields to set
   */
  void setFields(final Map<String, Object> fields) {
    this.fields = fields;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Point point = (Point) o;
    return Objects.equals(measurement, point.measurement)
            && Objects.equals(tags, point.tags)
            && Objects.equals(time, point.time)
            && precision == point.precision
            && Objects.equals(fields, point.fields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(measurement, tags, time, precision, fields);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Point [name=");
    builder.append(this.measurement);
    if (this.time != null) {
      builder.append(", time=");
      builder.append(this.time);
    }
    builder.append(", tags=");
    builder.append(this.tags);
    if (this.precision != null) {
      builder.append(", precision=");
      builder.append(this.precision);
    }
    builder.append(", fields=");
    builder.append(this.fields);
    builder.append("]");
    return builder.toString();
  }

  /**
   * calculate the lineprotocol entry for a single Point.
   *
   * Documentation is WIP : https://github.com/influxdb/influxdb/pull/2997
   *
   * https://github.com/influxdb/influxdb/blob/master/tsdb/README.md
   *
   * @return the String without newLine.
   */
  public String lineProtocol() {
    return lineProtocol(null);
  }

  /**
   * Calculate the lineprotocol entry for a single point, using a specific {@link TimeUnit} for the timestamp.
   * @param precision the time precision unit for this point
   * @return the String without newLine
   */
  public String lineProtocol(final TimeUnit precision) {

    // setLength(0) is used for reusing cached StringBuilder instance per thread
    // it reduces GC activity and performs better then new StringBuilder()
    StringBuilder sb = CACHED_STRINGBUILDERS.get();
    sb.setLength(0);

    escapeKey(sb, measurement);
    concatenatedTags(sb);
    concatenatedFields(sb);
    formatedTime(sb, precision);

    return sb.toString();
  }

  private void concatenatedTags(final StringBuilder sb) {
    for (Entry<String, String> tag : this.tags.entrySet()) {
      sb.append(',');
      escapeKey(sb, tag.getKey());
      sb.append('=');
      escapeKey(sb, tag.getValue());
    }
    sb.append(' ');
  }

  private void concatenatedFields(final StringBuilder sb) {
    for (Entry<String, Object> field : this.fields.entrySet()) {
      Object value = field.getValue();
      if (value == null) {
        continue;
      }
      escapeKey(sb, field.getKey());
      sb.append('=');
      if (value instanceof Number) {
        if (value instanceof Double || value instanceof Float || value instanceof BigDecimal) {
          sb.append(NUMBER_FORMATTER.get().format(value));
        } else {
          sb.append(value).append('i');
        }
      } else if (value instanceof String) {
        String stringValue = (String) value;
        sb.append('"');
        escapeField(sb, stringValue);
        sb.append('"');
      } else {
        sb.append(value);
      }

      sb.append(',');
    }

    // efficiently chop off the trailing comma
    int lengthMinusOne = sb.length() - 1;
    if (sb.charAt(lengthMinusOne) == ',') {
      sb.setLength(lengthMinusOne);
    }
  }

  static void escapeKey(final StringBuilder sb, final String key) {
    for (int i = 0; i < key.length(); i++) {
      switch (key.charAt(i)) {
        case ' ':
        case ',':
        case '=':
          sb.append('\\');
        default:
          sb.append(key.charAt(i));
      }
    }
  }

  static void escapeField(final StringBuilder sb, final String field) {
    for (int i = 0; i < field.length(); i++) {
      switch (field.charAt(i)) {
        case '\\':
        case '\"':
          sb.append('\\');
        default:
          sb.append(field.charAt(i));
      }
    }
  }

  private void formatedTime(final StringBuilder sb, final TimeUnit precision) {
    if (this.time == null) {
      return;
    }
    if (precision == null) {
      sb.append(" ").append(TimeUnit.NANOSECONDS.convert(this.time, this.precision));
      return;
    }
    sb.append(" ").append(precision.convert(this.time, this.precision));
  }

  private static String findMeasurementName(final Class<?> clazz) {
    return clazz.getAnnotation(Measurement.class).name();
  }
}
