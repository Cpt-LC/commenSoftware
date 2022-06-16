package com.lianzheng.h5.enums;

import com.lianzheng.h5.common.GlobalException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
public enum NotarizationTypeEnum {
    WTMFB01("WTMFB01","委托买房"),
    WTMFS01("WTMFS01","委托卖房（直系亲属之间）"),
    WTQT("WTQT","委托（其他）"),
    FQJCSM("FQJCSM","放弃继承权声明"),
    SMQT("SMQT","声明（其他）"),
    FDJC("FDJC","法定继承"),
    CHSH("CHSH","出生"),
    SHC("SHC","生存"),
    SW("SW","死亡"),
    GJ("GJ","国籍"),
    JH("JH","监护"),
    HJZHX("HJZHX","户籍注销"),
    CYM("CYM","曾用名"),
    ZHSD("ZHSD","住所地（居住地）"),
    GR("GR","毕业证书"),
    DC("DC","学位证书"),
    JL("JL","经历"),
    ZHWU("ZHWU","职务（职称）"),
    ZG("ZG","资格"),
    WFZ("WFZ","无犯罪记录"),
    HY("HY","婚姻状况"),
    QSH("QSH","亲属关系"),
    SHY("SHY","收养关系"),
    GQ("GQ","股权"),
    ZHSHCHQ("ZHSHCHQ","知识产权权"),
    CK("CK","存款"),
    BDCHWQ("BDCHWQ","不动产物权"),
    DCHWQ("DCHWQ","动产物权"),
    ZHQ("ZHQ","债权"),
    XNCHQL("XNCHQL","虚拟财产权利"),
    CCHQT("CCHQT","其他财产权"),
    SHR("SHR","收入状况"),
    TAX("TAX","完税证明"),
    PJJJ("PJJJ","票据拒绝"),
    XP("XP","选票"),
    ZHWEN("ZHWEN","指纹"),
    YJSHY("YJSHY","印鉴式样"),
    QMSHY("QMSHY","签名式样"),
    BKKL("BKKL","不可抗力"),
    YWSHJ("YWSHJ","意外事件"),
    CHWDAJZ("CHWDAJZ","查无档案记载"),
    ZHSHZHZH("ZHSHZHZH","证书(执照)"),
    DL("DL","机动车驾驶证"),
    WBXF("WBXF","文本相符"),
    ZBXZ("ZBXZ","暂不选择"),;

    private final String code;
    private final String msg;

    NotarizationTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public static NotarizationTypeEnum getEnumByCode(String code) {
        return Arrays.stream(NotarizationTypeEnum.values())
                .filter(p -> Objects.equals(p.getCode(), code))
                .findFirst().orElseThrow(() -> new GlobalException("事项类型异常"));
    }
}
