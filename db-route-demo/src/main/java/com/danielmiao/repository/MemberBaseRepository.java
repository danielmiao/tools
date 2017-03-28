package com.danielmiao.repository;

import com.danielmiao.repository.domain.MemberBaseEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by danielmiao on 2016/3/28.
 */
public interface MemberBaseRepository extends CrudRepository<MemberBaseEntity,Integer>{
}
