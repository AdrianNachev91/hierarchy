package com.hierarchy.web.request;

import com.hierarchy.domain.Employee;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Data
public class EmployeeRequest {

    @NotBlank
    private String corporateId;
    @NotBlank
    private String name;
    @NotBlank
    private String roleId;

    public Employee toEmployee() {
        return Employee.builder()
                .corporateId(this.corporateId)
                .name(this.name)
                .roleId(this.roleId)
                .build();
    }
}
