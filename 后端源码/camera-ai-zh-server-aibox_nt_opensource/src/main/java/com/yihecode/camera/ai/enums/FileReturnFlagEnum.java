package com.yihecode.camera.ai.enums;

import cn.hutool.core.util.StrUtil;

public enum FileReturnFlagEnum {
    RETURN_FLAG_0("0", "Common Success"),
    RETURN_FLAG_1("1", "Common Failed"),
    RETURN_FLAG_2("2", "Success, File is Bright Text or Secret Text.\n Compress Package Add Decrypt API in not has this Status;\n \n Determine EOD outer send File Whether make Make Success API in table show In Progress make Make"),
    RETURN_FLAG_9("9", "System Secret Key not Get to or not Init"),
    RETURN_FLAG_20("20", "Encrypt API Exception"),
    RETURN_FLAG_30("30", "Encrypt API Exception"),
    RETURN_FLAG_31("31", "File Stream turn Two in make Failed"),
    RETURN_FLAG_32("32", "not has Get to File Stream"),
    RETURN_FLAG_33("33", "Receive to Need Encrypt Block big small for 0, no Method in Line Encrypt (data~counSize Param Value for 0 or empty)"),
    RETURN_FLAG_34("34", "Compress Package Name Is Empty (data~fileName Param Is Empty)"),
    RETURN_FLAG_35("35", "File Size Is Empty (data~fileSize Param Is Empty)"),
    RETURN_FLAG_36("36", "Compress Package Download failed"),
    RETURN_FLAG_37("37", "Compress Package not has Fall Ground"),
    RETURN_FLAG_38("38", "File Path Is Empty (data~path Param Is Empty)"),
    RETURN_FLAG_39("39", "Get is File Clip, no Method in Line Determine"),
    RETURN_FLAG_40("40", "File does not exist"),
    RETURN_FLAG_41("41", "Get eodConfig out error (data~eodJson Param out error)"),
    RETURN_FLAG_42("42", "Execute EOD File Generate Command Hour out error"),
    RETURN_FLAG_43("43", "EOD Config File Generate failed"),
    RETURN_FLAG_44("44", "Generate EOD File Method Exception"),
    RETURN_FLAG_45("45", "Target File or Directory does not exist (data~targetPath Param out error)"),
    RETURN_FLAG_46("46", "eod File make Make Failed or eod File by Delete"),
    RETURN_FLAG_47("47", "ip Address Is Empty"),
    RETURN_FLAG_48("48", "Hardware code Is Empty"),
    RETURN_FLAG_49("49", "in between piece SDK should Use sub Number super over 5 sub"),
    RETURN_FLAG_50("50", "File Name Is Empty"),
    RETURN_FLAG_51("51", "Non windows Format File Name"),
    RETURN_FLAG_52("52", "Non combine Method Compress Package Type"),
    RETURN_FLAG_54("54", "data~fileOffset turn Complete Integer Failed"),
    RETURN_FLAG_55("55", "data~counSize turn Complete Integer Failed"),
    RETURN_FLAG_56("56", "data~fileSize turn Complete Integer Failed"),
    RETURN_FLAG_57("57", "data~fileOffset Param Is Empty"),
    RETURN_FLAG_58("58", "IP Address not combine Method"),
    RETURN_FLAG_59("59", "Hardware code Format not combine Method"),
    RETURN_FLAG_60("60", "Add Decrypt part part Success part part Failed"),
    RETURN_FLAG_61("61", "EodJson not combine Method"),
    RETURN_FLAG_400("400", "Failed. Request Method Request URL or Param not Match need Request"),
    RETURN_FLAG_422("422", "Failed. Request Data In Server does not exist"),
    RETURN_FLAG_500("500", "Failed, Server Internal Error"),
    RETURN_FLAG_503("503", "Server not has Authorize"),
    RETURN_FLAG_504("504", "Service unavailable, in between piece Authorize Type and Call API Type not Consistent"),
    RETURN_FLAG_505("505", "Service unavailable, in between piece Authorization failed sub Number super over Three sub");

    private String code;
    private String name;

    public static String getNameByCode(String code) {
        FileReturnFlagEnum[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            FileReturnFlagEnum batchExecuteTypeEnum = var1[var3];
            if (StrUtil.equals(code, batchExecuteTypeEnum.getCode())) {
                return batchExecuteTypeEnum.getName();
            }
        }

        return "Encrypt API Exception";
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    private FileReturnFlagEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}