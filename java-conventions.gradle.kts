plugins {
	java
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
}

tasks.withType<JavaCompile>.configureEach { 
  options.compilerArgs.add("-parameters")
}
