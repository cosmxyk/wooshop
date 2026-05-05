package com.wooshop.member.service;

import com.wooshop.common.exception.DuplicateEmailException;
import com.wooshop.member.auth.JwtProvider;
import com.wooshop.member.domain.Member;
import com.wooshop.member.domain.MemberRole;
import com.wooshop.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public Long register(String email, String password, String name) {
        // 1. 이메일 중복 체크
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException(email);
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 3. 회원 엔티티 생성
        Member member = Member.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .role(MemberRole.USER)
                .build();

        // 4. 저장 후 ID 반환
        return memberRepository.save(member).getMemberId();
    }

    public String login(String email, String password) {
        // 1. 이메일로 회원 조회
        //    없으면 예외 발생
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        // 2. 비밀번호 검증
        //    passwordEncoder.matches() 사용
        //    불일치 시 예외 발생
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 3. JWT 토큰 발급 후 반환
        return jwtProvider.generateToken(email, member.getRole().name());
    }
}
