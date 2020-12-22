object Versions {
  const val bintray = "1.8.5"
  const val byteBuddy = "1.10.19"
  const val eventBus = "3.2.0"
  const val icu4j = "68.2"
  const val kassava = "2.1.0"
  const val kotlin = "1.4.21"
  const val versions = "0.36.0"
}

object Plugins {
  const val bintray = "com.jfrog.bintray"
  const val versions = "com.github.ben-manes.versions"
}

object Libs {
  const val byteBuddy = "net.bytebuddy:byte-buddy:${Versions.byteBuddy}"
  const val eventBus = "org.greenrobot:eventbus:${Versions.eventBus}"
  const val icu4j = "com.ibm.icu:icu4j:${Versions.icu4j}"
  const val kassava = "au.com.console:kassava:${Versions.kassava}"
  const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
  const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
}
