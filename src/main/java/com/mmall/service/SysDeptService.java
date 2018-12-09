package com.mmall.service;

import com.mmall.dao.SysDeptMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysDept;
import com.mmall.param.DeptParam;
import com.mmall.util.BeanValidator;
import com.mmall.util.LevelUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class SysDeptService {
    @Resource
    private SysDeptMapper sysDeptMapper;
    public void save(DeptParam param){
        BeanValidator.check(param);
        if(ckeckExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("该部门已存在");
        }
        SysDept dept=SysDept.builder().name(param.getName()).parentId(param.getParentId())
                     .seq(param.getSeq()).remark(param.getRemark()).build();
        dept.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        dept.setOperator("system");//TODO
        dept.setOperateIp("127.0.0.1");//TODO
        dept.setOperateTime(new Date());
        sysDeptMapper.insertSelective(dept);
    }
    private boolean ckeckExist(Integer parentId,String deptName,Integer deptId){
        //todo
        return true;
    }
    private String getLevel(Integer deptId){
        SysDept dept=sysDeptMapper.selectByPrimaryKey(deptId);
        if(dept==null){
            return null;
        }
        return dept.getLevel();
    }
}
