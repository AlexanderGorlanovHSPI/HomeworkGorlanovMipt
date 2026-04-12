package com.example.demo.exception;

import java.util.List;

public class BulkCompleteTaskException extends RuntimeException {
  private final List<Long> missingIds;

  public BulkCompleteTaskException(List<Long> missingIds) {
    super("Tasks not found for ids: " + missingIds);
    this.missingIds = missingIds;
  }

  public List<Long> getMissingIds() {
    return missingIds;
  }
}
