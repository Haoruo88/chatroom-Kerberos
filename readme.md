# 基于Kerberos协议的公共聊天室(基于JAVA语言实现)

| 项目          | 说明                   |
| ------------- | ---------------------- |
| MessageServer | 服务器                 |
| Client        | 客户端                 |
| TGS           | Ticket Grantion Server |
| AS            | Authentication Server  |

整个Kerberos系统由四部分组成：AS，TGS，Client，Server。当客户端请求服务时，首先向认证服务器AS请求票据许可票据，然后将其交给票据许可服务器TGS进行验证，验证通过后为客户端分配服务许可票据，最后将票据交给Server进行验证，验证通过后，即可获得服务。
本项目使用Java语言，完整模拟实现Kerberos协议认证全流程，并附加实现了简易的群聊功能，主机间使用Socket进行通信。

* 使用DES算法对报文进行对称加密，防止窃听
* 使用RSA算法对消息摘要进行非对称加密生成数字签名，保证了消息的完整性和不可否认性
