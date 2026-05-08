package com.alerts;

public class PriorityAlertDecorator extends AlertDecorator {
    private final String priority;

    public PriorityAlertDecorator(Alert wrappedAlert, String priority) {
        super(wrappedAlert);
        if (priority == null || priority.isBlank()) {
            throw new IllegalArgumentException("priority must not be blank");
        }
        this.priority = priority.trim().toUpperCase();
    }

    public String getPriority() {
        return priority;
    }

    @Override
    public String getType() {
        return super.getType() + " | priority=" + priority;
    }
}