package org.example.sys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Klasa reprezentująca złożony klucz główny (EmbeddedId)
 * dla encji TaskEmployee (połączenie taskId + employeeId).
 */
@Embeddable
public class TaskEmployeeId implements Serializable {

    @Column(name = "Id_zadania")
    private int taskId;

    @Column(name = "Id_pracownika")
    private int employeeId;

    // Konstruktor bezparametrowy wymagany przez JPA
    public TaskEmployeeId() {
    }

    // Konstruktor wygodny
    public TaskEmployeeId(int taskId, int employeeId) {
        this.taskId = taskId;
        this.employeeId = employeeId;
    }

    // Gettery i settery

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    // equals i hashCode – ważne, żeby JPA prawidłowo porównywało identyfikatory

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskEmployeeId)) return false;
        TaskEmployeeId that = (TaskEmployeeId) o;
        return taskId == that.taskId &&
                employeeId == that.employeeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, employeeId);
    }
}
