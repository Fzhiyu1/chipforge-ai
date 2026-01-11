# /forge - ChipForge-AI FPGA 开发助手

## 描述
AI 驱动的 FPGA 开发工具，提供 Verilog 开发辅助功能。

## 使用方法
```
/forge [command] [options]
```

## 可用命令

### 知识查询
- `/forge query <topic>` - 查询 Verilog 语法、设计模式、最佳实践

### 仿真工具
- `/forge sim <file>` - 运行仿真 (ModelSim/Icarus Verilog)
- `/forge sim --tool <tool>` - 指定仿真工具

### 波形分析
- `/forge wave <vcd_file>` - 分析 VCD 波形文件
- `/forge wave --check` - 检测信号异常

### 综合工具
- `/forge synth <project>` - 运行综合 (Vivado/Quartus)
- `/forge synth --target <fpga>` - 指定目标 FPGA

### 项目管理
- `/forge init <name>` - 初始化新 FPGA 项目
- `/forge status` - 查看项目状态

## 示例

```bash
# 查询 FSM 设计模式
/forge query fsm

# 运行仿真
/forge sim top_module.v

# 分析波形
/forge wave output.vcd
```

## 开发状态
当前版本：v0.1.0-SNAPSHOT (开发中)
