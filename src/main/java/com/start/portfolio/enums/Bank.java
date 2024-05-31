package com.start.portfolio.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Bank {
	KEB_HANA_BANK("KEB하나은행"),
	SHINHAN_BANK("신한은행"),
	KAKAO_BANK("카카오뱅크"),
	WOORI_BANK("우리은행"),
	KB_KOOKMIN_BANK("KB국민은행"),
	NH_NONGHYUP_BANK("NH농협은행"),
	IBK_BANK("IBK기업은행"),
	K_BANK("케이뱅크");

	private final String koreaName;
}
