package org.example.hansabal.domain.users.entity;

public enum UserStatus {
    // 정상적으로 활동 중인 유저
    ACTIVE,

    // 장기간 미접속 등으로 휴면 상태
    DORMANT,

//    // 사용자가 자진 탈퇴한 상태 (회원 정보는 일정 기간 보존)
//    WITHDRAWN,

//    //	부정 행위 등으로 관리자에 의해 정지된 계정
//    BANNED,

//    // 비밀번호 연속 실패, OTP 인증 실패 등으로 일시적으로 잠긴 계정
//    LOCKED
}

