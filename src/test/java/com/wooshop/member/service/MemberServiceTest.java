package com.wooshop.member.service;

import com.wooshop.common.exception.DuplicateEmailException;
import com.wooshop.member.auth.JwtProvider;
import com.wooshop.member.domain.Member;
import com.wooshop.member.domain.MemberRole;
import com.wooshop.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    MemberService memberService;

    @Test
    @DisplayName("이메일 중복 시 예외 발생")
    void register_duplicateEmail_throwsException() {
        // given
        given(memberRepository.findByEmail("test@email.com"))
                .willReturn(Optional.of(mock(Member.class)));

        // when
        // then
        assertThatThrownBy(() -> memberService.register("test@email.com", "password", "홍길동"))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    @DisplayName("정상 회원가입 시 회원 ID 반환")
    void register_success_returnsMemberId() {
        // given 1: 이메일 중복 없음
        given(memberRepository.findByEmail("new@email.com"))
                .willReturn(Optional.empty());

        // given 2: 암호화 결과 고정
        given(passwordEncoder.encode("password"))
                .willReturn("encodedPassword");

        // given 3: save() 호출 시 memberId = 1L인 Member 반환
        Member savedMember = Member.builder()
                .memberId(1L)
                .email("new@email.com")
                .password("encodedPassword")
                .name("홍길동")
                .role(MemberRole.USER)
                .build();

        given(memberRepository.save(any(Member.class)))
                .willReturn(savedMember);

        // when
        Long memberId = memberService.register("new@email.com", "password", "홍길동");

        // then
        assertThat(memberId).isEqualTo(1L);
    }

    @Test
    @DisplayName("로그인 성공 시 JWT 토큰 반환")
    void login_success_returnsToken() {
        // given
        given(memberRepository.findByEmail("test@email.com"))
                .willReturn(Optional.of(mock(Member.class)));

        given(passwordEncoder.matches(eq("password"), any())).willReturn(true);

        given(jwtProvider.generateToken("test@email.com")).willReturn("mocked.jwt.token");

        // when
        String token = memberService.login("test@email.com", "password");

        // then
        assertThat(token).isEqualTo("mocked.jwt.token");
    }

    @Test
    @DisplayName("이메일 미존재 시 예외 발생")
    void login_emailNotFound_throwsException() {
        // given
        given(memberRepository.findByEmail("test@email.com"))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.login("test@email.com", "password"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("비밀번호 불일치 시 예외 발생")
    void login_wrongPassword_throwsException() {
        // given
        given(memberRepository.findByEmail("test@email.com"))
                .willReturn(Optional.of(mock(Member.class)));

        // given
        given(passwordEncoder.matches(eq("password"), any())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> memberService.login("test@email.com", "password"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
