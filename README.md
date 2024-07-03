## 更适合中国宝宝体质的ai！

模组下载: [https://modrinth.com/mod/minekimi](https://modrinth.com/mod/minegpt)

本模组添加了kimi命令，可以调用kimi api进行对话，最近kimi api开放，做QQ机器人之余有这个想法。于是搜了一下，发现了MineGPT并参考了一下（不然差点把hutool塞进去）
![show](https://cdn.modrinth.com/data/cached_images/d9eb43654b632783409e2ce361757fd1f307322c.png)
添加了按键绑定（默认K，可以在按键绑定中修改），按下后可以快进到直接输对话内容。

如果你只想为自己使用，可以下载client_only版本（它是专为客户端设计的，服务端无需添加）

启动后在config文件夹里把api key换成你自己的即可食用，还可以修改背景设定和名字哦~

MineGPT仓库: [https://github.com/MCTeamPotato/MineGPT/tree/arch-1.20](https://github.com/MCTeamPotato/MineGPT/tree/arch-1.20)

MineGPT下载: [https://modrinth.com/mod/minegpt](https://modrinth.com/mod/minegpt)

白嫖Kimi api key:
[https://platform.moonshot.cn/console](https://platform.moonshot.cn/console)

Kimi 官方文档: [https://platform.moonshot.cn/docs/api-reference](https://platform.moonshot.cn/docs/api-reference)


### config说明：

**temperature:** 使用什么采样温度，介于 0 和 1 之间。较高的值（如 0.7）将使输出更加随机，而较低的值（如 0.2）将使其更加集中和确定性

**size:** 记录的最大对话长度（做了简单的轮式对话）

**system:** 对话的初始背景等设定

**name:** 游戏里显示的名字

**broadcast:** 多人游戏时是否广播你与kimi的对话（仅限服务端）