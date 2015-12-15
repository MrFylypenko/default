package com.todolist.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by vano on 14.12.15.
 */


@Entity
@Table (name = "persistent_logins")
public class PersistentLogin {

    /**
     * Эта сущность нужна для remember-me, поэтому ее не трогаем! и не изменяем!!
     *
     * */

    public PersistentLogin(){
    }


    @Column(name = "username")
    public String username1;

    @Id
    @Column(name = "series")
    public String series;

    @Column(name = "token")
    public String token;

    @Column(name = "last_used")
    public Date last_used;




}
