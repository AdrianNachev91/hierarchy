package com.ing.inghierarchy.web;

import com.ing.inghierarchy.domain.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
@Accessors(chain = true)
@Data
public class TeamMemberRequest extends Person { }
