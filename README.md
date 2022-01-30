 ![icon](assets/logo.png)

Omni-Notes
==========

2021年11月2日16:30:58
增加了 文档内搜索关键词的功能。 我觉得挺好的，不过代码写的太烂，可能过不了审核，所以就不提交pr了，就fork吧

2021年10月22日09:19:29
文档：@Override.note
链接：http://note.youdao.com/noteshare?id=2db46adb46c8d0bc161735be5e02ddf0&sub=67AB422369DD4F16AD1F13E79BF8C2FE
import android.app.SearchManager;

文档：2021-10-24 094623.063 29778-29778it....
链接：http://note.youdao.com/noteshare?id=23c52af401df0732404d9b5d74fe2036&sub=A6F88B0E1560442FB37425650359DD90

文档：private static void exportAttachment...
链接：http://note.youdao.com/noteshare?id=e8876406ffc6930d64d976ed5b8013c7&sub=B1533829858E456EBAB2814E5F0C1798

文档：case R.id.menu_search.note
链接：http://note.youdao.com/noteshare?id=70f8d9b1322178562a78fadf4bb7c304&sub=4CD257F85863415B84368CF78AABDF4D


![License](https://img.shields.io/badge/License-GPLv3-red.svg)
[![CI workflow](https://github.com/federicoiosue/Omni-Notes/workflows/CI/badge.svg)](https://github.com/federicoiosue/Omni-Notes/actions?query=workflow%3ACI)
[![CodeQL Workflow](https://github.com/federicoiosue/Omni-Notes/workflows/CodeQL/badge.svg)](https://github.com/federicoiosue/Omni-Notes/actions?query=workflow%3ACodeQL)
[![Sonarcloud Coverage](https://sonarcloud.io/api/project_badges/measure?project=omni-notes&metric=coverage)](https://sonarcloud.io/dashboard?id=omni-notes)
[![Sonarcloud Maintainability](https://sonarcloud.io/api/project_badges/measure?project=omni-notes&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=omni-notes)
[![Crowdin](https://d322cqt584bo4o.cloudfront.net/omni-notes/localized.png)](https://crowdin.com/project/omni-notes)
[![GitHub release](https://img.shields.io/github/release/federicoiosue/omni-notes.svg)](https://github.com/federicoiosue/Omni-Notes/releases/latest)

Note taking <b>open-source</b> application aimed to have both a <b>simple interface</b> but keeping <b>smart</b> behavior.

The project was inspired by the absence of such applications compatible with old phones and old versions of Android. It aims to provide an attractive look and follow the most recent design guidelines of the Google operating system.

**Follow the developments and post your comments and advice on Facebook Community at https://www.facebook.com/OmniNotes**

Help to keep translations updated is always welcome, if you want give a hand checkout the translation project on *https://translate.omninotes.app.*

记笔记<b>开源<b>应用程序旨在既有<b>简单的界面</b>，又保持<b>智能</b>行为。
该项目的灵感来自于缺少与旧手机和旧版本Android兼容的应用程序。它旨在提供一个有吸引力的外观，并遵循谷歌操作系统的最新设计准则。
**关注事态发展，并将您的评论和建议发布到Facebook社区https://www.facebook.com/OmniNotes**
如果你想亲自检查翻译项目，欢迎随时帮助更新翻译*https://translate.omninotes.app.*

<a href="https://f-droid.org/repository/browse/?fdid=it.feio.android.omninotes.foss" target="_blank">
<img src="https://f-droid.org/badge/get-it-on.png" alt="Get it on F-Droid" height="90"/></a>
<a href="https://play.google.com/store/apps/details?id=it.feio.android.omninotes" target="_blank">
<img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" alt="Get it on Google Play" height="90"/></a>

If you're willing to help speeding up developments please also opt-in for the Alpha version of the app following continuous delivery principles:

<a href="https://play.google.com/store/apps/details?id=it.feio.android.omninotes.alpha" target="_blank">
<img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" alt="Get it on Google Play" height="90"/></a>

## Features

Currently the following functions are implemented:

* Material Design interface
  *Basic add, modify, archive, trash and delete notes actions
* Share, merge and search notes
* Image, audio and generic file attachments
* Manage your notes using tags and categories
* To-do list
* Sketch-note mode
* Notes shortcut on home screen
* Export/import notes to backup
* Google Now integration: just tell "write a note" followed by the content
* Multiple widgets, DashClock extension, Android 4.2 lockscreen compatibility
* Multilanguage: 30+ languages supported: https://crowdin.com/project/omni-notes

目前实施了以下功能：
*材料设计界面
*基本的添加、修改、归档、废弃和删除笔记操作
*共享、合并和搜索笔记
*图像、音频和通用文件附件
*使用标记和类别管理笔记
*待办事项清单
*草图注释模式
*主屏幕上的注释快捷方式
*将注释导出/导入到备份
*Google Now集成：只需告诉“写一个便条”，然后告诉内容
*多个小部件、DashClock扩展、安卓4.2锁屏兼容性
*多语言：支持30多种语言：https://crowdin.com/project/omni-notes

Further developments will include:

* Notes sychronization
* Web interface to manage notes ([stub project](https://github.com/federicoiosue/omni-notes-desktop))

You can find a complete changelog inside the application settings menu!

进一步的发展将包括：
*笔记同步
*用于管理笔记的Web界面（[存根项目](https://github.com/federicoiosue/omni-notes-desktop))
您可以在应用程序设置菜单中找到完整的更改日志！

If you need some help on how to use the application you'll find everything you need in the [Help Online](assets/help/help.md) section.

如果您需要有关如何使用该应用程序的帮助，您可以在[联机帮助]（assets/help/help.md）部分找到所需的一切。

![](https://raw.githubusercontent.com/federicoiosue/Omni-Notes/develop/assets/play_store_pics/02.png)
![](https://raw.githubusercontent.com/federicoiosue/Omni-Notes/develop/assets/play_store_pics/03.png)
![](https://raw.githubusercontent.com/federicoiosue/Omni-Notes/develop/assets/play_store_pics/04.png)
![](https://raw.githubusercontent.com/federicoiosue/Omni-Notes/develop/assets/play_store_pics/05.png)
![](https://raw.githubusercontent.com/federicoiosue/Omni-Notes/develop/assets/play_store_pics/06.png)
![](https://raw.githubusercontent.com/federicoiosue/Omni-Notes/develop/assets/play_store_pics/07.png)
![](https://raw.githubusercontent.com/federicoiosue/Omni-Notes/develop/assets/play_store_pics/08.png)
![](https://raw.githubusercontent.com/federicoiosue/Omni-Notes/develop/assets/play_store_pics/09.png)
![](https://raw.githubusercontent.com/federicoiosue/Omni-Notes/develop/assets/play_store_pics/10.png)
![](https://raw.githubusercontent.com/federicoiosue/Omni-Notes/develop/assets/play_store_pics/11.png)
![](https://raw.githubusercontent.com/federicoiosue/Omni-Notes/develop/assets/play_store_pics/12.png)

## User guide

Look into the wiki for GIFs-based tutorials: [LINK](https://github.com/federicoiosue/Omni-Notes/wiki)
##用户指南
查看wiki中基于GIFs的教程：[链接](https://github.com/federicoiosue/Omni-Notes/wiki)

文档：Omni Notes user guide.note
链接：http://note.youdao.com/noteshare?id=c8ed5a8b8f90209df551c134c9bc0253&sub=99D0F1F95F3345908D900AEE8FA47009

## Build

Watch the following terminal session recording on how to compile distributable files
[![asciicast](https://asciinema.org/a/102898.png)](https://asciinema.org/a/102898)

##建造
观看以下有关如何编译可分发文件的终端会话录制

To be sure that build environment is fully compliant with the project the following command creates a container with all the needed tools to compile the code:
为确保生成环境与项目完全兼容，以下命令将创建一个容器，其中包含编译代码所需的所有工具：

```
cd {project-folder}; rm local.properties; docker rm android-omninotes; docker run -v $PWD:/workspace --name android-omninotes tabrindle/min-alpine-android-sdk:latest bash -c "yes | sdkmanager --update && yes | sdkmanager --licenses && cd /workspace && ./gradlew build --stacktrace -Dorg.gradle.daemon=true -Pandroid.useDeprecatedNdk=true"

```

## Test

To execute all tests included into the project connect a device or emulator, then run the following command:

## 试验
要执行项目中包含的所有测试，请连接设备或模拟器，然后运行以下命令：

```shell
./gradlew testAll
```

### Testing Pyramid

To speedup the development more levels of testing are available following the [testing pyramid approach](https://martinfowler.com/articles/practical-test-pyramid.html), each type test requires more time than the previous one.

###测试金字塔
为了加快开发速度，按照[测试金字塔方法]提供了更多级别的测试

### Unit Tests
```shell
./gradlew --stacktrace test
```

### Integration Tests
```shell
./gradlew --stacktrace -Pandroid.testInstrumentationRunnerArguments.notAnnotation=androidx.test.filters.LargeTest connectedAndroidTest
```

### UI Tests
```shell
./gradlew --stacktrace -Pandroid.testInstrumentationRunnerArguments.annotation=androidx.test.filters.LargeTest connectedPlayDebugAndroidTest
```
Notice that in this case I specified a single flavor to run tests on. This could be a useful and faster approach when you're testing specific flavor features.  

## Contributing

Due to the fact that I'm using [gitflow](https://github.com/nvie/gitflow) as code versioning methodology, you as developer should **always** start working on [develop branch](https://github.com/federicoiosue/Omni-Notes/tree/develop) that contains the most recent changes.

## 贡献
因为我正在使用[gitflow]

There are many features/improvements that are not on **my** roadmap but someone else could decide to work on them anyway: hunt for issues tagged as [Help Wanted](https://github.com/federicoiosue/Omni-Notes/issues?utf8=✓&q=label%3A"Help+wanted") to find them!

Feel free to add yourself to [contributors.md](https://github.com/federicoiosue/Omni-Notes/blob/develop/CONTRIBUTORS.md) file.

### New feature or improvements contributions

This kind of contributions **must** have screenshots or screencast as demonstration of the new additions.

有许多功能/改进不在**我的**路线图上，但其他人可能会决定使用它们：查找标记为[需要帮助]的问题(https://github.com/federicoiosue/Omni-Notes/issues?utf8=✓&amp;q=标签%3A“帮助+需要”）以查找它们！
请随意将自己添加到[contributors.md](https://github.com/federicoiosue/Omni-Notes/blob/develop/CONTRIBUTORS.md)文件。
###新功能或改进贡献
这类稿件**必须**有截图或屏幕广播来演示新添加的内容。

### Code style

If you plan to manipulate the code then you'll have to do it by following a [specific code style](https://gist.github.com/federicoiosue/dee53e882b3c70d544f8608769eb02fc).
Also pay attention if you're using any plugin that automatically formats/cleans/rearrange your code and set it to only change that code that you touched and not the whole files.

###代码样式
如果您计划操纵代码，则必须遵循[特定代码样式](https://gist.github.com/federicoiosue/dee53e882b3c70d544f8608769eb02fc).
如果您使用的插件能够自动格式化/清理/重新排列代码，并将其设置为仅更改您接触的代码，而不是更改整个文件，也请注意。

### Test your code contributions!

All code changes and additions **must** be tested.
See the [related section](#test) for more informations or this two pull requests comments: [one](https://github.com/federicoiosue/Omni-Notes/pull/646#pullrequestreview-187973443) and [two](https://github.com/federicoiosue/Omni-Notes/pull/683#issuecomment-506206689)

###测试您的代码贡献！
所有代码更改和添加**必须**进行测试。
有关更多信息，请参阅[相关部分]（#测试）或这两个请求注释：[1](https://github.com/federicoiosue/Omni-Notes/pull/646#pullrequestreview-187973443）和[2](https://github.com/federicoiosue/Omni-Notes/pull/683#issuecomment-506206689)

### Forking project

When forking the project you'll have to modify some files that are strictly dependent from my own development / build / third-party-services environment. Files that need some attention are the following:

  - *gradle.properties*: this is overridden by another file with the same name inside the *omniNotes* module. You can do the same or leave as it is, any missing property will let the app gracefully fallback on a default behavior.

###分叉项目
在分叉项目时，您必须修改一些严格依赖于我自己的开发/构建/第三方服务环境的文件。需要注意的文件如下：
-*gradle.properties*：这被*omniNotes*模块内另一个同名文件覆盖。你可以做同样的事情，
也可以保持原样，任何缺少的属性都会让应用程序优雅地退回默认行为。

## Code quality

A public instance of SonarQube is available both to encourage other developers to improve their code contributions (and existing code obviously) and to move the project even further into transparency and openness.

##代码质量
SonarQube的一个公开实例既可以鼓励其他开发人员改进他们的代码贡献（显然还有现有代码），也可以进一步提高项目的透明度和开放性。

Checkout for it [here](https://sonarcloud.io/dashboard?id=omni-notes)

Pull requests will be automatically analyzed and rejected if they'll rise the code technical debt.

在这里结账(https://sonarcloud.io/dashboard?id=omni-（附注）
如果拉动请求会增加代码技术债务，则会自动分析并拒绝它们。

## Dependencies

They're all listed into the [build.gradle](https://github.com/federicoiosue/Omni-Notes/blob/develop/omniNotes/build.gradle) file but due to the fact that many of the dependences have been customized by me I'd like to say thanks here to the original developers of these great libraries:

##依赖关系
它们都列在[build.gradle]中(https://github.com/federicoiosue/Omni-Notes/blob/develop/omniNotes/build.gradle)
但由于许多依赖项都不是由我定制的，我想在此向这些伟大库的原始开发人员表示感谢：

* https://github.com/RobotiumTech/robotium
* https://github.com/LarsWerkman/HoloColorPicker
* https://github.com/keyboardsurfer/Crouton
* https://github.com/romannurik/dashclock/wiki/API
* https://github.com/ACRA/acra
* https://github.com/Shusshu/Android-RecurrencePicker
* https://github.com/gabrielemariotti/changeloglib
* https://github.com/greenrobot/EventBus
* https://github.com/futuresimple/android-floating-action-button
* https://github.com/keyboardsurfer
* https://github.com/bumptech/glide
* https://github.com/neopixl/PixlUI
* https://github.com/afollestad/material-dialogs
* https://github.com/ical4j
* https://github.com/square/leakcanary
* https://github.com/pnikosis/materialish-progress
* https://github.com/apl-devs/AppIntro
* https://github.com/ReactiveX/RxAndroid
* https://github.com/artem-zinnatullin/RxJavaProGuardRules
* https://github.com/tbruyelle/RxPermissions
* https://github.com/ocpsoft/prettytime
* https://github.com/piwik/piwik-sdk-android
* https://github.com/mrmans0n/smart-location-lib


## Mentioned on

##提及

[XDA](https://www.xda-developers.com/omni-notes-the-open-source-note-app/)
[Android Authority](https://www.androidauthority.com/best-note-taking-apps-for-android-205356/)
[Droid Advisor](https://droidadvisor.com/omni-notes-note-taking-app/)
[Addictive Tips](https://www.addictivetips.com/android/note-taking-apps-for-android/)
[Techalook](https://techalook.com/apps/best-sticky-notes-android-iphone/)
[DZone](https://dzone.com/articles/amazing-open-source-android-apps-written-in-java)
[Slash Gear](https://www.slashgear.com/best-note-taking-apps-for-android-phones-and-tablets-04529297/)
[quaap.com](https://quaap.com/D/use-fdroid)

## Developed with love and passion by


* Federico Iosue - [Website](https://federico.iosue.it)
* [Other contributors](https://github.com/federicoiosue/Omni-Notes/blob/develop/CONTRIBUTORS.md)
##在爱和激情中成长
*费德里科·Iosue-[网站](https://federico.iosue.it)
*[其他撰稿人](https://github.com/federicoiosue/Omni-Notes/blob/develop/CONTRIBUTORS.md)


## License


    Copyright 2013-2021 Federico Iosue
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.



文档：omni-notes 文档.note
链接：http://note.youdao.com/noteshare?id=779e23661cadd3a5a36e71513c0684ee&sub=AD3995EA2C8E445C877844182A7B54D1

