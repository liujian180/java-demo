### RabbitMq
#### 相关概念
1. 队列服务, 会有三个概念： 发消息者、队列、收消息者，RabbitMQ 在这个基本
概念之上, 多做了一层抽象, 在发消息者和 队列之间, 加入了交换器 (Exchange).
 这样发消息者和队列就没有直接联系, 转而变成发消息者把消息给交换器, 
 交换器根据调度策略再把消息再给队列。
2. 虚拟主机，交换机，队列，和绑定。
 ```
虚拟主机：一个虚拟主机持有一组交换机、队列和绑定。为什么需要多个虚拟主机呢？
很简单， RabbitMQ 当中，用户只能在虚拟主机的粒度进行权限控制。 因此，如果需
要禁止A组访问B组的交换机/队列/绑定，必须为A和B分别创建一个虚拟主机。每一个 
RabbitMQ 服务器都有一个默认的虚拟主机“/”。

交换机：Exchange 用于转发消息，但是它不会做存储 ，如果没有 Queue bind 到 
Exchange 的话，它会直接丢弃掉 Producer 发送过来的消息。 这里有一个比较重要
的概念：路由键 。消息到交换机的时候，交互机会转发到对应的队列中，那么究竟
转发到哪个队列，就要根据该路由键。

绑定：也就是交换机需要和队列相绑定，这其中如上图所示，是多对多的关系。

Sender 可以将一个消息直接发送到一个queue;然后Receiver直接消费该队列里的消息
Sender 可以将消息放到一个交换机中；交换机通过绑定的routing key 进行路由到指定的
队列中；然后Receiver 消费该队列中的消息；
其中的交换机可以有完全匹配路由模式交换机；根据#，* 进行模糊匹配交换机；直接广播式
到所有绑定到该交换机的所有队列交换机。
```
3. Queue 
```
queue：queue的名称

exclusive：排他队列，如果一个队列被声明为排他队列，该队列仅对首次申明它的连接可见，
并在连接断开时自动删除。这里需要注意三点：1. 排他队列是基于连接可见的，同一连接的不
同信道是可以同时访问同一连接创建的排他队列；2.“首次”，如果一个连接已经声明了一个
排他队列，其他连接是不允许建立同名的排他队列的，这个与普通队列不同；3.即使该队列是
持久化的，一旦连接关闭或者客户端退出，该排他队列都会被自动删除的，这种队列适用于
一个客户端发送读取消息的应用场景。

autoDelete：自动删除，如果该队列没有任何订阅的消费者的话，该队列会被自动删除。这种
队列适用于临时队列。

durable: 表示队列持久化


```
4. Exchange
```
4.1 DirectExchange
完全根据 routing key进行投递

4.2 TopicExchange
对key进行模式匹配后进行投递；，符号”#”匹配一个或多个词，符号”*”匹配正好一个词

4.3 FanoutExchange
广播模式，一个消息进来时，投递到与该交换机绑定的所有队列

```
5. 虚拟主机
```
需要主动创建后才可使用

```

6. 对象序列化
```
1. 自定义一个ClassMapper : 解决包信任问题
2. 自定义一个Converter : 解决序列化和反序列化问题
3. 注入定义的ClassMapper ,Converter 到ListenerFactory 和RabbitTemplate

```

7. callback
````
1. 配置pushlish-confirms
2. 设置template.callback


````
8. admin 功能（todo）
```

```


#### boot-admin-demo
1. 主要是对admin的操作
2. 默认的rabbitmq 配置使用
#### spring-boot-demo
1. 使用自定义配置进行处理
#### spring-cloud-demo
1. 基于spring cloud 封装使用 

