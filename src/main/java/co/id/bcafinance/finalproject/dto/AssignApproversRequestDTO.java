package co.id.bcafinance.finalproject.dto;
/*
IntelliJ IDEA 2023.3.3 (Ultimate Edition)
Build #IU-233.14015.106, built on January 25, 2024
@Author Cornelius
Java Developer
Created on 5/30/2024 15:29 PM
@Last Modified 5/30/2024 15:29 PM
Version 1.0
*/

import java.util.List;

public class AssignApproversRequestDTO {
    private List<ApproverDTO> approverIds;

    public List<ApproverDTO> getApproverIds() {
        return approverIds;
    }

    public void setApproverIds(List<ApproverDTO> approverIds) {
        this.approverIds = approverIds;
    }
}

