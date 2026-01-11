# AgentPlan

> 为 AI 编程智能体设计的通用项目计划与进度追踪系统

## 🎯 什么是 AgentPlan？

AgentPlan 是一套基于 Markdown 的分层文档架构，专为 **Coder 智能体**（如 Claude Code、Cursor、GitHub Copilot 等）优化设计。

### 核心特性

- **智能体友好** - 分层结构，按需读取，减少 token 消耗
- **Sprint 驱动** - 敏捷开发流程，周期性迭代
- **状态可视化** - 清晰的符号系统和完成度追踪
- **知识沉淀** - ADR 记录设计决策，保留项目历史

## 📦 快速开始

### 1. 复制模板到你的项目

```bash
cp -r project-tracking/template your-project/project-tracking
```

### 2. 替换占位符

在所有文件中搜索并替换以下占位符：

| 占位符 | 说明 | 示例 |
|--------|------|------|
| `{{PROJECT_NAME}}` | 项目名称 | MyApp |
| `{{DATE}}` | 当前日期 | 2025-12-24 |
| `{{WEEK}}` | 周数 | 52 |
| `{{START_DATE}}` | Sprint 开始日期 | 2025-12-23 |
| `{{END_DATE}}` | Sprint 结束日期 | 2025-12-29 |
| `{{SPRINT_GOAL}}` | Sprint 目标 | 完成核心功能 |
| `{{DAY}}` | 星期 | Mon |
| `{{TASK_NAME}}` | 任务名称 | 用户认证模块 |
| `{{TASK_DESCRIPTION}}` | 任务描述 | 实现用户登录功能 |
| `{{MODULE_NAME}}` | 模块名称 | 认证系统 |
| `{{MODULE_PATH}}` | 模块路径 | auth/ |
| `{{PROJECT_STRUCTURE}}` | 项目结构 | 你的目录树 |

### 3. 在 CLAUDE.md 中添加引用

```markdown
## 进度追踪

项目使用分层文档架构追踪开发进度，位于 `project-tracking/` 目录。

### 启动检查清单

每次开始工作前，按顺序读取：

1. `project-tracking/INDEX.md` - 项目状态概览（必读）
2. `project-tracking/status/current-sprint.md` - 当前 Sprint 目标
3. `project-tracking/status/active-tasks.md` - 进行中任务详情（如有）

详细指南见：`project-tracking/QUICK-START.md`
```

---

## 📂 目录结构

```
project-tracking/
├── INDEX.md              # 项目状态概览（入口）
├── QUICK-START.md        # Claude Code 快速上手指南
├── status/
│   ├── current-sprint.md # 当前 Sprint 目标和进度
│   ├── active-tasks.md   # 进行中任务详情（最多10个）
│   └── backlog.md        # 待办队列
├── modules/
│   └── example-module.md # 模块文档模板
├── reference/
│   ├── decisions.md      # 设计决策记录 (ADR)
│   └── metrics.md        # 指标追踪
└── history/              # 历史记录（可选）
```

---

## 🎯 设计理念

### 为什么需要这个模板？

1. **Claude Code 友好** - 分层结构，按需读取，减少 token 消耗
2. **进度可视化** - 清晰的状态符号和完成度百分比
3. **任务追踪** - 从 Backlog → Sprint → Active → Done 的完整流程
4. **知识沉淀** - 设计决策记录 (ADR) 保留决策历史

### 核心原则

- **INDEX.md 是入口** - 所有导航从这里开始
- **最多 10 个活跃任务** - 避免过度并行
- **及时更新** - 完成子任务后立即更新进度
- **询问后更新** - 更新前先询问用户确认

---

## 📊 状态符号速查

| 符号 | 含义 |
|------|------|
| ✅ | 已完成 |
| 🔄 | 进行中 |
| 📅 | 已计划 |
| ❌ | 未开始 |
| ⚠️ | 有问题 |
| 🔒 | 已阻塞 |
| 🔴 | P0 高优先级 |
| 🟡 | P1 中优先级 |
| 🟢 | P2 低优先级 |

---

## 📝 使用流程

### 开始新项目

1. 复制模板到项目
2. 替换所有占位符
3. 在 `backlog.md` 中添加所有任务
4. 选择本周任务到 `current-sprint.md`
5. 开始工作的任务移到 `active-tasks.md`

### 日常工作

1. 读取 `INDEX.md` 了解当前状态
2. 读取 `current-sprint.md` 了解本周目标
3. 读取 `active-tasks.md` 了解当前任务
4. 完成子任务后更新进度
5. 完成任务后移到已完成列表

### Sprint 结束

1. 回顾完成情况
2. 更新 `metrics.md` 指标
3. 归档到 `history/`（可选）
4. 开始新 Sprint

---

## 💡 最佳实践

1. **保持文件精简** - 每个文件控制在合理大小
2. **及时更新** - 不要积压更新
3. **使用相对链接** - 方便文档间跳转
4. **记录决策** - 重要决策写入 ADR

---

## 🤖 支持的智能体

| 智能体 | 支持状态 |
|--------|----------|
| Claude Code | ✅ 完全支持 |
| Cursor | ✅ 完全支持 |
| GitHub Copilot | ✅ 完全支持 |
| Windsurf | ✅ 完全支持 |
| 其他 AI 编程助手 | ✅ 通用兼容 |

---

*AgentPlan - 让 AI 智能体更好地理解和追踪你的项目*
