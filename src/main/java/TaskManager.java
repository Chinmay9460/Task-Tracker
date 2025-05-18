import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class TaskManager {
    private final Map<LocalDate, Map<Integer, Task>> taskData = new LinkedHashMap<>();
    private final String filename = "tasks.json";

    public TaskManager() {
        loadFromJson();
    }

    public void markInProgress(int id) {
        LocalDate today = LocalDate.now();
        Map<Integer, Task> tasks = taskData.get(today);
        if (tasks != null && tasks.containsKey(id)) {
            Task task = tasks.get(id);
            task.setStatus(TaskStatus.IN_PROGRESS);
            System.out.println("✅ Marked task " + id + " as in progress");
            saveToJson();
        } else {
            System.out.println("❌ Task not found for today.");
        }
    }

    public void markDone(int taskId){
        LocalDate today = LocalDate.now();
        Map<Integer, Task> tasks = taskData.get(today);
        if (tasks != null && tasks.containsKey(taskId)) {
            Task task = tasks.get(taskId);
            task.setStatus(TaskStatus.DONE);
            System.out.println("✅ Marked task " + taskId + " as done");
            saveToJson();
        } else {
            System.out.println("❌ Task not found for today.");
        }
    }

    public void addTask(TaskStatus status, String description) {
        LocalDate today = LocalDate.now();
        taskData.putIfAbsent(today, new LinkedHashMap<>());
        Map<Integer, Task> tasks = taskData.get(today);
        int nextId = tasks.size() + 1;

        Task task = new Task(nextId, description, status, today);
        tasks.put(nextId, task);
        System.out.println("✅ Added task with ID " + nextId);
        saveToJson();
    }

    public void updateTask(int id, TaskStatus status, String description) {
        LocalDate today = LocalDate.now();
        Map<Integer, Task> tasks = taskData.get(today);
        if (tasks != null && tasks.containsKey(id)) {
            Task task = tasks.get(id);
            task.setStatus(status);
            if (description != null && !description.isEmpty()) {
                task.setDesc(description);
            }
            System.out.println("✅ Updated task " + id);
            saveToJson();
        } else {
            System.out.println("❌ Task not found for today.");
        }
    }

    public void deleteTask(int id) {
        LocalDate today = LocalDate.now();
        Map<Integer, Task> tasks = taskData.get(today);
        if (tasks != null && tasks.remove(id) != null) {
            System.out.println("✅ Deleted task " + id);
            saveToJson();
        } else {
            System.out.println("❌ Task not found for today.");
        }
    }

    public void listAll() {
        for (Map.Entry<LocalDate, Map<Integer, Task>> dateEntry : taskData.entrySet()) {
            System.out.println("📅 " + dateEntry.getKey() + ":");
            for (Task task : dateEntry.getValue().values()) {
                System.out.println("  " + task);
            }
        }
    }

    public void listByStatus(TaskStatus status) {
        for (Map.Entry<LocalDate, Map<Integer, Task>> dateEntry : taskData.entrySet()) {
            for (Task task : dateEntry.getValue().values()) {
                if (task.getStatus() == status) {
                    System.out.println("📅 " + dateEntry.getKey() + " -> " + task);
                }
            }
        }
    }

    private void saveToJson() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("{\n");
            int dateCount = 0;
            for (Map.Entry<LocalDate, Map<Integer, Task>> entry : taskData.entrySet()) {
                writer.write("  \"" + entry.getKey() + "\": {\n");
                int taskCount = 0;
                for (Map.Entry<Integer, Task> taskEntry : entry.getValue().entrySet()) {
                    writer.write("    \"" + taskEntry.getKey() + "\": " + taskEntry.getValue().toJson());
                    if (++taskCount < entry.getValue().size()) writer.write(",");
                    writer.write("\n");
                }
                writer.write("  }");
                if (++dateCount < taskData.size()) writer.write(",");
                writer.write("\n");
            }
            writer.write("}\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromJson() {
        Path path = Paths.get(filename);
        if (!Files.exists(path)) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            LocalDate currentDate = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.matches("\"\\d{4}-\\d{2}-\\d{2}\": \\{")) {
                    String dateStr = line.split(":")[0].replace("\"", "");
                    currentDate = LocalDate.parse(dateStr);
                    taskData.put(currentDate, new LinkedHashMap<>());
                } else if (line.matches("\"\\d+\": \\{.*")) {
                    int colon = line.indexOf(":");
                    String idStr = line.substring(1, colon - 1);
                    String json = line.substring(colon + 1).trim();
                    if (json.endsWith(",")) json = json.substring(0, json.length() - 1);
                    Task task = Task.fromJson(json);
                    taskData.get(currentDate).put(Integer.parseInt(idStr), task);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
