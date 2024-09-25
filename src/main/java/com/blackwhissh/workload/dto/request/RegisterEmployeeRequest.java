package com.blackwhissh.workload.dto.request;

import java.time.LocalDate;

public record RegisterEmployeeRequest(String shift, String email, String workId, int set, String firstName,
                                      String lastName, String pid, LocalDate dob, String phoneNumber, String address,
                                      String emergencyContact, LocalDate dateOfHire, String position, String password) {
}
