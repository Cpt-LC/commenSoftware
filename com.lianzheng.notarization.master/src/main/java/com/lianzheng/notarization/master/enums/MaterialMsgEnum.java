package com.lianzheng.notarization.master.enums;

public enum MaterialMsgEnum {
    //约定材料文字
    WTMFB01MSG("WTMFB01MSG","受托人身份材料复印件,权属证明材料（如产权证、认购书、定金收据、购房合同等）,委托事项相关材料（婚姻状况证明等）"),
    WTMFS01MSG("WTMFS01MSG","受托人身份材料复印件,权属证明材料（如产权证等其它证明材料）,委托事项相关材料（婚姻状况证明等）"),
    WTQTMSG("WTQTMSG","受托人身份材料复印件,委托事项相关材料（婚姻状况证明等）,委托事项相关材料（与一般事务利害相关）"),
    FQJCSMMSG("FQJCSMMSG","被继承人死亡证明材料,当事人与被继承人关系的证明材料,遗产权属证明（财产凭证）原件或复印件"),
    SMQTMSG("SMQTMSG","与声明事项相关的材料"),
    FDJCMSG("FDJCMSG","被继承人死亡证明材料,与被继承人有继承关系的亲属关系证明,财产权利凭证（房产证、土地证等,应当提供的其他材料"),
    CHSHMSG("CHSHMSG","医学出生证、独生子女证、公安户籍登记底档等,*父母死亡的提供死亡证明"),
    SHCMSG("SHCMSG","当事人需到场"),
    SWMSG("SWMSG","死亡证明"),
    GJMSG("GJMSG","户口簿"),
    JHMSG("JHMSG","与被监护人关系的证明"),
    HJZHXMSG("HJZHXMSG","户籍信息证明、常住人口个人信息表等"),
    CYMMSG("CYMMSG","曾用名证明、户口本、履历表等"),
    ZHSDMSG("ZHSDMSG","户口簿、有效的居住证明等"),
    GRMSG("GRMSG","毕业证书、毕业证明书等"),
    DCMSG("DCMSG","学位证书等"),
    JLMSG("JLMSG","经历证明等"),
    ZHWUMSG("ZHWUMSG","专业技术证书、专业技术岗位聘任证书等材料等"),
    ZGMSG("ZGMSG","资格证书等"),
    WFZMSG("WFZMSG","公安部门所出具的无犯罪记录证明"),
    HYMSG("HYMSG","婚姻证明等"),
    QSHMSG("QSHMSG","出生医学证明、独生子女父母光荣证、亲子鉴定书、收养证、原始户籍底册、职工履历表等"),
    SHYMSG("SHYMSG","收养登记证、原始户籍底册、职工履历表等"),
    GQMSG("GQMSG","股权证明书、证券登记公司证明等"),
    ZHSHCHQMSG("ZHSHCHQMSG","著作权登记证书、专利证书、商标登记证书等"),
    CKMSG("CKMSG","存款证明"),
    BDCHWQMSG("BDCHWQMSG","不动产证书等"),
    DCHWQMSG("DCHWQMSG","动产登记凭证，包括车辆行驶证、购买发票等"),
    ZHQMSG("ZHQMSG","债权凭证，包括合同等"),
    XNCHQLMSG("XNCHQLMSG","虚拟财产凭证等"),
    CCHQTMSG("CCHQTMSG","其他财产凭证"),
    SHRMSG("SHRMSG","收入证明等"),
    TAXMSG("TAXMSG","纳税证明等"),
    PJJJMSG("PJJJMSG","原始票据、银行出具的拒绝证明或退票通知等"),
    XPMSG("XPMSG","选票、封装选票的信封、邮票、地址等"),
    ZHWENMSG("ZHWENMSG","当事人需到场"),
    YJSHYMSG("YJSHYMSG","印章"),
    QMSHYMSG("QMSHYMSG","当事人需到场"),
    BKKLMSG("BKKLMSG","不可抗力的证明文件"),
    YWSHJMSG("YWSHJMSG","意外事件的证明"),
    CHWDAJZMSG("CHWDAJZMSG","无档案记载的证明"),
    ZHSHZHZHMSG("ZHSHZHZHMSG","需证明的有法律意义的文书原件"),
    DLMSG("DLMSG","需证明的有法律意义的文书原件"),
    WBXFMSG("WBXFMSG","需证明的有法律意义的文书原件"),
    ZBXZMSG("ZBXZMSG","暂不选择"),
    ;
    private String code;

    private String msg;

    MaterialMsgEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static String getEnumCode(String value) {
        MaterialMsgEnum[] enums = values();
        for (MaterialMsgEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        MaterialMsgEnum[] enums = values();
        for (MaterialMsgEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
