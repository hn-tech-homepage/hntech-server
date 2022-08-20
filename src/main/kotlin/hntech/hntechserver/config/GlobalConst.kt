package hntech.hntechserver.config

/**
 * 유효성 검증
 */
const val VALIDATION_ERROR = "ValidationError"
const val MAX_TITLE_LENGTH = 50
const val MAX_CONTENT_LENGTH = 750
const val MAX_COMMENT_LENGTH = 50
const val MAX_TITLE_LENGTH_MSG = "제목 최대 글자수는 (공백 포함) 50자 입니다."
const val MAX_CONTENT_LENGTH_MSG = "내용 최대 글자수는 (공백 포함) 750자 입니다."
const val MAX_COMMENT_LENGTH_MSG = "최대 글자수는 (공백 포함) 50자 입니다."
const val REG_BOOL = "^(true|false)$"
const val REG_BOOL_MSG = "true 또는 false 값만 입력 가능합니다."

// 카테고리
const val MAX_MAIN_CATEGORY_COUNT = 8
const val ARCHIVE = "archive"
const val PRODUCT = "product"
const val UNKNOWN = "unknown"

// 자료실
const val MAX_NOTICE_NUM = 10

// 이미지 구분
const val REPRESENTATIVE_IMAGE = "대표"
const val PRODUCT_IMAGE = "제품"
const val STANDARD_IMAGE = "규격"

// 관리자 인증 관련
const val ADMIN = "admin" // 세션 키 용
const val AUTH_DENIED = "로그인 되어 있지 않음."
const val LOGIN_FAIL = "비밀번호가 일치하지 않습니다."