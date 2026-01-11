# Claude Code 快速上手指南

> 本文档专为 Claude Code 设计，帮助快速理解项目和工作流程。

---

## 🚀 启动检查清单

每次开始工作前，按顺序执行：

1. **读取索引** → `project-tracking/INDEX.md`
2. **读取当前 Sprint** → `project-tracking/status/current-sprint.md`
3. **如果有进行中任务** → `project-tracking/status/active-tasks.md`

---

## 📂 项目结构速查

```
{{PROJECT_STRUCTURE}}
```

---

## 🔧 常用操作

### 1. 查看任务详情
```
读取: project-tracking/status/active-tasks.md#T-{任务ID}
```

### 2. 查看模块设计
```
读取: project-tracking/modules/{模块名}.md
```

### 3. 查看设计文档
```
读取: docs/{设计文档}.md#{章节}
```

---

## ✏️ 更新进度规则

### 何时更新
- ✅ 完成一个子任务后
- ✅ 完成一个完整任务后
- ✅ 发现新问题/阻塞后
- ✅ 开始新任务前

### 更新流程
1. **询问用户**：「是否需要我更新进度追踪文档？」
2. **用户确认后**，更新以下文件：
   - `status/active-tasks.md` - 更新任务状态和子任务
   - `status/current-sprint.md` - 更新完成列表
   - `INDEX.md` - 更新完成度百分比
3. **如果任务完成**：
   - 从 `active-tasks.md` 移除
   - 添加到 `current-sprint.md` 的已完成列表

### 更新格式示例

**询问用户：**
```
我已完成 T-001 的 xxx 子任务。
是否需要我更新进度追踪文档？
- 更新 active-tasks.md（勾选子任务，进度 50% → 60%）
- 更新 INDEX.md（总体完成度）
```

---

## 📊 状态符号说明

| 符号 | 含义 | 使用场景 |
|------|------|---------|
| ✅ | 已完成 | 任务/子任务完成 |
| 🔄 | 进行中 | 正在开发 |
| 📅 | 已计划 | 在 Sprint 中但未开始 |
| ❌ | 未开始 | 在 Backlog 中 |
| ⚠️ | 有问题 | 部分完成或有风险 |
| 🔒 | 已阻塞 | 等待外部依赖 |
| 🔴 | 高优先级 | P0 任务 |
| 🟡 | 中优先级 | P1 任务 |
| 🟢 | 低优先级 | P2 任务 |

---

## 🎯 任务优先级

| 级别 | 含义 | 处理方式 |
|------|------|---------|
| P0 | 必须完成 | 立即处理，阻塞发布 |
| P1 | 重要 | 本 Sprint 内完成 |
| P2 | 可选 | 有空时处理 |

---

## ⚠️ 重要约束

### active-tasks.md 规则
- **最多 10 个任务**
- 超过时，优先完成现有任务或移回 backlog
- 每个任务必须有明确的子任务列表

### 文件大小控制
- INDEX.md: < 300 tokens
- current-sprint.md: < 800 tokens
- active-tasks.md: < 1500 tokens（10 个任务）
- modules/*.md: < 1500 tokens/文件

### 更新频率
- INDEX.md: 每次任务状态变化
- active-tasks.md: 每次子任务完成
- current-sprint.md: 每日更新
- modules/*.md: 模块开发时更新

---

## 💡 常见问题

### Q: 不确定任务属于哪个模块？
→ 查看 `INDEX.md` 的模块导航，或询问用户

### Q: 发现设计文档和实现不一致？
→ 记录到对应 `modules/*.md` 的「已知问题」部分

### Q: 任务依赖其他任务怎么办？
→ 在任务详情中标注「依赖」字段，优先完成依赖任务

### Q: 需要创建新模块文档？
→ 在 `modules/` 下创建，并更新 `INDEX.md` 的导航

---

*此文档供 Claude Code 参考，人类开发者也可阅读*
