package com.lfork.a98620.lfree.data.imdata.local;

import com.lfork.a98620.lfree.data.imdata.MessageDataSource;
import com.lfork.a98620.lfree.imservice.MessageListener;
import com.lfork.a98620.lfree.imservice.message.Message;
import com.lfork.a98620.lfree.imservice.message.MessageContentType;

import org.litepal.crud.DataSupport;
import org.litepal.crud.callback.FindMultiCallback;
import org.litepal.crud.callback.SaveCallback;

import java.util.List;

public class MessageLocalDataSource implements MessageDataSource {

    private static MessageLocalDataSource INSTANCE;

    public static MessageLocalDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MessageLocalDataSource();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void getMessages(int id, MessageContentType type, GeneralCallback<List<Message>> callback) {
        DataSupport.where("receiverid=? or senderid=?" ,id+"", id+"").order("messageID")  //messageId 实际上是message生成的时间 System.currentTimeMillis()
                .findAsync(Message.class)
                .listen(new FindMultiCallback() {
                    @Override
                    public <T> void onFinish(List<T> t) {

                        if (t.size() < 1) {
                            callback.failed("没有消息记录");
                        } else {
                            callback.succeed((List<Message>) t);
                        }
                    }
                });


    }

    @Override
    public void setMessageListener(MessageListener listener) {
        //消息监听交给 remote进行处理
    }


    @Override
    public void saveAndSendMessage(Message msg, GeneralCallback<Message> callback) {
        msg.saveAsync().listen(new SaveCallback() {
            @Override
            public void onFinish(boolean success) {
                if (success) {
                    callback.succeed(null);
                } else {
                    callback.failed("消息重复");
                }
            }
        });

    }

    @Override
    public void clearMessages(int id, MessageContentType type) {

    }

}