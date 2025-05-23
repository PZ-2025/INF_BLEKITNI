/*
 * Classname: OrderRepositoryTest
 * Version information: 1.0
 * Date: 2025-05-23
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "Wnioski_o_nieobecnosc")
@Access(AccessType.FIELD)
public class AbsenceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "Typ_wniosku", length = 100, nullable = false)
    private String requestType;

    @Column(name = "Data_rozpoczecia", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "Data_zakonczenia", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "Opis", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private RequestStatus status = RequestStatus.PENDING; // domyślna wartość

    @ManyToOne
    @JoinColumn(name = "Id_pracownika", nullable = false)
    private Employee employee;

    // Enum odpowiadający możliwym statusom w tabeli SQL
    public enum RequestStatus {
        PENDING("Oczekuje"),
        NOTACCEPTED("Nie przyjęty"),
        ACCEPTED("Przyjęty");

        private final String value;

        RequestStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public AbsenceRequest() {}

    public AbsenceRequest(String requestType, Date startDate, Date endDate,
                          String description, Employee employee, RequestStatus status) {
        this.requestType = requestType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.employee = employee;
        this.status = status;
    }

    // === Gettery i settery ===
    public int getId() {
        return id;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return String.format(
                "AbsenceRequest{id=%d, type='%s', from=%s, to=%s, description='%s', status='%s', employee=%s %s}",
                id, requestType,
                startDate != null ? dateFormat.format(startDate) : "null",
                endDate != null ? dateFormat.format(endDate) : "null",
                description,
                status,
                employee != null ? employee.getName() : "null",
                employee != null ? employee.getSurname() : ""
        );
    }
}