package cn.bevol.model.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import cn.bevol.model.Base;
import cn.bevol.model.BaseMessage;

/**
 * 消息
 *
 * @author hualong
 */
@Document(collection = "message")
public class Message  extends BaseMessage{
	
	
}
