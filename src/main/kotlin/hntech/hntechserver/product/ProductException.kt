package hntech.hntechserver.product

const val PRODUCT_NOT_FOUND = "해당 제품이 존재하지 않습니다."
const val DUPLICATE_PRODUCT_NAME = "해당 이름의 제품이 이미 존재합니다."
const val NO_REPRESENTATIVE_IMAGE = "대표 사진을 등록해주세요."
const val NO_PRODUCT_IMAGE = "제품 사진을 등록해주세요."
const val NO_STANDARD_IMAGE = "규격 사진을 등록해주세요."

class ProductException(message: String): RuntimeException(message)