### 控制台聊天室使用说明
- 运行`SimpleChatServer`的`main`方法，启动netty服务端
- 运行`SimpleChatClient`的`main`方法，启动netty客户端，可以启动多个客户端，观察server端控制台的输出
- 在其中一个客户端的控制台打印信息，观察所有客户端的控制台输出


### 浏览器聊天室使用说明
- 运行`WebSocketChatServer`的`main`方法，启动netty服务端
- 以浏览器打开`resources`文件夹下的`WebSocketChatClient.html`文件，启动netty客户端，启动多个，发送消息，观察效果。     

演示了netty使用webSocket协议，从而让web端开发集成进来
