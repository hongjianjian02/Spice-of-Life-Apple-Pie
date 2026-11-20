# 非官方 NeoForge 移植版发布说明

更新日期：2026-11-20；维护分支：hongjianjian02。移植版独立编号，当前版本为 `1.1.0`。

## 生成可分发包

使用 Java 21，在仓库根目录执行：

```powershell
.\gradlew.bat clean releaseBundle
```

Linux/macOS 使用 `./gradlew clean releaseBundle`。
此命令运行测试、验证许可证/源码打包，并生成：

- `build/libs/solapplepie Unofficial-1.1.0-1.21.1-neoforge.jar`：MOD 二进制，内含许可和来源说明。
- `build/libs/solapplepie Unofficial-1.1.0-1.21.1-neoforge-sources.zip`：与 JAR 对应的完整源码，含构建脚本、wrapper、资源、测试和许可证。
- `build/distributions/solapplepie Unofficial-1.1.0-1.21.1-neoforge-release.zip`：上述两个文件及许可证的合包。

JAR 与源码包中的 `SOURCE_INFO.txt` 必须一致。清单逐个列出源文件的 SHA-256，
Git 提交 ID 只用于说明基础版本；如果包含未提交修改，源码 ZIP 仍保留实际构建文件。
源码包不包含 `.git`、本地游戏存档、`run/`、缓存或凭据。Minecraft/NeoForge 等依赖由构建工具另行获取。

## 发布前检查

1. 保留 `LICENSE.txt` 原文、原作者署名、`NOTICE.txt` 和第三方许可，不增加禁止修改/再分发等相冲突的限制。
2. 修改文件保留日期声明；新增修改也补充日期和 `MODIFICATIONS.md`。
3. 标题和简介说明“非官方 1.21.1 NeoForge 移植”，链接原项目，不暗示原作者背书。
4. 每次发布都上传对应源码 ZIP（或整个 release ZIP），不要只给随时变化的分支链接。
   GitHub Release 同时附上 JAR、源码 ZIP 与 release ZIP；整合包再分发也要随附源码或等效下载入口。
5. **单独的 `sources.zip` 也可解压后直接构建，不需要 `.git`。** 构建时保留根目录的
   `SOURCE_INFO.txt` 用于识别来源；修改源码后的新 SHA-256 清单会重新生成。
6. CurseForge/其他平台需要使用你有权发布的项目，不使用原项目 ID 或凭据，除非原作者明确授权。
7. 当前发布工作流先将所有材料上传到 GitHub Release；可选 CurseForge 上传需要显式配置
   `CF_PROJECT_ID` 仓库变量和 `CF_API_TOKEN` secret。发布日志通过同一 GitHub Release 的源码资产
   提供对应源码下载链接；发布后确认链接在 CurseForge 页面公开可见。
8. 发布前确认依赖范围、测试结果、旧存档迁移限制及第三方联动说明。

这里是工程层面的合规材料，不是法律保证；也不替代平台规则、商标/名称规则或第三方资产授权核查。
