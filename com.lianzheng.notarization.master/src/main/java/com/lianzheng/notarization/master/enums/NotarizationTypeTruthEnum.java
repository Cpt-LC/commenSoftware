package com.lianzheng.notarization.master.enums;

public enum NotarizationTypeTruthEnum {
    WTMFB01("WTMFB01","0116"),
    WTMFS01("WTMFS01","0116"),
    WTQT("WTQT","0116"),
    FQJCSM("FQJCSM","0201"),
    SMQT("SMQT","0202"),
    FDJC("FDJC","3901"),
    CHSH("CHSH","09"),
    SHC("SHC","10"),
    SW("SW","0202"),
    GJ("GJ","1201"),
    JH("JH","1202"),
    HJZHX("HJZHX","1208"),
    CYM("CYM","1301"),
    ZHSD("ZHSD","14"),
    GR("GR","15"),
    DC("DC","16"),
    JL("JL","1701"),
    ZHWU("ZHWU","1801"),
    ZG("ZG","1904"),
    WFZ("WFZ","2001"),
    HY("HY","0202"),
    QSH("QSH","2201"),
    SHY("SHY","2301"),
    GQ("GQ","0107"),
    ZHSHCHQ("ZHSHCHQ","1902"),
    CK("CK","2505"),
    BDCHWQ("BDCHWQ","2506"),
    DCHWQ("DCHWQ","2507"),
    ZHQ("ZHQ","2508"),
    XNCHQL("XNCHQL","2509"),
    CCHQT("CCHQT","2510"),
    SHR("SHR","26"),
    TAX("TAX","27"),
    PJJJ("PJJJ","28"),
    XP("XP","29"),
    ZHWEN("ZHWEN","3001"),
    YJSHY("YJSHY","3002"),
    QMSHY("QMSHY","3003"),
    BKKL("BKKL","3101"),
    YWSHJ("YWSHJ","3102"),
    CHWDAJZ("CHWDAJZ","32"),
    ZHSHZHZH("ZHSHZHZH","33"),
    DL("DL","33"),
    WBXF("WBXF","3501"),
    CERT("CERT","3502"),
//    毕业证书：15
//    学位：16
//    完税证明：27
//    机动车驾驶证：33
//    无犯罪：2001
//    双证：3502

    ;
    private String code;

    private String msg;

    NotarizationTypeTruthEnum(String code, String msg) {
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
        NotarizationTypeTruthEnum[] enums = values();
        for (NotarizationTypeTruthEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getEnumMsg(String value) {
        NotarizationTypeTruthEnum[] enums = values();
        for (NotarizationTypeTruthEnum item : enums) {
            if (item.getCode().equals(value)) {
                return item.getMsg();
            }
        }
        return null;
    }
}
