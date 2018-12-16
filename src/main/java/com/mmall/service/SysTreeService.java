package com.mmall.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mmall.dao.SysAclMapper;
import com.mmall.dao.SysAclModuleMapper;
import com.mmall.dao.SysDeptMapper;
import com.mmall.dto.AclDto;
import com.mmall.dto.AclModuleLevelDto;
import com.mmall.dto.DeptLevelDto;
import com.mmall.model.SysAcl;
import com.mmall.model.SysAclModule;
import com.mmall.model.SysDept;
import com.mmall.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysTreeService {
    @Resource
    private SysDeptMapper sysDeptMapper;
    @Resource
    private SysAclModuleMapper sysAclModuleMapper;
    @Resource
    private SysCoreService sysCoreService;
    @Resource
    private SysAclMapper sysAclMapper;

    public List<AclModuleLevelDto> userAclTree(int userId){
        List<SysAcl> userAclList=sysCoreService.getUserAclList(userId);
        List<AclDto> aclDtoList=Lists.newArrayList();
        for(SysAcl acl:userAclList){
            AclDto dto=AclDto.adapt(acl);
            dto.setChecked(true);
            dto.setHasAcl(true);
            aclDtoList.add(dto);
        }
        return aclListToTree(aclDtoList);
    }
    public List<AclModuleLevelDto> roleTree(int roleId){
        //取出当前用户已分配的权限点
        List<SysAcl> userAclList=sysCoreService.getCurrentUserAclList();
        //取出角色已分配的权限点
        List<SysAcl> roleAclList=sysCoreService.getRoleAclList(roleId);
        //相当于遍历userAclList，并把集合中每个对象的id属性取出来放入set集合中
        Set<Integer> userAclIdSet=userAclList.stream().map(sysAcl->sysAcl.getId()).collect(Collectors.toSet());
        Set<Integer> roleAclIdSet=roleAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());

        List<SysAcl> allAclList=sysAclMapper.getAll();
        //当前系统所有的权限点
        List<AclDto> aclDtoList=Lists.newArrayList();
        for(SysAcl acl:allAclList){
            AclDto dto=AclDto.adapt(acl);
            if(userAclIdSet.contains(acl.getId())){
                dto.setHasAcl(true);
            }
            if(roleAclIdSet.contains(acl.getId())){
                dto.setChecked(true);
            }
            aclDtoList.add(dto);
        }
        return aclListToTree(aclDtoList);
    }
    public List<AclModuleLevelDto> aclListToTree(List<AclDto> aclDtoList){
        if(CollectionUtils.isEmpty(aclDtoList)){
            return Lists.newArrayList();
        }
        List<AclModuleLevelDto> aclModuleLevelList=aclModuleTree();
        Multimap<Integer,AclDto> moduleIdAclMap=ArrayListMultimap.create();
        for(SysAcl acl:aclDtoList){
            if(acl.getStatus()==1){
                moduleIdAclMap.put(acl.getAclModuleId(),AclDto.adapt(acl));
            }
        }
        bindAclsWithOrder(aclModuleLevelList,moduleIdAclMap);
        return aclModuleLevelList;
    }
    public void bindAclsWithOrder(List<AclModuleLevelDto> aclModuleLevelList,Multimap<Integer,AclDto> moduleIdAclMap){
        if(CollectionUtils.isEmpty(aclModuleLevelList)){
           return;
        }
        for(AclModuleLevelDto dto:aclModuleLevelList){
            List<AclDto> aclDtoList= (List<AclDto>) moduleIdAclMap.get(dto.getId());
            if(CollectionUtils.isNotEmpty(aclDtoList)){
                Collections.sort(aclDtoList,aclSeqComparator);
                dto.setAclList(aclDtoList);
            }
            bindAclsWithOrder(dto.getAclModuleList(),moduleIdAclMap);
        }
    }
    public List<AclModuleLevelDto> aclModuleTree() {
        List<SysAclModule> aclModuleList = sysAclModuleMapper.getAllAclModule();
        List<AclModuleLevelDto> dtoList = Lists.newArrayList();
        for (SysAclModule aclModule : aclModuleList) {
            dtoList.add(AclModuleLevelDto.adapt(aclModule));
        }
        return aclModuleListToTree(dtoList);
    }
    public List<AclModuleLevelDto> aclModuleListToTree(List<AclModuleLevelDto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return Lists.newArrayList();
        }
        // level -> [aclmodule1, aclmodule2, ...] Map<String, List<Object>>
        Multimap<String, AclModuleLevelDto> levelAclModuleMap = ArrayListMultimap.create();
        List<AclModuleLevelDto> rootList = Lists.newArrayList();

        for (AclModuleLevelDto dto : dtoList) {
            levelAclModuleMap.put(dto.getLevel(), dto);
            if (LevelUtil.ROOT.equals(dto.getLevel())) {
                rootList.add(dto);
            }
        }
        Collections.sort(rootList, aclModuleSeqComparator);
        transformAclModuleTree(rootList, LevelUtil.ROOT, levelAclModuleMap);
        return rootList;
    }
    public void transformAclModuleTree(List<AclModuleLevelDto> dtoList, String level, Multimap<String, AclModuleLevelDto> levelAclModuleMap) {
        for (int i = 0; i < dtoList.size(); i++) {
            AclModuleLevelDto dto = dtoList.get(i);
            String nextLevel = LevelUtil.calculateLevel(level, dto.getId());
            List<AclModuleLevelDto> tempList = (List<AclModuleLevelDto>) levelAclModuleMap.get(nextLevel);
            if (CollectionUtils.isNotEmpty(tempList)) {
                Collections.sort(tempList, aclModuleSeqComparator);
                dto.setAclModuleList(tempList);
                transformAclModuleTree(tempList, nextLevel, levelAclModuleMap);
            }
        }
    }
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
    public Comparator<AclDto> aclSeqComparator=new Comparator<AclDto>() {
        @Override
        public int compare(AclDto a1, AclDto a2) {
            return a1.getSeq()-a2.getSeq();
        }
    };
    public Comparator<AclModuleLevelDto> aclModuleSeqComparator=new Comparator<AclModuleLevelDto>() {
        @Override
        public int compare(AclModuleLevelDto a1, AclModuleLevelDto a2) {
            return a1.getSeq()-a2.getSeq();
        }
    };
}
