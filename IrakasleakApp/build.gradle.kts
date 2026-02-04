// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    //DaggerHilt
    id("com.google.dagger.hilt.android") version "2.48" apply false

    //SafeArgs
    id("androidx.navigation.safeargs.kotlin") version "2.7.1" apply false

  }