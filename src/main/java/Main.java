import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        Scanner sc = new Scanner(System.in);

        System.out.println("🧠 Task CLI started. Commands: add, update, delete, list [status]");
        while (true) {
            System.out.print("task-cli> ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) break;
            if (input.isEmpty()) continue;

            String[] parts = input.trim().split("\\s+");
            String command = parts[0];

            switch (command.toLowerCase()) {
                case "add":
                    if (parts.length < 3) {
                        System.out.println("Usage: add <status-code> <description>");
                        continue;
                    }

                    int code = Integer.parseInt(parts[1]);
                    TaskStatus status = TaskStatus.fromCode(code); // your helper method

                    // Join remaining parts as description
                    StringBuilder descBuilder = new StringBuilder();
                    for (int i = 2; i < parts.length; i++) {
                        descBuilder.append(parts[i]).append(" ");
                    }
                    String description = descBuilder.toString().trim();

                    manager.addTask(status, description);
                    break;

                case "update":
                    if (parts.length == 3) {
                        String[] subParts = parts[2].split(" ", 2);
                        int id = Integer.parseInt(parts[1]);
                        TaskStatus newStatus = TaskStatus.valueOf(subParts[0].toUpperCase());
                        String newDesc = subParts.length == 2 ? subParts[1] : "";
                        manager.updateTask(id, newStatus, newDesc);
                    } else {
                        System.out.println("❌ Usage: update <id> <status> <description>");
                    }
                    break;

                case "delete":
                    if (parts.length == 2) {
                        manager.deleteTask(Integer.parseInt(parts[1]));
                    } else {
                        System.out.println("❌ Usage: delete <id>");
                    }
                    break;

                case "list":
                    if(parts.length == 1) {
                        manager.listAll();
                    } else {
                        String type = parts[1].toLowerCase();
                        switch (type) {
                            case "done": manager.listByStatus(TaskStatus.DONE); break;
                            case "todo": manager.listByStatus(TaskStatus.TODO); break;
                            case "in-progress": manager.listByStatus(TaskStatus.IN_PROGRESS); break;
                            default: System.out.println("Unknown list type: " + type);
                        }
                    }
                    break;

                case "mark-in-progress":
                    if (parts.length != 2) {
                        System.out.println("Usage: mark-in-progress <taskId>");
                        break;
                    }
                    try {
                        int taskId = Integer.parseInt(parts[1]);
                        manager.markInProgress(taskId);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid task ID.");
                    }
                    break;
                case "mark-done":
                    if (parts.length != 2) {
                        System.out.println("Usage: mark-done <taskId>");
                        break;
                    }
                    try {
                        int taskId = Integer.parseInt(parts[1]);
                        manager.markDone(taskId);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid task ID.");
                    }
                    break;

                default:
                    System.out.println("❓ Unknown command");
            }
        }
    }
}
