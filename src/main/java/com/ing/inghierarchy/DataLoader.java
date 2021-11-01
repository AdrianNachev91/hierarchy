package com.ing.inghierarchy;

import com.ing.inghierarchy.domain.Role;
import com.ing.inghierarchy.domain.Team;
import com.ing.inghierarchy.domain.TeamMember;
import com.ing.inghierarchy.domain.TeamType;
import com.ing.inghierarchy.repositories.RoleRepository;
import com.ing.inghierarchy.repositories.TeamMemberRepository;
import com.ing.inghierarchy.repositories.TeamRepository;
import com.ing.inghierarchy.repositories.TeamTypeRepository;
import com.ing.inghierarchy.service.PersonService;
import com.ing.inghierarchy.service.RoleService;
import com.ing.inghierarchy.service.TeamService;
import com.ing.inghierarchy.service.TeamTypeService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DataLoader implements ApplicationRunner {

	private RoleRepository roleRepo;
	private TeamMemberRepository memberRepo;
	private TeamTypeRepository teamTypeRepository;
	private TeamRepository teamRepository;
	
	@Autowired
    public DataLoader(RoleRepository roleRepo, TeamMemberRepository memberRepo, TeamTypeRepository teamTypeRepository, TeamRepository teamRepository){
		this.roleRepo = roleRepo;
		this.memberRepo = memberRepo;
		this.teamTypeRepository = teamTypeRepository;
		this.teamRepository = teamRepository;
    }
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		Role roleDev = Role.builder()
				.title("Developer")
				.build();
		Role roleOps = Role.builder()
				.title("Ops")
				.build();
		Role roleCJE = Role.builder()
				.title("Customer Journey Expert")
				.build();
		Role rolePO = Role.builder()
				.title("Product Owner")
				.build();
		roleDev = this.roleRepo.save(roleDev);
		roleOps = this.roleRepo.save(roleOps);
		roleCJE = this.roleRepo.save(roleCJE);
		rolePO = this.roleRepo.save(rolePO);
		
		System.out.println(String.format("Role-ids: %s, %s, %s, %s", roleDev.getId(), roleOps.getId(), roleCJE.getId(), rolePO.getId()));

		TeamMember member1 = TeamMember.builder()
				.corporateId("100001")
				.name("Member One")
				.roleId(roleCJE.getId())
				.build();
		TeamMember member2 = TeamMember.builder()
				.corporateId("100002")
				.name("Member Two")
				.roleId(roleDev.getId())
				.build();
		TeamMember member3 = TeamMember.builder()
				.corporateId("100002")
				.name("PO of team")
				.roleId(rolePO.getId())
				.build();
		member1 = this.memberRepo.save(member1);
		member2 = this.memberRepo.save(member2);
		member3 = this.memberRepo.save(member3);
		
		System.out.println(String.format("Member-ids: %s, %s, %s", member1.getId(), member2.getId(), member3.getId()));
		
		TeamType teamType1 = TeamType.builder()
				.title("devgroup")
				.build();
		teamType1 = this.teamTypeRepository.save(teamType1);
		
		System.out.println(String.format("TeamType-ids: %s", teamType1.getId()));
		
		List<String> teamMemberIds = new ArrayList<String>();
		teamMemberIds.add(member1.getId());
		teamMemberIds.add(member2.getId());
		Team team = Team.builder()
				.managedBy(member3.getId())
				.crew(teamMemberIds)
				.title("hiearchies-team")
				.build();
		team = teamRepository.save(team);
		
		System.out.println(String.format("Team-ids: %s", team.getId()));
	}

}
