package org.example.sys;

public class TaskRecord {
    private String employee;
    private String taskName;
    private String priority;
    private String date;

    public TaskRecord(String employee, String taskName, String priority, String date) {
        this.employee = employee;
        this.taskName = taskName;
        this.priority = priority;
        this.date = date;
    }

    // Gettery i settery
    public String getEmployee() { return employee; }
    public void setEmployee(String employee) { this.employee = employee; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}