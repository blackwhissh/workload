package com.blackwhissh.workload.dto.request;

import java.time.LocalDate;
import java.util.Optional;

public record EditEmployeeProfileRequest(Optional<String> firstName,
                                         Optional<String> lastName,
                                         Optional<String> email,
                                         Optional<String> phoneNumber,
                                         Optional<String> emergencyContact,
                                         Optional<String> address,
                                         Optional<LocalDate> dob){
}
