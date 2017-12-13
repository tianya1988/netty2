/*
 * Copyright 2013-2018 Lilinfeng.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.phei.netty.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author lilinfeng
 * @version 1.0
 * @date 2014年2月16日
 */
public class AcceptCompletionHandler implements
        CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler> {


    /**
     * 当调用asynchronousServerSocketChannel的accept方法后,如果有新的客户端连接接入,
     * 系统将回调我们传入的CompletionHandler实例的completed方法,表示新的客户端已经成功接入.
     * 因为一个asynchronousServerSocketChannel可以接收成千上万个客户端,所以需要继续调用它的accept方法,
     * 接收其他的客户端连接,最终形成一个循环.
     * @param result  当完成新的连接接入 其结果就是AsynchronousSocketChannel
     * @param attachment
     */
    @Override
    public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {
        attachment.asynchronousServerSocketChannel.accept(attachment, this);
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        /**
         * 1. 异步读操作: 从这个通道读取一个字节序列到给定的缓冲区中
         * 2. 如果AsynchronousSocketChannel中无数据可读,ReadCompletionHandler(result)读取操作立即结束，
         *    ReadCompletionHandler的completed中的入参result为0而不启动I/O操作
         *
         */
        result.read(buffer, buffer, new ReadCompletionHandler(result));
    }

    @Override
    public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
        exc.printStackTrace();
        attachment.latch.countDown();
    }

}
