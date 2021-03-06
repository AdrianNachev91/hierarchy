package com.hierarchy.web.request;

import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Data
public class TeamRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String managedBy;
    @NotBlank
    private String leadId;
    @NotBlank
    private String teamType;
}
