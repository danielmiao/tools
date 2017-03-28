package com.danielmiao.repository.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by danielmiao on 2016/3/28.
 */
@Entity
@Table(name="baseinfo")
public class MemberBaseEntity {

    @Id
    @Column(name = "uid")
    private Integer id;

    @Column(name = "nick",length = 64)
    private String nick;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
