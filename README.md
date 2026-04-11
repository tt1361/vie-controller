# vie-app-controller-decompiled

这是从 `VIEWEB/WEB-INF/lib/vie-app-controller-0.0.1-SNAPSHOT.jar` 反编译出来的 Java 工程。

## 目录说明

- `src/main/java`: Fernflower 反编译得到的源码
- `src/main/resources`: jar 内的 `META-INF` 资源
- `src/test/java`: 预留测试源码目录
- `src/test/resources`: 预留测试资源目录
- `original-classes`: 原始 jar 备份
- `build.gradle`: 可直接被 IntelliJ IDEA / Gradle 导入的离线构建文件
- `pom.xml`: 可直接 `mvn -o package` 的离线 Maven 构建文件
- `compile.sh`: 使用 `VIEWEB/WEB-INF/lib/*.jar` 作为类路径的本地编译脚本
- `package.sh`: 重新打包为 `dist/vie-app-controller-0.0.1-SNAPSHOT.jar`
- `install-into-vieweb.sh`: 备份并替换 `VIEWEB/WEB-INF/lib` 下的原 jar

## 使用方式

### IntelliJ IDEA

推荐直接打开当前目录。工程里已经补了 IntelliJ 模块和项目库配置，IDEA 会把 `../VIEWEB/WEB-INF/lib` 和 `./libs` 下的 jar 作为项目依赖加载。

如果 IDEA 之前已经按旧的 Maven 模型导入过，执行一次：

1. 关闭当前项目
2. 删除旧的 `.idea/workspace.xml` 或直接重新打开当前目录
3. 在 IDEA 里执行 `Reload All from Disk` / `Invalidate Caches` 后重新索引

### 命令行编译

```bash
./compile.sh
```

编译结果：

- `target/classes`

### Maven 打包

```bash
mvn -o package
```

当前 Maven 打包会在离线模式下委托执行 `./package.sh`，用于兼容原工程依赖 `VIEWEB/WEB-INF/lib` 扁平 jar 目录的历史结构。

Maven 构建产物：

- `target/vie-app-controller-0.0.1-SNAPSHOT.jar`

### 重新打包

```bash
./package.sh
```

打包结果：

- `dist/vie-app-controller-0.0.1-SNAPSHOT.jar`

临时打包目录：

- `target/package-stage`

### 回装到 VIEWEB

```bash
./install-into-vieweb.sh
```

该脚本会先备份原始 jar，再覆盖到：

- `../VIEWEB/WEB-INF/lib/vie-app-controller-0.0.1-SNAPSHOT.jar`

## 说明

- 反编译工具使用的是 IntelliJ 自带 Fernflower。
- 根目录没有直接覆盖使用 jar 内嵌的原始 `pom.xml`，因为它依赖父工程 `com.iflytek.vie:vie-app:0.0.1-SNAPSHOT`，单独拿出来无法离线直接构建。
- 当前 `pom.xml` 已经改造成可独立离线构建的 Maven 项目，但仍保留了原始坐标 `com.iflytek.vie:vie-app-controller:0.0.1-SNAPSHOT`。
- 这个 Maven 结构是“可编辑、可离线重打包”的重建版，不是完整恢复的原多模块父工程。
- `compile.sh` 和 `package.sh` 现在统一使用 `target/` 作为脚本构建中间目录，`dist/` 仅保留最终 jar。
- 原始 Maven 元数据保存在 `src/main/resources/META-INF/maven/com.iflytek.vie/vie-app-controller/`。
