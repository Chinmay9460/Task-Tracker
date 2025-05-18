public enum TaskStatus {
    TODO,
    DONE,
    IN_PROGRESS;

    public static TaskStatus fromCode(int code) {
        return switch (code) {
            case 0 -> TaskStatus.TODO;
            case 1 -> TaskStatus.IN_PROGRESS;
            case 2 -> TaskStatus.DONE;
            default -> throw new IllegalArgumentException("Invalid status code: " + code);
        };
    }
}
