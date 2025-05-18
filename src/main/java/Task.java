import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Task {

    private final LocalDate date;
    //id to identify the task id
    private final int task_id;
    private TaskStatus status;
    private String desc;


    public Task(int task_id, String desc, TaskStatus status, LocalDate date) {
        this.task_id = task_id;
        this.status = status;
        this.desc = desc;
        this.date = date;
    }


    public String toJson() {
        return String.format(
                "{ \"id\": %d, \"description\": \"%s\", \"status\": \"%s\", \"createdDate\": \"%s\" }",
                task_id, escape(desc), status.name(), date);
    }

    public static Task fromJson(String jsonLine) {
        Map<String, String> map = new HashMap<>();
        jsonLine = jsonLine.replaceAll("[\\{\\}\"]", ""); // remove { } and quotes

        for (String pair : jsonLine.split(",")) {
            String[] kv = pair.trim().split(":", 2);
            if (kv.length == 2) {
                map.put(kv[0].trim(), kv[1].trim());
            }
        }

        int id = Integer.parseInt(map.get("id"));
        String description = map.get("description");
        LocalDate createdDate = LocalDate.parse(map.get("createdDate"));

        TaskStatus status;
        try {
            status = TaskStatus.valueOf(map.get("status"));
        } catch (IllegalArgumentException e) {
            // fallback or default
            status = TaskStatus.TODO;
        }

        return new Task(id, description, status, createdDate);
    }



    private String escape(String s) {
        return s.replace("\"", "\\\""); //basic escaping
    }


    public int getTask_id() {
        return task_id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Task{" +
                "date=" + date +
                ", task_id=" + task_id +
                ", status=" + status +
                ", desc='" + desc + '\'' +
                '}';
    }
}
