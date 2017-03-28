package com.danielmiao.service;

import com.danielmiao.repository.MemberBaseRepository;
import com.danielmiao.repository.domain.MemberBaseEntity;
import com.danielmiao.route.annotation.Split;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created by danielmiao on 2017/3/28.
 */
@Service
public class MemberBaseService {

    @Autowired
    private MemberBaseRepository memberBaseRepository;

    @Split
    @Transactional
    public void save(MemberBaseEntity memberBaseEntity){
        this.memberBaseRepository.save(memberBaseEntity);
    }
}
