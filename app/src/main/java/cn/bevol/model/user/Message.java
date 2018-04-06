package cn.bevol.model.user;

import cn.bevol.model.BaseMessage;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 消息
 *
 * @author hualong
 */
@Document(collection = "message")
public class Message  extends BaseMessage {
	
	
}
