# ROS Interface Syntax Highlighter — CLion Plugin

为 JetBrains CLion 提供 ROS 接口定义文件（`.msg` / `.srv` / `.action`）的语法高亮支持。

## 目录

- [动机](#动机)
- [调研过程](#调研过程)
- [架构设计](#架构设计)
- [项目结构](#项目结构)
- [核心实现详解](#核心实现详解)
- [支持的语法元素](#支持的语法元素)
- [构建与安装](#构建与安装)
- [开发环境](#开发环境)
- [参考资料](#参考资料)

---

## 动机

在 CLion 中进行 ROS 软件开发时，`.msg`、`.srv`、`.action` 等接口定义文件没有语法高亮。VS Code 有成熟的 ROS 插件（如 `ms-iot.vscode-ros` 和 `Ranch-Hand-Robotics/rde-ros-2`）提供此功能，但 CLion 缺少等效插件。

已有的 IntelliJ 插件 [Noam-Dori/ros-integrate](https://github.com/Noam-Dori/ros-integrate) 已停止维护（最后更新 2020 年），且不包含 msg 文件的语法高亮。

---

## 调研过程

实现前进行了系统性的技术调研，涵盖三个方向：

### 1. VS Code ROS 插件的语法高亮方案

研究了两个主要的 VS Code 扩展：

**[ms-iot/vscode-ros](https://github.com/ms-iot/vscode-ros)**（原始版本，微软维护）
- ROS 1 + ROS 2 支持
- 功能包括：自动 ROS 环境配置、核心管理、colcon/catkin 构建任务、启动/调试 launch 文件、rosdep 快捷操作
- 语法高亮支持：`.msg`、`.urdf` 等 ROS 文件
- 此项目已标记为废弃，由 Ranch Hand Robotics 接替

**[Ranch-Hand-Robotics/rde-ros-2](https://github.com/Ranch-Hand-Robotics/rde-ros-2)**（Ranch Hand Robotics 维护）
- 专注于 ROS 2，从 ms-iot 分拆而来
- 语法高亮：`.msg`、`.urdf` 等
- IntelliSense：hover 显示消息属性、Go to Definition（F12）
- 自动添加 ROS C++ include 和 Python import 路径
- 格式化 C++ 为 ROS clang-format 风格
- 测试资源管理器集成

两个 VS Code 扩展的语法高亮均通过 **TextMate 语法文件**（`.tmLanguage`）实现。

### 2. TextMate 语法方案

发现关键仓库 **[jtbandes/ros-tmlanguage](https://github.com/jtbandes/ros-tmlanguage)**：

- 这是 ROS 接口定义语言的 **TextMate 语法** 官方实现
- 被 GitHub Linguist 使用（GitHub 上 `.msg` 文件的语法高亮）
- 被 rde-ros-2 扩展使用
- 定义 `source.rosmsg` 作用域
- 支持 `.msg`、`.srv`、`.action` 三种文件类型
- 提供 YAML、JSON、PList（.tmLanguage）三种格式

TextMate 语法高亮覆盖：
| 要素 | 作用域名 | 示例 |
|---|---|---|
| 注释 | `comment.line.number-sign.rosmsg` | `# this is a comment` |
| 内置类型 | `storage.type.rosmsg` | `int32`, `float64`, `string`, `bool` |
| 包/类型引用 | `support.type.rosmsg` | `std_msgs/Header` |
| 字段名 | `variable.other.field.rosmsg` | `header`, `pose` |
| 数字 | `constant.numeric.rosmsg` | `42`, `3.14` |
| 布尔值 | `constant.language.boolean.rosmsg` | `true`, `false` |
| 字符串 | `string.quoted.double.rosmsg` | `"hello"` |
| 分隔符 | `meta.separator.rosmsg` | `---`, `===`, `MSG:` |
| 属性 | `storage.modifier.attribute.rosmsg` | `@optional` |

### 3. IntelliJ Platform TextMate Bundle 集成方案

调研了 IntelliJ Platform 对 TextMate 语法文件的支持：

**[IntelliJ TextMate Bundles 插件](https://www.jetbrains.com/help/idea/textmate.html)**
- 所有 IntelliJ 产品内置，默认启用
- 支持 TextMate `.tmLanguage` / Sublime `.sublime-syntax` 格式
- 通过 Settings → Editor → TextMate Bundles 手动导入
- 也支持通过插件以编程方式注册 Bundle

**插件集成方式**（参考 [likec4/jetbrains-plugin](https://github.com/likec4/jetbrains-plugin)）：

```
plugin.xml 扩展点:
<extensions defaultExtensionNs="com.intellij">
    <textmate.bundleProvider implementation="xxx.TextMateBundleProvider"/>
</extensions>

build.gradle.kts 依赖:
bundledPlugins.add("org.jetbrains.plugins.textmate")

prepareSandbox 任务: 将 textmate/ 目录复制到插件沙箱
```

**作用域 → 颜色映射**（参考 [TextMateDefaultColorsProvider](https://github.com/JetBrains/intellij-community/blob/master/plugins/textmate/src/org/jetbrains/plugins/textmate/language/syntax/highlighting/TextMateDefaultColorsProvider.java)）：

| TextMate 作用域前缀 | IntelliJ 颜色键 |
|---|---|
| `comment` | `DefaultLanguageHighlighterColors.LINE_COMMENT` |
| `constant` | `DefaultLanguageHighlighterColors.CONSTANT` |
| `constant.numeric` | `DefaultLanguageHighlighterColors.NUMBER` |
| `storage.type` | `DefaultLanguageHighlighterColors.KEYWORD` |
| `string` | `DefaultLanguageHighlighterColors.STRING` |
| `entity.name` | `DefaultLanguageHighlighterColors.CLASS_NAME` |
| `support.type` | `DefaultLanguageHighlighterColors.PREDEFINED_SYMBOL` |
| `variable` | `DefaultLanguageHighlighterColors.LOCAL_VARIABLE` |
| `keyword` | `DefaultLanguageHighlighterColors.KEYWORD` |

---

## 架构设计

采用 **双层高亮方案**：

```
┌─────────────────────────────────────────────────────────────┐
│                     CLion IDE                               │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              Style 1: TextMate Bundle (主力)          │   │
│  │                                                       │   │
│  │  plugin.xml                                           │   │
│  │    └─ textmate.bundleProvider                          │   │
│  │         └─ RosTextMateBundleProvider                   │   │
│  │              └─ 加载 textmate/ROS/ROS Interface.tmLanguage│   │
│  │                   └─ TextMateService                    │   │
│  │                        └─ TextMateHighlighter (高亮)    │   │
│  └──────────────────────────────────────────────────────┘   │
│                          │                                   │
│                          ▼                                   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         Style 2: 原生 IntelliJ Lexer (fallback)       │   │
│  │                                                       │   │
│  │  RosInterfaceLexer (手写字符级 Lexer)                  │   │
│  │    └─ 生成 RosInterfaceTokenType token                 │   │
│  │         └─ RosSyntaxHighlighter 映射为 TextAttributesKey│   │
│  │              └─ 颜色设置页: RosColorSettingsPage       │   │
│  └──────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              文件类型注册                              │   │
│  │                                                       │   │
│  │  RosInterfaceLanguage (ROSInterface)                   │   │
│  │    └─ RosInterfaceFileType (.msg/.srv/.action)          │   │
│  │         └─ RosInterfaceParserDefinition (轻量解析器)    │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### 设计决策

**为什么优先使用 TextMate Bundle？**
1. **复用成熟方案** — jtbandes/ros-tmlanguage 是 ROS 社区维护的官方语法，被 GitHub、VS Code rde-ros-2 使用
2. **维护成本低** — TextMate 语法文件是声明式的，不需要写解析器代码
3. **质量可靠** — 覆盖了 ROS msg/srv/action 的各种边界情况（数组、转义、嵌套类型、gendeps 格式等）
4. **颜色映射完善** — IntelliJ 对标准 TextMate 作用域有内置的配色方案

**为什么还需要原生 Lexer 作为 fallback？**
1. TextMate Bundles 插件可能在某些 CLion 版本中被禁用
2. 原生实现允许更精确的控制（颜色设置页面、自定义配色）
3. 为未来扩展（代码补全、导航）提供 PSI 树基础

---

## 项目结构

```
ros-msg-highlighter/
├── build.gradle.kts                          # Gradle 构建配置
├── settings.gradle.kts                       # Gradle 项目设置
├── gradle.properties                         # 插件版本属性
├── gradlew / gradlew.bat                     # Gradle Wrapper
├── gradle/wrapper/                           # Gradle Wrapper JAR
│
└── src/main/
    ├── java/org/ros/clion/highlighter/
    │   ├── RosInterfaceLanguage.java          # 语言定义
    │   ├── RosInterfaceFileType.java          # 文件类型 (.msg/.srv/.action)
    │   ├── RosInterfaceTokenType.java         # Token 类型
    │   ├── RosInterfaceLexer.java             # 词法分析器 (fallback)
    │   ├── RosInterfaceParser.java            # 轻量解析器
    │   ├── RosInterfaceParserDefinition.java  # 解析器定义注册
    │   ├── RosInterfaceFile.java              # PSI 文件
    │   ├── RosInterfacePsiElement.java        # PSI 元素
    │   ├── RosSyntaxHighlighter.java          # 语法高亮 (fallback)
    │   ├── RosSyntaxHighlighterFactory.java   # 高亮工厂
    │   ├── RosSyntaxHighlighterKeys.java      # 颜色键定义
    │   ├── RosColorSettingsPage.java          # 颜色设置页面
    │   └── RosTextMateBundleProvider.java     # TextMate Bundle 注册
    │
    └── resources/
        ├── META-INF/
        │   └── plugin.xml                    # 插件描述符 (核心配置)
        │
        └── textmate/ROS/
            ├── ROS Interface.tmLanguage       # TextMate 语法 (PList 格式)
            ├── package.json                   # VS Code 兼容描述 (可选)
            └── language-configuration.json    # 语言配置 (注释符号等)
```

---

## 核心实现详解

### 1. 插件配置 (plugin.xml)

`src/main/resources/META-INF/plugin.xml` 声明了以下扩展点：

| 扩展点 | 实现类 | 作用 |
|---|---|---|
| `com.intellij.fileType` | `RosInterfaceFileType` | 将 `.msg/.srv/.action` 关联到 `ROSInterface` 语言 |
| `com.intellij.lang.parserDefinition` | `RosInterfaceParserDefinition` | 注册词法分析器和解析器 |
| `com.intellij.lang.syntaxHighlighterFactory` | `RosSyntaxHighlighterFactory` | 注册 fallback 高亮器 |
| `com.intellij.colorSettingsPage` | `RosColorSettingsPage` | 在设置中添加 ROS Interface 配色页 |
| `com.intellij.textmate.bundleProvider` | `RosTextMateBundleProvider` | 注册 TextMate 语法包 (主力方案) |

依赖声明：
```xml
<depends>com.intellij.modules.platform</depends>
<depends>org.jetbrains.plugins.textmate</depends>
```

### 2. TextMate Bundle 注册

`RosTextMateBundleProvider` 实现 `TextMateBundleProvider` 接口：

```java
public class RosTextMateBundleProvider implements TextMateBundleProvider {
    @Override
    public @NotNull Collection<Bundle> getBundles(@NotNull ClassLoader classLoader) {
        URL bundleUrl = classLoader.getResource("textmate/ROS");
        if (bundleUrl == null) return Collections.emptyList();
        File bundleDir = new File(bundleUrl.getPath());
        if (!bundleDir.exists() || !bundleDir.isDirectory()) return Collections.emptyList();
        return List.of(new Bundle(bundleDir));
    }
}
```

IntelliJ 的 `TextMateService` 会自动发现通过此扩展点注册的 Bundle，读取其中的 `.tmLanguage` 文件，并应用语法高亮。

### 3. TextMate 语法文件

`ROS Interface.tmLanguage` 是 PList XML 格式的 TextMate 语法文件，定义 `source.rosmsg` 作用域。核心规则：

```xml
<!-- 内置类型匹配 -->
<key>builtin-types</key>
<dict>
    <key>name</key>
    <string>storage.type.rosmsg</string>
    <key>match</key>
    <string>\b(?:bool|byte|char|u?int(?:8|16|32|64)|float(?:32|64)|w?string|time|duration)\b</string>
</dict>

<!-- 注释 -->
<key>comments</key>
<dict>
    <key>name</key>
    <string>comment.line.number-sign.rosmsg</string>
    <key>match</key>
    <string>#.*</string>
</dict>

<!-- 分隔符 ---, ===, MSG: -->
<key>separators</key>
<dict>
    <key>name</key>
    <string>meta.separator.rosmsg</string>
    <key>match</key>
    <string>^---\s*$\n?</string>
</dict>
```

文件类型声明：
```xml
<key>fileTypes</key>
<array>
    <string>msg</string>
    <string>srv</string>
    <string>action</string>
</array>
```

### 4. Fallback Lexer

`RosInterfaceLexer` 是一个手写的字符级 JFlex 风格 Lexer（基于 `LexerBase`），可识别以下 token：

```java
// 解析规则示例（简化）：
if (c == '#')              → RosInterfaceTokenType.COMMENT
if (c == '-' && "---")     → RosInterfaceTokenType.SEPARATOR
if (c == '[' || c == ']')  → RosInterfaceTokenType.ARRAY
if (isBuiltinType(word))   → RosInterfaceTokenType.BUILTIN_TYPE
if (isNumber(...))          → RosInterfaceTokenType.NUMBER
if (c == '"' || c == '\'') → RosInterfaceTokenType.STRING
if (c == '@')              → RosInterfaceTokenType.ATTRIBUTE
```

内置类型列表：`bool`, `byte`, `char`, `int8`..`int64`, `uint8`..`uint64`, `float32`, `float64`, `string`, `wstring`, `time`, `duration`

### 5. 颜色设置页面

`RosColorSettingsPage` 允许用户在 **Settings → Editor → Color Scheme → ROS Interface** 中自定义每个语法元素的颜色：

```java
private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
    new AttributesDescriptor("Comment", RosSyntaxHighlighterKeys.COMMENT),
    new AttributesDescriptor("Built-in type", RosSyntaxHighlighterKeys.BUILTIN_TYPE),
    new AttributesDescriptor("Type (msg/package)", RosSyntaxHighlighterKeys.SUPPORT_TYPE),
    new AttributesDescriptor("Field name", RosSyntaxHighlighterKeys.FIELD),
    new AttributesDescriptor("Constant", RosSyntaxHighlighterKeys.CONSTANT),
    new AttributesDescriptor("Number (integer)", RosSyntaxHighlighterKeys.NUMBER),
    new AttributesDescriptor("Number (float)", RosSyntaxHighlighterKeys.FLOAT),
    new AttributesDescriptor("Boolean literal", RosSyntaxHighlighterKeys.BOOLEAN),
    new AttributesDescriptor("String (quoted)", RosSyntaxHighlighterKeys.STRING),
    new AttributesDescriptor("String (unquoted)", RosSyntaxHighlighterKeys.STRING_UNQUOTED),
    new AttributesDescriptor("String escape", RosSyntaxHighlighterKeys.STRING_ESCAPE),
    new AttributesDescriptor("Array brackets", RosSyntaxHighlighterKeys.ARRAY),
    new AttributesDescriptor("Separator (---)", RosSyntaxHighlighterKeys.SEPARATOR),
    new AttributesDescriptor("Attribute modifier", RosSyntaxHighlighterKeys.ATTRIBUTE),
};
```

预览文本展示了所有支持的语法元素：
```rosmsg
# Comment: Odometry message
std_msgs/Header header
string child_frame_id
geometry_msgs/PoseWithCovariance pose
geometry_msgs/TwistWithCovariance twist
---
# Service request/response separator
int32 STATUS_OK = 0
int32 STATUS_ERROR = 1
bool success
string message
---
# Action feedback separator
float64 progress
string[] messages ["hello", "world"]
@optional string optional_field
```

### 6. 构建配置要点

`build.gradle.kts` 关键配置：

```kotlin
intellij {
    type.set("CL")                    // 目标: CLion
    version.set("2024.1")            // IntelliJ Platform 版本
    bundledPlugins.add("org.jetbrains.plugins.textmate")  // TextMate 插件依赖
    plugins.add("com.intellij.clion") // CLion 特定 API
}

tasks {
    patchPluginXml {
        sinceBuild.set("241")         // 兼容 2024.1+
        untilBuild.set("243.*")
    }
    prepareSandbox {
        // 将 TextMate 语法文件复制到插件沙箱
        from(layout.projectDirectory.dir("src/main/resources/textmate")) {
            into("${pluginName.get()}/textmate")
        }
    }
}
```

---

## 支持的语法元素

| 语法元素 | 示例 | 高亮颜色 | TextMate 作用域 |
|---|---|---|---|
| 注释 | `# this is a comment` | 灰色斜体 | `comment.line.number-sign` |
| 内置类型 | `int32`, `float64`, `string` | 关键字色 | `storage.type` |
| 包引用 | `std_msgs/Header` | 预定义符号色 | `support.type` |
| 字段名 | `header`, `pose`, `data` | 字段色 | `variable.other.field` |
| 常量 | `STATUS_OK = 0` | 常亮色 | `constant.numeric` |
| 整数 | `42`, `-1` | 数字色 | `constant.numeric.integer` |
| 浮点数 | `3.14`, `-0.5e10` | 数字色 | `constant.numeric.float` |
| 布尔值 | `true`, `false` | 关键字色 | `constant.language.boolean` |
| 双引号字符串 | `"hello, world"` | 字符串色 | `string.quoted.double` |
| 单引号字符串 | `'hello'` | 字符串色 | `string.quoted.single` |
| 无引号字符串 | `some_value` | 字符串色 | `string.unquoted` |
| 转义字符 | `\"`, `\n`, `\x41` | 转义色 | `constant.character.escape` |
| 数组标记 | `[]`, `[<=5]` | 括号色 | `meta.array` |
| 数组值 | `[1, 2, 3]` | 数字色 | `meta.array` |
| 分隔符 | `---` | 元数据色 | `meta.separator` |
| MSG 标记 | `MSG: std_msgs/Header` | 元数据色 | `meta.separator` |
| 属性 | `@optional` | 属性色 | `storage.modifier.attribute` |

---

## 构建与安装

```bash
# 1. 进入项目目录
cd /path/to/ros-msg-highlighter

# 2. 构建插件
./gradlew buildPlugin

# 3. 构建产物为 ZIP 文件
ls build/distributions/ros-msg-highlighter-1.0.0.zip
```

安装到 CLion：
1. 打开 CLion → **Settings** → **Plugins**
2. 点击齿轮图标 → **Install Plugin from Disk...**
3. 选择 `build/distributions/ros-msg-highlighter-1.0.0.zip`
4. 重启 CLion
5. 打开任意 `.msg` / `.srv` / `.action` 文件，语法高亮自动生效

**验证 TextMate Bundle 加载：**
- Settings → Editor → TextMate Bundles
- 应看到 "ROS Interface" 出现在列表中

**自定义配色：**
- Settings → Editor → Color Scheme → ROS Interface
- 可独立调整每个语法元素的颜色

---

## 开发环境

| 工具 | 版本 |
|---|---|
| JDK | 17+ |
| IntelliJ Platform | 2024.1+ |
| CLion | 2024.1 - 2024.3 |
| Gradle | 8.x (由 Wrapper 管理) |
| IntelliJ Platform Gradle Plugin | 1.17.4 |

### 本地开发

```bash
# 运行测试 CLion 实例
./gradlew runIde
```

这会在一个独立的 CLion 沙箱中启动插件，可以实时测试。

---

## 参考资料

### TextMate 语法
- [jtbandes/ros-tmlanguage](https://github.com/jtbandes/ros-tmlanguage) — ROS 接口 TextMate 语法（本项目使用的核心语法文件）
- [TextMate Language Grammars](https://macromates.com/manual/en/language_grammars) — TextMate 语法规则官方文档
- [ROS 2 Interfaces 概念](https://docs.ros.org/en/rolling/Concepts/Basic/About-Interfaces.html) — ROS msg/srv/action 规范

### VS Code ROS 扩展
- [Ranch-Hand-Robotics/rde-ros-2](https://github.com/Ranch-Hand-Robotics/rde-ros-2) — ROS 2 开发扩展（包含 msg 语法高亮）
- [ms-iot/vscode-ros](https://github.com/ms-iot/vscode-ros) — 原始 VS Code ROS 扩展
- [ajshort/vscode-msg](https://github.com/ajshort/vscode-msg) — 最早的 msg 语法高亮扩展

### IntelliJ Platform 插件开发
- [CLion Plugin Development](https://www.jetbrains.com/help/clion/develop-plugins-for-clion.html) — CLion 插件开发指南
- [Custom Language Support](https://plugins.jetbrains.com/docs/intellij/custom-language-support.html) — 自定义语言支持教程
- [TextMate Bundles Support](https://www.jetbrains.com/help/idea/textmate.html) — IntelliJ TextMate Bundle 导入
- [Implementing Lexer](https://plugins.jetbrains.com/docs/intellij/implementing-lexer.html) — IntelliJ Lexer API 文档
- [likec4/jetbrains-plugin](https://github.com/likec4/jetbrains-plugin) — 参考了其 `textmate.bundleProvider` 集成方式
- [Plugin Configuration File](https://plugins.jetbrains.com/docs/intellij/plugin-configuration-file.html) — plugin.xml 配置文档
- [TextMateDefaultColorsProvider](https://github.com/JetBrains/intellij-community/blob/master/plugins/textmate/src/org/jetbrains/plugins/textmate/language/syntax/highlighting/TextMateDefaultColorsProvider.java) — TextMate 作用域到 IntelliJ 颜色键的映射

### 现有 IntelliJ ROS 插件
- [Noam-Dori/ros-integrate](https://github.com/Noam-Dori/ros-integrate) — 已有的 IntelliJ ROS 插件（已停止维护，不包含 msg 高亮）
- [Hatchery](https://github.com/duckietown/hatchery) — Duckietown 的 ROS IDE 插件
- [ROS Support - JetBrains Marketplace](https://plugins.jetbrains.com/plugin/11235-ros-support) — 另一个 ROS 支持插件

---

## 许可证

### 本项目（自主代码部分）

版权所有 (c) 2025 ROS Developers

Licensed under the **Apache License, Version 2.0**. 详见根目录 [LICENSE](LICENSE) 文件。

### 第三方组件

#### ROS Interface.tmLanguage — TextMate 语法文件

- 来源: [jtbandes/ros-tmlanguage](https://github.com/jtbandes/ros-tmlanguage)
- 许可证: **MIT License**
- 版权所有: Jacob Bandes-Storch
- 许可证文件位于: `src/main/resources/textmate/ROS/LICENSE`

该文件根据 MIT 许可证条款使用。使用本插件即表示您也接受了该许可证的条款。

### 致谢

- [Jacob Bandes-Storch (@jtbandes)](https://github.com/jtbandes) — 创建并维护 ROS 接口 TextMate 语法（`ros-tmlanguage`），本项目核心语法高亮能力来源于此
- [Ranch Hand Robotics](https://github.com/Ranch-Hand-Robotics) — 维护 `rde-ros-2` VS Code 扩展，为功能设计提供参考
- [Microsoft IoT](https://github.com/ms-iot) — 维护 `vscode-ros` 扩展，为功能设计提供参考
- [JetBrains](https://github.com/JetBrains) — IntelliJ Platform 和 TextMate Bundles 插件
- [Noam Dori](https://github.com/Noam-Dori) — `ros-integrate` IntelliJ 插件先驱
- [LikeC4 Team](https://github.com/likec4) — `jetbrains-plugin` 中 `textmate.bundleProvider` 集成方式的参考