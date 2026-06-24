package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Override
    public void insert(AddressBook addressBook) {
        addressBookMapper.insert(addressBook);
    }

    @Override
    public List<AddressBook> selectAll() {
        List<AddressBook> addressBookList=addressBookMapper.selectAll(BaseContext.getCurrentId());
        return addressBookList;
    }

    @Override
    public AddressBook selectDefault() {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook=addressBookMapper.selectDefault(userId,1);
        return addressBook;
    }

    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    @Override
    public void delete(Long id) {
        addressBookMapper.delete(id);
    }

    @Override
    public AddressBook getById(Long id) {
        return addressBookMapper.getById(id);
    }

    @Override
    public void setDefault(AddressBook addressBook) {
       List<AddressBook> addressBooks=addressBookMapper.selectAll(BaseContext.getCurrentId());
       for(AddressBook addressBook1:addressBooks){
           addressBook1.setIsDefault(0);
           addressBookMapper.update(addressBook1);
       }
        addressBookMapper.setDefault(addressBook);
    }
}


