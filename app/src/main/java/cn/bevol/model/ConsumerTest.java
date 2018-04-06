package cn.bevol.model;

import cn.bevol.app.service.AliyunService;
import com.aliyun.openservices.ons.api.*;

import java.util.Properties;

/**
 * Created by ${chj}. on 2018-03-15.
 * 消息订阅者
 */
public class ConsumerTest {
    //开始订阅
    public void subscribe(){
        Properties properties = new Properties();
        // 您在 MQ 控制台创建的 Consumer ID
        properties.put(PropertyKeyConst.ConsumerId, "XXX");
        // 鉴权用 AccessKey，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.AccessKey, "XXX");
        // 鉴权用 SecretKey，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.SecretKey, "XXX");
        // 设置 TCP 接入域名（此处以公共云公网环境接入为例）
        properties.put(PropertyKeyConst.ONSAddr,
                "http://onsaddr-internet.aliyun.com/rocketmq/nsaddr4client-internet");
        Consumer consumer = ONSFactory.createConsumer(properties);
        consumer.subscribe("TopicTestMQ", "*", new MessageListener() {
            public Action consume(Message message, ConsumeContext context) {
                //todo 业务逻辑
                Properties userProperties= message.getUserProperties();
                AliyunService aliyunService=new AliyunService();
                aliyunService.sendVcode(userProperties.getProperty("account"),userProperties.getProperty("vcode")
                        ,Integer.parseInt(userProperties.getProperty("vcodeType")),
                        Long.parseLong(userProperties.getProperty("vcodeId")));
                System.out.println("Receive: " + message);
                return Action.CommitMessage;
            }
        });
        consumer.start();
        System.out.println("Consumer Started-----------");
    }

    public static void main(String[] args) {
        try{
            ConsumerTest consumerTest=new ConsumerTest();
            //topic模式先订阅后发布
            //消息订阅
            consumerTest.subscribe();
            //消息发布
            consumerTest.sendMsg("13469958973","1234",0,12);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void sendMsg(String phone, String vcode, int vcodeType,long vcodeId) {
        String producerId="";
        Message msg = new Message( //
                // 在控制台创建的 Topic，即该消息所属的 Topic 名称
                "TopicTestMQ",
                // Message Tag,
                // 可理解为 Gmail 中的标签，对消息进行再归类，方便 Consumer 指定过滤条件在 MQ 服务器过滤
                "TagA",
                // Message Body
                // 任何二进制形式的数据， MQ 不做任何干预，
                // 需要 Producer 与 Consumer 协商好一致的序列化和反序列化方式
                "Hello MQ".getBytes());
        // 设置代表消息的业务关键属性，请尽可能全局唯一，以方便您在无法正常收到消息情况下，可通过 MQ 控制台查询消息并补发
        // 注意：不设置也不会影响消息正常收发
        msg.setKey("ORDERID_100");
        // 发送消息，只要不抛异常就是成功
        // 打印 Message ID，以便用于消息发送状态查询
        Properties properties=new Properties();
        //account, vc.getVcode(), type,vc.getId()
        properties.setProperty("account",phone);
        properties.setProperty("vcode",vcode);
        properties.setProperty("vcodeType",String.valueOf(vcodeType));
        properties.setProperty("vcodeId",String.valueOf(vcodeId));
        msg.setUserProperties(properties);
        com.aliyun.openservices.ons.api.SendResult sendResult = ProducerTest.getProducer(producerId).send(msg);
        System.out.println("Send Message success. Message ID is: " + sendResult.getMessageId());
        // 在应用退出前，可以销毁 Producer 对象
        // 注意：如果不销毁也没有问题
        ProducerTest.getProducer(producerId).shutdown();
    }
}
