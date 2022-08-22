package hntech.hntechserver.domain.admin

const val ADMIN_PASSWORD_VALID_FAIL = "현재 비밀번호가 틀립니다."
const val ADMIN_PASSWORD_CHECK_FAIL = "비밀번호가 일치하지 않습니다."

class AdminException(message: String) : RuntimeException(message)