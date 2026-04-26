package com.wooshop.member.controller;

import lombok.Getter;

public class MemberRequest {

    @Getter
    public static class Register {
        private String email;
        private String password;
        private String name;
    }
}
