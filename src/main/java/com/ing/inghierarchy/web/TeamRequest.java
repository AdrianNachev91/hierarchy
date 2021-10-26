package com.ing.inghierarchy.web;

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
    private String teamType;
}
