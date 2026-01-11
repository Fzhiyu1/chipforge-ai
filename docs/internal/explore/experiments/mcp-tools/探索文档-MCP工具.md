# 探索文档：MCP 工具

## 探索目标

设计 ChipForge-AI 的 MCP 工具实现方案。

## 背景

基于已完成的知识图谱服务设计，探索如何通过 MCP 协议实现工具接口。

## 待探索内容

- [x] MCP SDK 技术分析
- [x] Spring Boot 集成方案

---

## 探索记录

### 话题1：MCP SDK 技术分析

**SDK 模块结构**：

| 模块 | 用途 |
|------|------|
| mcp-core | 核心实现（STDIO、HttpClient、Servlet） |
| mcp-json-jackson2 | Jackson JSON 绑定 |
| mcp | 便捷包（core + jackson） |
| mcp-spring-webmvc | Spring WebMVC 集成 |
| mcp-spring-webflux | Spring WebFlux 集成 |

**Server 模式**：

| 模式 | 类 | 适用场景 |
|------|-----|---------|
| 同步 | McpSyncServer | 简单场景，阻塞式 |
| 异步 | McpAsyncServer | 高并发，响应式 |

**Tool 定义结构**：

```java
Tool tool = new Tool(
    "tool_name",           // 工具名称
    "Tool description",    // 工具描述
    new Tool.InputSchema(  // 输入参数 Schema
        "object",
        Map.of(
            "param1", Map.of("type", "string", "description", "...")
        ),
        List.of("param1")  // 必填参数
    )
);
```

**Tool 处理器签名**：

```java
// 同步模式
(exchange, request) -> {
    Map<String, Object> args = request.arguments();
    // 处理逻辑
    return new CallToolResult(
        List.of(new TextContent("结果")),
        false  // isError
    );
}
```

**Transport 支持**：
- STDIO（命令行）
- HTTP SSE（Server-Sent Events）
- Spring WebMVC/WebFlux

**技术决策**：
- 使用同步模式（McpSyncServer）
- 启动时一次性注册所有工具
- 模块化单 Server 架构（工具按模块分包，命名带前缀如 kg_xxx）
- 复杂参数使用 JSON 字符串传递

---

### 话题2：Spring Boot 集成方案

**关键组件**：

| 组件 | 类 | 用途 |
|------|-----|------|
| Transport | WebMvcSseServerTransportProvider | SSE 传输 |
| Server | McpSyncServer | 同步服务器 |
| 端点 | /mcp/message | SSE 消息端点 |

**配置示例**：

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
            .tool(tool1, handler1)
            .build();
    }
}
```

**注意**：项目使用 MCP SDK 0.12.1，需验证 API 兼容性。

---

## 结论

### 技术选型

| 组件 | 选择 |
|------|------|
| Server 模式 | McpSyncServer（同步） |
| Transport | WebMvcSseServerTransportProvider |
| 架构 | 模块化单 Server |
| 参数传递 | JSON 字符串 |

### 下一步

1. 实现 MCP Server 配置类
2. 实现知识图谱 8 个工具
3. 集成测试
