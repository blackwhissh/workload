package com.blackwhissh.workload.dto.response;

import java.time.LocalDate;

public record EmployeeProfileResponse(String firstName, String lastName, LocalDate dob,
                                      String phoneNumber, String address, String emergencyContact,
                                      String position, String pid, String workId, String email,
                                      boolean isActive) {
}
