package org.influxdb.querybuilder.clauses;

import java.util.List;

import static org.influxdb.querybuilder.Appender.joinAndAppendNames;

public class MultipleFromClause extends FromClause {

  private final List<String> tables;

  public MultipleFromClause(final List<String> tables) {
    if (tables == null || tables.size() == 0) {
      throw new IllegalArgumentException("Tables names should be specified");
    }
    this.tables = tables;
  }

  @Override
  public void appendTo(final StringBuilder stringBuilder) {
    joinAndAppendNames(stringBuilder, tables);
  }
}
