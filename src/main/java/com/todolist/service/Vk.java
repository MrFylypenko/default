package com.todolist.service;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by vano on 09.12.15.
 */
public interface Vk {

    public String getToken (String code)throws Exception;




}
