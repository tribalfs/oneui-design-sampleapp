## OneUI6 Design Lib

This design lib is consists of custom components intended to complement and integrate with both [SESL6 Android Jetpack Modules](https://github.com/tribalfs/sesl-androidx?tab=readme-ov-file#sesloneui-android-jetpack-unofficial)
and [SESL6 Material Components for Android](https://github.com/tribalfs/sesl-material-components-android?tab=readme-ov-file#sesloneui-material-components-for-android-unofficial).

## Usage
- Add the needed [SESL6 Android Jetpack Modules](https://github.com/tribalfs/sesl-androidx?tab=readme-ov-file#sesloneui-android-jetpack-unofficial)
  and [SESL6 Material Components for Android](https://github.com/tribalfs/sesl-material-components-android?tab=readme-ov-file#sesloneui-material-components-for-android-unofficial)
  dependencies to your project following their usage guide. Then add the following dependency next:

```
repositories {
  //other remote repositories
  
   maven {
      url = uri("https://maven.pkg.github.com/tribalfs/oneui-design")
      credentials {
          username = "<gh_username>"
          password = "<gh_access_token>"
      }
   } 
}
```

```
dependencies {
  //sesl and other dependencies
  
  implementation("io.github.tribalfs:oneui-design:0.3.7+oneui6")
}
```

- Add the the following to your app's AndroidManifest file:
```xml
<application
        ...
        android:theme="@style/OneUITheme">

        <!-- This enables your app to apply the OneUI device's color pallete.
         Note: android:value corresponds to the filename of the xml file
         that needs to be added to the res/xml folder. Filename can be different.-->
<meta-data
android:name="theming-meta"
android:value="meta_998_sesl_app" />

        </application>
```

- Create theme mata data xml file (e.g. meta_998_sesl_app.xml) with the following content and add it to the app's res/xml folder:

```xml
<?xml version="1.0" encoding="utf-8"?>
<ThemeMetaData FormatVersion="1.3" GuideVersion="1.4">
  <AppMetaData Name="<Any name>" TargetApi="21" TargetPackageName="<app.package.name>" VersionCode="1" VersionName="">
    <Include RefName="SESL" />

  </AppMetaData>
</ThemeMetaData>
```

### Sample apps
- <a href="https://github.com/tribalfs/oneui-design/tree/oneui6/sample-app"> OneUI Sample (widgets showcase)</a> <a href="https://github.com/tribalfs/oneui-design/raw/oneui6/sample-app/release/sample-app-release.apk">Download APK</a>
- <a href="https://github.com/tribalfs/Stargazers">Stargazers (real app usage)</a>

### Credits
- [Google](https://developer.android.com/jetpack) for their Jetpack and Material Components libraries.
- [Samsung](https://www.samsung.com/) for their awesome OneUI Design.
- [Yanndroid](https://github.com/Yanndroid) and [Salvo Giangreco](https://github.com/salvogiangri) who created the [OneUI4 Design library](https://github.com/OneUIProject/oneui-design) where this repository came from. 
