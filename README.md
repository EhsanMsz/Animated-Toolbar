# Animated Toolbar
[![](https://img.shields.io/maven-central/v/com.ehsanmsz/animated-toolbar?color=blue)](https://search.maven.org/artifact/com.ehsanmsz/animated-toolbar/0.7.1/aar)
[![](https://img.shields.io/github/license/ehsanmsz/animated-toolbar?color=blue)](https://github.com/EhsanMsz/Animated-Toolbar/blob/master/LICENSE.txt)
[![](https://img.shields.io/github/issues/ehsanmsz/animated-toolbar?color=blue)](https://github.com/EhsanMsz/Animated-Toolbar/issues)


Simple toolbar animation for Android

# Download

Gradle:
```groovy
dependencies {
    implementation 'com.ehsanmsz:animated-toolbar:0.7.1'
}
```

Maven:
```xml
<dependency>
  <groupId>com.ehsanmsz</groupId>
  <artifactId>animated-toolbar</artifactId>
  <version>0.7.1</version>
  <type>aar</type>
</dependency>
```
  
# Example
XML:

```xml
<com.ehsanmsz.animatedtoolbar.AnimatedToolbar
    android:id="@+id/toolbar"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:gravity="top"
    android:elevation="8dp"
    android:layout_alignParentTop="true">

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textColor="@android:color/white"
            android:text="@string/app_name"
            android:gravity="center"/>

</com.ehsanmsz.animatedtoolbar.AnimatedToolbar>
 ```

### Java:
```java
AnimatedToolbar animatedToolbar = findViewById(R.id.toolbar);

Rect rect = new Rect();
rect.setAngle(30.0);
rect.setGravity(Shape.GRAVITY_RIGHT);

GradientColor color = new GradientColor(new int[]{startColor, endColor}, 90.0, null);

animatedToolbar.setDuration(400L);
animatedToolbar.setInterpolator(new DecelerateInterpolator());
animatedToolbar.setShape(rect);
animatedToolbar.setGradientColor(color);
        
setSupportActionBar(animatedToolbar);
```

### Kotlin:
```kotlin
val animatedToolbar = findViewById<AnimatedToolbar>(R.id.toolbar)
        
val rect = Rect().apply {
    angle = 30.0
    gravity = Shape.GRAVITY_RIGHT
}

val color = GradientColor(intArrayOf(startColor, endColor)), 90.0)

animatedToolbar.apply {
    duration = 400L
    interpolator = DecelerateInterpolator()
    shape = rect
    gradientColor = color
}
setSupportActionBar(animatedToolbar)
```

  
# License
```
Copyright 2020 Ehsan msz

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
