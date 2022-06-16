package com.lianzheng.h5.service;

import com.lianzheng.h5.dto.CheckFaceDTO;

import java.util.Map;

/**
 * @author kk
 * @date 2022/6/13 22:27
 * @describe
 * @remark
 */
public interface IOcrService {

    /**
     * 人脸比对
     */
    Map<String,Object> checkFace(CheckFaceDTO checkFaceDTO) throws Exception;

}
