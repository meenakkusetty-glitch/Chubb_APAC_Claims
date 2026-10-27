package com.chubb.claims.workload;

import jakarta.persistence.*;

@Entity
@Table(name = "workload_records")
public class WorkloadRecord {
    @Id
    private String claimId;
    private String staffName;
    private String status;

    protected WorkloadRecord() {
    }

    public WorkloadRecord(String id, String staff, String status) {
        claimId = id;
        staffName = staff;
        this.status = status;
    }

    public String getClaimId() {
        return claimId;
    }

    public String getStaffName() {
        return staffName;
    }

    public String getStatus() {
        return status;
    }

    public void update(String staff, String s) {
        if (staff != null && !staff.isBlank()) staffName = staff;
        status = s;
    }
}
