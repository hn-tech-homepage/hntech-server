package hntech.hntechserver.utils.config

/**
 * 경로
 */
// Windows
const val DOCS_SAVE_PATH_WINDOW = "C:\\dev\\files\\documents\\"
const val IMAGE_SAVE_PATH_WINDOW = "C:\\dev\\files\\images\\"
const val ADMIN_IMAGE_SAVE_PATH_WINDOW = "C:\\dev\\files\\admin\\"
const val FILE_SAVE_PATH_WINDOW_TEST = "C:\\dev\\files\\"
const val YAML_FILE_PATH_WINDOW = "C:\\application-mail.yml"
const val DUMMY_FILE_PATH_WINDOW = "C:\\dev\\files\\dummy.jpg"

// Linux
const val FILE_SAVE_PATH_LINUX = "/home/ubuntu/spring/files"
const val YAML_FILE_PATH_LINUX = "/home/ubuntu/spring/application-mail.yml"


const val VALIDATION_ERROR = "validation_error"

// 카테고리
const val MAX_MAIN_CATEGORY_COUNT = 8
const val ARCHIVE = "archive"
const val PRODUCT = "product"

// 제품

// 이미지 구분
const val REPRESENTATIVE_IMAGE = "대표"
const val PRODUCT_IMAGE = "제품"
const val STANDARD_IMAGE = "규격"

// 관리자 인증 관련
const val ADMIN = "admin" // 세션 키 용
const val AUTH_DENIED = "로그인 되어 있지 않음."
const val LOGIN_FAIL = "비밀번호가 일치하지 않습니다."