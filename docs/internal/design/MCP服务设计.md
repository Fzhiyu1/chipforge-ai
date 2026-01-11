# MCP 服务设计文档

## 1. 概述

### 1.1 目的

为 ChipForge-AI 提供 MCP（Model Context Protocol）服务，支持 AI 智能体调用各类工具。

### 1.2 技术选型

| 组件 | 选择 | 理由 |
|------|------|------|
| SDK | MCP SDK 0.12.1 | 官方 Java SDK |
| Server 模式 | McpSyncServer | 同步模式，简单可靠 |
| Transport | WebMvcSseServerTransportProvider | Spring WebMVC 集成 |
| 架构 | 模块化单 Server | 部署简单，扩展灵活 |

---

## 2. 架构设计

### 2.1 整体架构

```
┌─────────────────┐
│   AI 智能体     │
└────────┬────────┘
         │ MCP 协议 (SSE)
┌────────▼────────┐
│  MCP Server     │
│  (Spring Boot)  │
├─────────────────┤
│ ┌─────────────┐ │
│ │ kg_xxx      │ │  ← 知识图谱模块
│ ├─────────────┤ │
│ │ sim_xxx     │ │  ← 仿真模块（预留）
│ ├─────────────┤ │
│ │ synth_xxx   │ │  ← 综合模块（预留）
│ └─────────────┘ │
└─────────────────┘
```

### 2.2 模块化设计

- 单一 MCP Server，工具按模块组织
- 工具命名带前缀：`kg_xxx`、`sim_xxx`、`synth_xxx`
- 代码按模块分包

---

## 3. Spring Boot 集成

### 3.1 端点配置

| 端点 | 用途 |
|------|------|
| /mcp/message | SSE 消息端点 |

### 3.2 配置类

```java
@Configuration
class McpConfig {

    @Bean
    WebMvcSseServerTransportProvider transportProvider() {
        return new WebMvcSseServerTransportProvider(
            new ObjectMapper(), "/mcp/message"
        );
    }

    @Bean
    RouterFunction<ServerResponse> routerFunction(
        WebMvcSseServerTransportProvider transport
    ) {
        return transport.getRouterFunction();
    }

    @Bean
    McpSyncServer mcpServer(McpServerTransportProvider transport) {
        return McpServer.sync(transport)
            .serverInfo("chipforge-ai", "0.1.0")
            // 注册工具
            .build();
    }
}
```

---

## 4. 工具定义规范

### 4.1 Tool 结构

```java
Tool tool = new Tool(
    "tool_name",
    "Tool description",
    new Tool.InputSchema(
        "object",
        Map.of("param", Map.of("type", "string")),
        List.of("param")  // 必填参数
    )
);
```

### 4.2 处理器签名

```java
(exchange, request) -> {
    Map<String, Object> args = request.arguments();
    // 处理逻辑
    return new CallToolResult(
        List.of(new TextContent("结果")),
        false  // isError
    );
}
```

### 4.3 参数传递

复杂参数使用 JSON 字符串传递，在处理器内部解析。

---

## 5. 代码结构

```
src/main/java/com/chipforge/ai/
├── ChipForgeApplication.java
├── config/
│   └── McpConfig.java           # MCP 配置
└── mcp/
    ├── kg/                      # 知识图谱模块
    │   ├── KgTools.java         # 工具定义
    │   └── KgService.java       # 业务逻辑
    ├── sim/                     # 仿真模块（预留）
    └── synth/                   # 综合模块（预留）
```
