# AdvancedShader — 1.10.2 移植

这是 [HyperCol/AdvancedShader](https://github.com/HyperCol/AdvancedShader) 的移植分支，
把原项目从 Minecraft 1.12.2（OptiFine G5）移植到 Minecraft 1.10.2（OptiFine HD U H5）。

- 分支：`murong81a/AdvancedShader` 的 `1.10.2-forge` 分支
- 源码来自：`HyperCol/AdvancedShader` 的 `main` 分支（1.12.2 / OptiFine G5）
- 目标环境：Minecraft 1.10.2 + Forge 12.18.3.2511 + OptiFine HD U H5

## 构建

ForgeGradle 2.3 + JDK 8。`libs/OptiFine-H5-DEOBF-DO-NOT-PUT-IN-MODS.jar` 是编译用的
去混淆 OptiFine，`compileOnly` 引用，**不要放进 mods 目录**；运行时由用户自己的
`OptiFine_1.10.2_HD_U_H5.jar` 提供这些类。

```
./gradlew build
```

产物：`build/libs/AdvancedShader-1.0-FINAL-1.10.2.jar`

## 许可

沿用原项目 LICENSE（MIT）。
