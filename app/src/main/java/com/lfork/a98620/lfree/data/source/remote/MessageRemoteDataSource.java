package com.lfork.a98620.lfree.data.source.remote;

import android.util.Log;
import android.view.View;

import com.lfork.a98620.lfree.data.source.MessageDataRepository;
import com.lfork.a98620.lfree.data.source.MessageDataSource;
import com.lfork.a98620.lfree.data.source.remote.imservice.Config;
import com.lfork.a98620.lfree.data.source.remote.imservice.MessageListener;
import com.lfork.a98620.lfree.data.source.remote.imservice.UDPConnection;
import com.lfork.a98620.lfree.data.source.remote.imservice.UDPMessageMaid;
import com.lfork.a98620.lfree.data.entity.message.Message;
import com.lfork.a98620.lfree.data.entity.message.MessageContentType;
import com.lfork.a98620.lfree.data.entity.message.MessageStatus;

import java.util.LinkedList;
import java.util.List;

public class MessageRemoteDataSource implements MessageDataSource {
//    private List<Message> messageSendQueue;   //发送失败的消息是需要重新发送的。。。  这个交给用户来处理

    private List<Message> messageReceiveQueue;

    private static MessageRemoteDataSource INSTANCE;  //负责消息的发送控制，和桥梁作用(连接View前端和数据后台)

    private UDPConnection mConnection;  //负责提供UDP的基本服务 + 负责消息的接收和直接储存(内存)

    private UDPMessageMaid messageMaid; //负责对直接储存消息的处理，然后进行分类储存(磁盘，数据库)，推送到view界面

    private MessageDataRepository mRepository;

//    private View view; //这里还需要一个View的引用和主线程的引用，以便将新消息推送到前台

    private int userId = -1;

    private MessageListener listener;

    private MessageRemoteDataSource(int userId) {

        this.userId = userId;

        //获取客户端信息
//        IMDataRepository.getInstance(null);
//        User u = new User();
//        u.setUsername("admin");
//        u.setId(1);

        //初始化消息接收队列(储存处)
        messageReceiveQueue = new LinkedList<>();

        //消息接收与储存 + 提供消息发送接口
        mConnection = new UDPConnection(userId, Config.URL, 7010, messageReceiveQueue);
        mConnection.start();

        //消息处理
        messageMaid = new UDPMessageMaid(this, messageReceiveQueue);
        messageMaid.start();
    }

    public static MessageRemoteDataSource getInstance(int userId) {
        if (INSTANCE == null) {
            INSTANCE = new MessageRemoteDataSource(userId);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        if (INSTANCE != null && INSTANCE.mConnection != null){
            INSTANCE.mConnection.closeConnection();
        }

        INSTANCE = null;
    }

    public void setRepository(MessageDataRepository mRepository) {
        this.mRepository = mRepository;
    }

    @Override
    public void getMessages(int id, MessageContentType type, GeneralCallback<List<Message>> callback) {
    }

    @Override
    public void setViewReference(View view) {

    }

//    @Override
//    public void setViewReference(View view) {
//        this.view = view;
//    }

    /**
     * deal normal message
     *
     * @param msg
     */

    public void refreshView(Message msg) {
        Log.d("接收到的消息", "refreshView: " + msg);
//        if (view != null)
////            view.refreshMessage(msg);

        if (listener != null) {
            listener.onReceived(msg);
        }
    }


    /**
     * deal command
     */
    @Override
    public void dealCommand() {

    }

    /**
     * deal notification
     */
    @Override
    public void dealNotification() {

    }

    /**
     * 暂时只写发送的操作
     *
     * @param msg      需要发送的消息
     * @param callback 回调
     */
    @Override
    public synchronized void saveAndSendMessage(Message msg, GeneralCallback<Message> callback) {

        int repeatTimes = 3;
        boolean succeed = false;

        while (repeatTimes > 0 && !succeed) {       //这里需要进行3次重发，如果多次发送失败那么最后就交给用户来处理发送失败的信息 2018年3月8日16:28:01
            repeatTimes--;

            if (repeatTimes == 1) {
                mConnection.rebuildConnection();
            }
            succeed = mConnection.sendNormalMessage(msg);

            Log.d("发送结果", "saveAndSendMessage: " + succeed);
        }

        if (succeed) {
            callback.succeed(msg);
            msg.setStatus(MessageStatus.SENT);
        } else {
            callback.failed("发送失败");
            msg.setStatus(MessageStatus.SENT_FAILED);
        }

    }

    @Override
    public void clearMessages(int id, MessageContentType type) {
        // local work
    }

    @Override
    public void addMessage(Message msg) {

    }


    private void refreshLocalMessageData(String msg) {
        mRepository.refreshLocalMessageData(msg);
    }
//
//    public View getView() {
//        return view;
//    }

    public void setMessageListener(MessageListener listener){
        this.listener = listener;
    }

    public MessageListener getListener() {
        return listener;
    }
}
