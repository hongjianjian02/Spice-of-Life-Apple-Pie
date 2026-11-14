<!-- Modified 2026-11-15 by hongjianjian02: port and distribution documentation. -->
# Minecraft 1.21.1 NeoForge 移植说明

## 目标与构建

- Minecraft：1.21.1
- NeoForge：21.1.265；声明支持范围为 `[21.1.265,21.2)`，实际启动验证使用 21.1.265。
- JDK：21；Gradle Wrapper：9.2.1；ModDevGradle：2.0.148。
- MOD ID 保持 `solapplepie`，物品注册 ID 与原版相同。
- Windows：设置 `JAVA_HOME` 为 JDK 21，然后执行 `gradlew.bat clean build`。
- Linux/macOS：使用 JDK 21，执行 `./gradlew clean build`。
- 产物：`build/libs/solapplepie-1.21.1-2.2.0-neoforge.jar`；客户端和服务器均需安装。
- 开发启动：`gradlew.bat runClient` / `gradlew.bat runServer`。
  工作目录分别为 `run/client` 和 `run/server`；服务器终端支持 `stop` 正常退出。

## 已迁移内容

1. ForgeGradle/Forge 元数据改为 ModDevGradle/NeoForge 元数据，并升级到 Java 21。
2. 玩家饮食记录改为可持久化 NeoForge Data Attachment；药水奖励缓存使用运行时 Attachment。
   登录、重生、维度切换继续同步饮食数据，死亡时遵循原有清空配置。
3. SimpleChannel 网络包改为有协议版本的客户端 CustomPacketPayload 与 StreamCodec。
4. 午餐袋、午餐盒、金午餐盒改用原版 `minecraft:container` 物品组件和 NeoForge ComponentItemHandler。
   存储会随物品保存与同步，保留食品筛选、最佳食物选择及碗/瓶返回逻辑。
   容器打开时显式传输主手/副手，防止选错物品；阻止移动打开的容器及嵌套容器。
5. 修复组件处理器返回副本时 Shift 点击合并可能丢失食物的问题。
6. GUI 改为 GuiGraphics、新版按钮/提示 API；注册菜单界面与热键。
7. 属性奖励使用 Holder 与稳定 ResourceLocation 修饰符 ID，避免重启后随机 ID 叠加。
8. 保留原版自动食物贡献算法的数值平衡：1.21.1 的实际饱和度增益换算回旧版使用的值。
9. 数据目录更新为 `recipe` / `advancement`，配方结果更新为 `id`，进度物品条件更新为 `items`。
10. 资源包版本更新为 34，物品 PNG 与模型引用更新到 `textures/item`，适配新版图集。
11. 恢复工具/实用品创造标签页中的四种物品；更新配置重载及 CI 的 Java/加载器版本。
12. 畸形奖励配置和已移除食品会跳过，不会导致整个 MOD 解析失败。

## 验证

- `clean build` 构建通过。
- NeoForge 运行环境中的 JUnit 回归测试：饮食 NBT、Attachment 保存恢复、食物过期与模拟贡献、
  网络编解码、容器组件/复制隔离/实时视图/能力注册/嵌套拒绝、稳定属性 ID、旧数值平衡、
  无效配置、模型纹理路径。
- 专用服务器启动到 `Done`；四个配方与一个进度加载成功；`help solapplepie` 确认命令注册；
  使用 `stop` 正常保存并退出。
- 客户端启动、MOD 注册、资源与模型图集加载已检查。

## 兼容性与尚未验证的范围

- **不保证旧 1.19.2 Forge 世界直接升级。** 玩家存储从 Forge capability 改为 NeoForge attachment，
  午餐容器从旧 capability NBT 改为物品组件；没有实现旧数据自动转换。升级前备份，优先新世界。
- Origins 改为无硬依赖的可选反射桥接，仅适用于保留 Edwin Mindcraft 旧接口的分支。
  不兼容接口会给出一次警告，不承诺现代 Origins 分支的饮食限制功能；未进行联动实测。
- Farmer's Delight、Pam's HarvestCraft、First Aid 等联动未安装实测；沿用注册 ID/配置驱动逻辑。
- 没有完成真实玩家的多人联机、吃食物触发奖励、死亡/跨维度、完整手册翻页及容器操作回归。
  启动和单元测试通过不等同于这些游戏行为已经逐项验证。
- 保留上游版权与许可证；本次没有重绘模型或像素贴图。

## 许可证与分发

本分支是 hongjianjian02 维护的**非官方移植版**，不是原作者的官方发行。
保留上游 LGPL 2.1 以及 Cyclic / Gradle Wrapper 的独立许可与版权说明。
执行 `gradlew.bat releaseBundle` 可生成包含 JAR、完整对应源码与许可证的发布包。
源码包包含 Gradle 构建脚本、wrapper、资源和测试，不只是 Java 源码。
发布包中的 `SOURCE_INFO.txt` 用 SHA-256 清单标识当前源文件快照，即使有未提交修改也能核对。
见 [RELEASING.md](RELEASING.md)；没有替你上传、发布或取得原项目平台权限。
