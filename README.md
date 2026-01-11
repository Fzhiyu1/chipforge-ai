# ChipForge-AI

> AI-powered FPGA development toolkit

## 项目简介

ChipForge-AI 是一个基于 MCP 协议的 FPGA 开发工具服务器，通过 Claude Code 集成，提供智能化的硬件设计辅助功能。

## 核心功能

- 🧠 **知识图谱查询** - Verilog 语法、设计模式、最佳实践
- 🔬 **仿真工具封装** - ModelSim/Icarus Verilog 自动化
- 📊 **波形分析** - 智能波形调试和问题定位
- ⚙️ **综合工具** - Vivado/Quartus 集成
- 📁 **项目管理** - 自动化工作流程和状态跟踪

## 技术栈

- **语言**: Java 17+
- **框架**: Spring Boot 3.3.0
- **协议**: MCP (Model Context Protocol) 0.12.1
- **构建**: Maven

## 快速开始

### 前置要求

- Java 17 或更高版本
- Maven 3.6+
- Claude Code CLI

### 安装

```bash
# 克隆项目
git clone https://github.com/Fzhiyu1/chipforge-ai.git
cd chipforge-ai

# 构建项目
mvn clean package

# 启动服务器
mvn spring-boot:run
```

### 使用

在 Claude Code 中：
```
/forge
```

## 项目结构

```
chipforge-ai/
├── docs/              # 文档
├── src/
│   ├── main/
│   │   ├── java/      # Java 源代码
│   │   └── resources/ # 配置文件
│   └── test/          # 测试代码
└── pom.xml            # Maven 配置
```

## 开发状态

🚧 **开发中** - 当前版本：v0.1.0-SNAPSHOT

## 许可证

Apache License 2.0

## 作者

小枫 (XiaoFeng)

---

**ChipForge-AI** - Where silicon meets intelligence
