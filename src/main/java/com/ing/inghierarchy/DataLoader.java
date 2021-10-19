package com.ing.inghierarchy;

import com.ing.inghierarchy.domain.Role;
import com.ing.inghierarchy.domain.TeamMember;
import com.ing.inghierarchy.repositories.RoleRepository;
import com.ing.inghierarchy.repositories.TeamMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class DataLoader implements ApplicationRunner {

	private RoleRepository roleRepo;
	private TeamMemberRepository memberRepo;
	
	@Autowired
    public DataLoader(RoleRepository roleRepo, TeamMemberRepository memberRepo) {
		this.roleRepo = roleRepo;
        this.memberRepo = memberRepo;
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
		this.roleRepo.save(roleDev);
		this.roleRepo.save(roleOps);
		this.roleRepo.save(roleCJE);
		this.roleRepo.save(rolePO);

		TeamMember member1 = TeamMember.builder()
				.corporateId("100001")
				.name("Member One")
				.roleId(rolePO.getId())
				.build();
		TeamMember member2 = TeamMember.builder()
				.corporateId("100002")
				.name("Member Two")
				.roleId(roleDev.getId())
				.build();
		this.memberRepo.save(member1);
		this.memberRepo.save(member2);
	}

}
