import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.1"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	kotlin("plugin.jpa") version "1.6.21"
	kotlin("plugin.allopen") version "1.6.21"
	kotlin("plugin.noarg") version "1.6.21"
	kotlin("kapt") version "1.7.10"

	// ktlint (컨벤션 검사)
	id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
	id("org.jlleitschuh.gradle.ktlint-idea") version "10.0.0"
}

group = "hntech"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	runtimeOnly("mysql:mysql-connector-java")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// mail
	implementation("org.springframework.boot:spring-boot-starter-mail")

	// Apache Commons
	implementation("org.apache.commons:commons-configuration2:2.8.0")
	implementation("commons-beanutils:commons-beanutils:1.9.4")

	// mockK
	testImplementation("io.mockk:mockk:1.12.0")

	// kotest
	testImplementation("io.kotest:kotest-runner-junit5:5.4.1")
	testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2")

	// swagger
	implementation("io.springfox:springfox-boot-starter:3.0.0")
	implementation("io.springfox:springfox-swagger-ui:3.0.0")

	// gson
	implementation("com.google.code.gson:gson:2.9.0")

	// AOP
	implementation("org.springframework.boot:spring-boot-starter-aop")

	// Querydsl-jpa
	val querydslVersion = "5.0.0"
	implementation("com.querydsl:querydsl-jpa:$querydslVersion")
	apply(plugin = "kotlin-kapt")
	kapt("com.querydsl:querydsl-apt:$querydslVersion:jpa")
	kapt("org.springframework.boot:spring-boot-configuration-processor")
}

// Querydsl 추가 설정
sourceSets["main"].withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
	kotlin.srcDir("$buildDir/generated/source/kapt/main")
}

allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.MappedSuperclass")
	annotation("javax.persistence.Embeddable")
}

noArg {
	annotation("javax.persistence.Entity")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()
}

// jar 파일 이름 변경
tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
	archiveFileName.set("hnt.jar")
}