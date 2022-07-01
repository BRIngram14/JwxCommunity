package com.jwx.community.entity;

import java.util.HashMap;
import java.util.Map;

public class Event {
    private String topic;//事件主题
    private int userId;//事件触发人
    private int entityType;//实体类型
    private int entityId;//实体Id
    private int entityUserId;//实体的作者
    private Map<String,Object> data=new HashMap<>();//其余的数据放在map中

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
        //这样写可以set完一个属性后又返回当前对象 又可以继续.set 更方便
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key,Object values) {
        this.data.put(key, values);//一条一条 以键值对的方式传数据 不是直接传一个map进来
        return this;
    }
}
