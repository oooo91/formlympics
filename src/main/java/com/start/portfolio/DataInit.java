/**
package com.start.portfolio;

import com.start.portfolio.entity.CompanyMember;
import com.start.portfolio.entity.Member;
import com.start.portfolio.repository.CompanyMemberRepository;
import com.start.portfolio.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInit {

	private final MemberRepository memberRepository;
	private final CompanyMemberRepository companyMemberRepository;

	@PostConstruct
	void init() {
		List<Member> memberList = new ArrayList<>();
		List<CompanyMember> companyMemberList = new ArrayList<>();
		IntStream.range(1, 100).forEach(i -> {
			memberList.add(Member.builder().name("개인 회원" + i).loginId("test" + i).build());
			companyMemberList.add(CompanyMember.builder().companyName("기업회원" + i).loginId("company" + i).build());
		});

		memberRepository.saveAll(memberList);
		companyMemberRepository.saveAll(companyMemberList);
	}
}
*/
