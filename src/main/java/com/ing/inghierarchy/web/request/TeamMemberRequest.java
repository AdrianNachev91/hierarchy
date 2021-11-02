package com.ing.inghierarchy.web.request;

import com.ing.inghierarchy.domain.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
@Accessors(chain = true)
@Data
public class TeamMemberRequest extends Employee { }
