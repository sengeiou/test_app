package cn.bevol.statics.entity.user;

import cn.bevol.statics.entity.BaseMessage;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 消息
 *
 * @author hualong
 */
@Document(collection = "message")
public class Message  extends BaseMessage {
	
	
}
