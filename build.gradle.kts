plugins {
	java
	id("jacoco-report-aggregation")
	id("org.springframework.boot") version "3.4.5"
	id("io.spring.dependency-management") version "1.1.7"
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2024.0.1"

dependencies {
	jacocoAggregation(project(":frontdoor"))
	jacocoAggregation(project(":gatekeeper"))
	jacocoAggregation(project(":hr"))
	jacocoAggregation(project(":ward"))
	jacocoAggregation(project(":rx"))
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

subprojects {
	apply(plugin = "java")
	apply(plugin = "jacoco")

	tasks.withType<JavaCompile>().configureEach { 
		options.compilerArgs.add("-parameters")
	}

	tasks.jacocoTestReport {
		dependsOn(tasks.test)

		reports {
			xml.required = true
			html.required = true
			csv.required = false
			xml.outputLocation = layout.buildDirectory.file("reports/jacoco/test/jacoco.xml")
		}

		classDirectories.setFrom(
			files(
				classDirectories.files.map {
					fileTree(it) {
						setExcludes(listOf(
							"**/entity/**/*",
							"**/dto/**/*"))
					}
				}
			)
		)
	}

	tasks.jacocoTestCoverageVerification {
		dependsOn(tasks.jacocoTestReport)

		violationRules {
			rule {
				classDirectories.setFrom(tasks.jacocoTestReport.get().classDirectories)
				limit {
					minimum = "0.85".toBigDecimal()
				}
			}
		}
	}
}
