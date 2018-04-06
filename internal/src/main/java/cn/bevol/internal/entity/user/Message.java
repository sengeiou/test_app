package cn.bevol.internal.entity.user;

import cn.bevol.model.entity.BaseMessage;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 消息
 *
 * @author hualong
 */
@Document(collection = "message")
public class Message  extends BaseMessage {
	
	
}
