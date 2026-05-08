package com.alerts;

public class RepeatedAlertDecorator extends AlertDecorator {
    private final int repeatCount;

    public RepeatedAlertDecorator(Alert wrappedAlert, int repeatCount) {
        super(wrappedAlert);
        if (repeatCount < 2) {
            throw new IllegalArgumentException("repeatCount must be >= 2");
        }
        this.repeatCount = repeatCount;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    @Override
    public String getCondition() {
        return super.getCondition() + " (repeated x" + repeatCount + ")";
    }

    @Override
    public String getType() {
        return super.getType() + " | repeated";
    }
}