package com.wujt.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import static com.gow.domain.NioConfig.PORT;

/**
 * ServerSocketChannel 服务端套接字通道
 * public static ServerSocketChannel open() 创建服务端套接字
 * public final SelectableChannel configureBlocking(boolean block) 设置套接字通道为非阻塞
 * public ServerSocket socket() 获取服务端套接字
 * public final SelectionKey register(Selector sel, int ops,Object att)
 *
 * @author wujt
 */
public class NioServerSocketChannelDemo {
    private Selector selector;          //创建一个选择器
    private final static int BUF_SIZE = 10240;

    private void initServer() throws IOException {
        //创建通道管理器对象selector
        this.selector = Selector.open();

        //创建一个通道对象ServerSocketChannel
        ServerSocketChannel channel = ServerSocketChannel.open();
        //将通道设置为非阻塞
        channel.configureBlocking(false);
        //将通道绑定在8686端口
        channel.socket().bind(new InetSocketAddress(PORT));

        //将上述的通道管理器和通道绑定，并为该通道注册OP_ACCEPT事件
        //注册事件后，当该事件到达时，selector.select()会返回（一个key），如果该事件没到达selector.select()会一直阻塞
        // 并且会将Channel attach到selectionKey
        SelectionKey selectionKey = channel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            //一直等待直到有数据可读，返回值是key的数量（可以有多个）或者超时返回
            int select = selector.select(10);
            Set<SelectionKey> keys = selector.selectedKeys();//如果channel有数据了，将生成的key访入keys集合中
            Iterator iterator = keys.iterator();        //得到这个keys集合的迭代器
            while (iterator.hasNext()) {             //使用迭代器遍历集合
                SelectionKey key = (SelectionKey) iterator.next();       //得到集合中的一个key实例
                iterator.remove();          //拿到当前key实例之后记得在迭代器中将这个元素删除，非常重要，否则会出错
                if (key.isAcceptable()) {         //判断当前key所代表的channel是否在Acceptable状态，如果是就进行接收
                    doAccept(key);
                } else if (key.isReadable()) {
                    doRead(key);
                } else if (key.isWritable() && key.isValid()) {
                    doWrite(key);
                } else if (key.isConnectable()) {
                    System.out.println("连接成功！");
                }
            }

        }
    }

    public void doAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        System.out.println("ServerSocketChannel正在循环监听");
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(key.selector(), SelectionKey.OP_READ);
    }

    public void doRead(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(BUF_SIZE);
        long bytesRead = clientChannel.read(byteBuffer);
        while (bytesRead > 0) {
            byteBuffer.flip();
            byte[] data = byteBuffer.array();
            String info = new String(data).trim();
            System.out.println("从客户端发送过来的消息是：" + info);
            byteBuffer.clear();
            bytesRead = clientChannel.read(byteBuffer);
        }
        if (bytesRead == -1) {
            clientChannel.close();
        }
    }

    public void doWrite(SelectionKey key) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(BUF_SIZE);
        byteBuffer.flip();
        SocketChannel clientChannel = (SocketChannel) key.channel();
        while (byteBuffer.hasRemaining()) {
            clientChannel.write(byteBuffer);
        }
        byteBuffer.compact();
    }
}
