# NeumorphismClock
[![](https://jitpack.io/v/otmaneTheDev/NeumorphismClock.svg)](https://jitpack.io/#otmaneTheDev/NeumorphismClock)

### Sample project
View the sample app's source code [here](https://github.com/otmaneTheDev/NeumorphismClock/tree/master/app)

<img src="https://github.com/otmaneTheDev/NeumorphismClock/blob/master/images/neumorphismclock_light.png" width="200" height="400" alt="some_text">

### Setup

Add the JitPack repository to your project level `build.gradle`:
```groovy
allprojects {
 repositories {
    google()
    jcenter()
    maven { url "https://jitpack.io" }
 }
}

```
Add ZodiacEditText to your app `build.gradle`:
```groovy
dependencies {
    implementation 'com.github.otmaneTheDev:NeumorphismClock:<latest-version>'
}
```

## Usage
```xml
    <com.otmanethedev.neumorphismclock.NeumorphismClock
        android:id="@+id/clock"
        android:layout_width="350dp"
        android:layout_height="350dp"
        android:layout_gravity="center" />
```

## Neumorphism clock attributes

```xml
        <attr name="lightBackgroundColor" format="reference|string" />
        <attr name="lightShadowColor" format="reference|string" />
        <attr name="darkShadowColor" format="reference|string" />
        <attr name="borderColor" format="reference|string" />
        <attr name="minHourHandsColor" format="reference|string" />
        <attr name="secondsHandColor" format="reference|string" />
        <attr name="textColor" format="reference|string" />
        <attr name="textSize" format="dimension" />
        <attr name="iconColor" format="reference|string" />
        <attr name="iconSize" format="dimension" />
```
### Contributing

Found a bug? feel free to fix it and send a pull request
