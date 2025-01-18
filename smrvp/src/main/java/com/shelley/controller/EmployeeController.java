package com.shelley.controller;

import com.shelley.entity.Employee;
import com.shelley.service.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("emp")
@CrossOrigin
@Slf4j
public class EmployeeController {

    @Autowired
    private IEmployeeService iEmployeeService;

    @Value("${photo.dir}")
    private String realpath;

    @GetMapping("findOne/{id}")
    public Employee findOne(@PathVariable("id") Integer id){
        log.info("根据id查询员工信息，id：[{}]",id);
        return iEmployeeService.findOne(id);
    }

    /**
     * update
     * @param employee
     * @param photo
     * @return
     */

    @PutMapping("update")
    public Map<String, Object> update(Employee employee, /*@RequestParam(value = "photo", required = false)*/ MultipartFile photo){
        Map<String, Object> map = new HashMap<>();
        try {
            if (photo != null && photo.getSize() >0){
                //头像保存
                String newfilename = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(photo.getOriginalFilename());
                photo.transferTo(new File(realpath,newfilename));
                //设置上传地址
                employee.setPath(newfilename);
            }
            //保存员工信息
            iEmployeeService.update(employee);
            map.put("state",true);
            map.put("msg","员工信息保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("state",false);
            map.put("msg","员工信息保存失败");
        }
        return map;
    }

    @DeleteMapping("delete/{id}")
    public Map<String, Object> delete(@PathVariable("id")Integer id){
        log.info("删除的员工的id为：[{}]",id);
        Map<String, Object> map = new HashMap<>();
        try {
            Employee emp = iEmployeeService.findOne(id);
            iEmployeeService.delete(id);
            map.put("state",true);
            map.put("msg","删除成功");

            //删除员工的头像的实体文件
            File file = new File(realpath,emp.getPath());
            if (file.exists()){
                file.delete();
            }


        } catch (Exception e) {
            e.printStackTrace();
            map.put("state",false);
            map.put("msg","删除失败");
        }
        return map;
    }


    @PostMapping("save")
    public Map<String, Object> save(Employee employee, MultipartFile photo) throws IOException {
        log.info("员工信息:[{}]",employee.toString());
        log.info("头像信息:[{}]",photo.getOriginalFilename());
        Map<String, Object> map = new HashMap<>();
        try {
            //头像保存
            //处理文件名
            String newfilename = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(photo.getOriginalFilename());
            photo.transferTo(new File(realpath,newfilename));
            //设置上传地址
            employee.setPath(newfilename);
            //保存员工信息
            iEmployeeService.save(employee);
            map.put("state",true);
            map.put("msg","员工信息保存成功");
        } catch (IOException e) {
            //删除已经传过去的文件
            //删除数据库中存储的信息
            e.printStackTrace();
            map.put("state",false);
            map.put("msg","员工信息保存失败");
        }
        return map;

    }

    @GetMapping("findAll")
    public List<Employee> findAll(){
        iEmployeeService.findAll();

        return iEmployeeService.findAll();
    }

}
