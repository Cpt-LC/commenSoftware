package com.lianzheng.notarization.master.service.Impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianzheng.notarization.master.dao.NotarizationMattersQuestionDao;
import com.lianzheng.notarization.master.entity.NotarizationMattersQuestionEntity;
import com.lianzheng.notarization.master.service.NotarizationMattersQuestionService;
import org.springframework.stereotype.Service;

@Service
@DS("h5")
public class NotarizationMattersQuestionServiceImpl  extends ServiceImpl<NotarizationMattersQuestionDao, NotarizationMattersQuestionEntity> implements NotarizationMattersQuestionService {

}
