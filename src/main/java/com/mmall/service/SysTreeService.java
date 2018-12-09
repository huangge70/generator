package com.mmall.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mmall.dao.SysDeptMapper;
import com.mmall.dto.DeptLevelDto;
import com.mmall.model.SysDept;
import com.mmall.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class SysTreeService {
    @Resource
    private SysDeptMapper sysDeptMapper;
    public List<DeptLevelDto> deptTree(){
        //获取所有的部门信息
        List<SysDept> deptList=sysDeptMapper.getAllDept();
        List<DeptLevelDto> dtoList= Lists.newArrayList();
        for(SysDept dept:deptList){
            DeptLevelDto dto=DeptLevelDto.adapt(dept);
            dtoList.add(dto);
        }
        return deptListToTree(dtoList);
    }
    //List<DeptLevelDto> deptLevelList：所有的部门信息列表集合
    public List<DeptLevelDto> deptListToTree(List<DeptLevelDto> deptLevelList){
        if(CollectionUtils.isEmpty(deptLevelList)){
            return Lists.newArrayList();
        }
        //一个键对应多个值，将同一层级的部门信息放在同一个key下
        Multimap<String,DeptLevelDto> levelDeptMap= ArrayListMultimap.create();
        //所有没有上层的部门信息的列表集合
        List<DeptLevelDto> rootList=Lists.newArrayList();
        for(DeptLevelDto dto:deptLevelList){
            //dto.getLevel()：我们将其规定为0.?.? .. 即当前部门信息的上层依次为0-》？->?
            levelDeptMap.put(dto.getLevel(),dto);
            if(LevelUtil.ROOT.equals(dto.getLevel())){
                rootList.add(dto);
            }
        }
        //按照seq排序
        Collections.sort(rootList, new Comparator<DeptLevelDto>() {
            @Override
            public int compare(DeptLevelDto o1, DeptLevelDto o2) {
                return o1.getSeq()-o2.getSeq();
            }
        });
        transformDeptTree(rootList,LevelUtil.ROOT,levelDeptMap);
        return rootList;
    }
    //递归算法
    //level :0,  all:0.1,0.2
    //level:0.1
    //level:0.2
    //List<DeptLevelDto> deptLevelList:当前层级部门列表
    //String level"当前要搜索什么下的子部门,格式即为0.?.? ..
    //Multimap<String,DeptLevelDto> levelDeptMap:所有部门，且同一层级的在同一个key下保存
    public void transformDeptTree(List<DeptLevelDto> deptLevelList,String level,Multimap<String,DeptLevelDto> levelDeptMap){
        for(int i=0;i<deptLevelList.size();i++){
            //遍历该层每个元素
            DeptLevelDto deptLevelDto=deptLevelList.get(i);
            //处理当前层级
            String nextLevel=LevelUtil.calculateLevel(level,deptLevelDto.getId());
            //处理下一层
            List<DeptLevelDto> tempDeptList= (List<DeptLevelDto>) levelDeptMap.get(nextLevel);
            if(CollectionUtils.isNotEmpty(tempDeptList)){
                //排序
                Collections.sort(tempDeptList,deptSeqComparator);
                //设置下一层
                deptLevelDto.setDeptList(tempDeptList);
                //进入下一层处理
                transformDeptTree(tempDeptList,nextLevel,levelDeptMap);
            }
        }
    }
    public Comparator<DeptLevelDto> deptSeqComparator=new Comparator<DeptLevelDto>() {
        @Override
        public int compare(DeptLevelDto o1, DeptLevelDto o2) {
            return o1.getSeq()-o2.getSeq();
        }
    };
}
