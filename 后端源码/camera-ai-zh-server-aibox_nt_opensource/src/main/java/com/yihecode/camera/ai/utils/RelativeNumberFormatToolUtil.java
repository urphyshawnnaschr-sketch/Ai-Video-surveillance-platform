package com.yihecode.camera.ai.utils;

import cn.hutool.core.util.ObjectUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
* @Author lichangliang
* @Date 2023/7/26 11:18
* @Describe
* @Version 1.0
*/
public class RelativeNumberFormatToolUtil {

    public static final String CH = "CH";
    /**
* Splice Audio Display
*/
    public static final String PY = "PY";

    private static final Long THOUSAND = 1000L;
    private static final Long TEN_THOUSAND = 10000L;
    private static final Long ONE_HUNDRED_MILLION = 100000000L;

    private static BigDecimal TenThousand = new BigDecimal(10000);

    /**
* Data Convert
*
* @param temp
* Need Convert Data Support Long,BigDecimal,Integer,String,int,long Type
* @param type
* Need Convert Mode RelativeNumberFormatTool.CH: in Text Display RelativeNumberFormatTool.PY: Splice Audio Display
* @return
*/
    public static String relativeNumberFormat(Object temp, String type) {
        Long num = numberFormat(temp);
        if (null == num) {
            return temp + "";
        }
        if (type.equals(PY)) {
            if (num.compareTo(ONE_HUNDRED_MILLION) == 1 || num.compareTo(ONE_HUNDRED_MILLION) == 0) {
                return "9999w+";
            }
            if (num.compareTo(TEN_THOUSAND) == 1 || num.compareTo(TEN_THOUSAND) == 0) {
                String divide = new BigDecimal(num).divide(new BigDecimal(TEN_THOUSAND), 1, RoundingMode.DOWN).toString();
                return divide+"w";
            }
            if (num.compareTo(THOUSAND) == 1 || num.compareTo(THOUSAND) == 0) {
                String divide = new BigDecimal(num).divide(new BigDecimal(THOUSAND), 1, RoundingMode.DOWN).toString();
                return divide+"k+";
            }
        } else if (type.equals(CH)) {
            if (num.compareTo(ONE_HUNDRED_MILLION) == 1 || num.compareTo(ONE_HUNDRED_MILLION) == 0) {
                return "9999 Ten Thousand +";
            }
            if (num.compareTo(TEN_THOUSAND) == 1 || num.compareTo(TEN_THOUSAND) == 0) {
                return num / TEN_THOUSAND + "Ten Thousand +";
            }
            if (num.compareTo(THOUSAND) == 1 || num.compareTo(THOUSAND) == 0) {
                return num / THOUSAND + "Thousand +";
            }
        }
        return num + "";
    }
    /**
* Format Change Data for Long Type
*/
    public static Long numberFormat(Object number) {
        if (number != null && !"".equals(number)) {
            if (number instanceof BigDecimal) {
                return ((BigDecimal)number).longValue();
            }
            if (number instanceof Integer) {
                return ((Integer)number).longValue();
            }
            if (number instanceof Long) {
                return (Long)number;
            }
            if (number instanceof String) {
                try {
                    return Long.valueOf(number + "");
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    public static double getPercentValue(int[] arr,double sum,int idx,int precision){
        if(arr[idx]<=0) {
            return 0;
        }
        if((arr.length-1) < idx){
            return 0;
        }
        //Request and
if(sum <= 0){
for (int i = 0; i < arr.length; i++) {
sum += arr[i];
}
}
//10 2 sub Power is 100, Use at Calculate Fine Degree.
double digits = Math.pow(10,precision);
// Expand big than Example 100
double[] votesPerQuota = new double[arr.length];
for(int i = 0; i < arr.length; i++){
double val = arr[i] / sum * digits * 100;
votesPerQuota[i] = val;
}
// Total, Expand big than Example Meaning Taste Total need Expand big
double targetSeats = digits * 100;
// again to down Get Value, group Complete Number group
double[] seats = new double[arr.length];
for(int i = 0; i < votesPerQuota.length; i++){
seats[i] = Math.floor(votesPerQuota[i]);
}
// again new Calculate combine Plan, Use at Determine and Total Count Whether Phase same, Phase same rule Occupy than will 100%
double currentSum = 0;
for (int i = 0; i < seats.length; i++) {
if(seats[i]<0){
continue;
}
currentSum += seats[i];
}
// remainder Number part part Number group: original First Number group decrease Remove to down Get Value Number group, Get to remainder Number part part Number group
double[] remainder = new double[arr.length];
for(int i = 0; i < seats.length; i++){
remainder[i] = votesPerQuota[i] - seats[i];
}
while(currentSum < targetSeats){
double max = 0;
int maxId = 0;
int len = 0;
for(int i = 0;i < remainder.length;++i){
if(remainder[i] > max){
max = remainder[i];
maxId = i;
}
}
// for most big Item remainder amount Add 1
++seats[maxId];
// via increase Add most big remainder Number Add 1, rule down sub Determine just can with not Need again Determine this remainder amount Number.
remainder[maxId] = 0;
// total Also need Add 1, for Determine Whether Total Whether Phase same, Jump out Loop.
++currentSum;
}
// this Hour Wait seats just will Total Occupy than will 100%
return seats[idx] / digits;
}

public static BigDecimal realTenThousand(BigDecimal data){
if(ObjectUtil.isNull(data)){
data = new BigDecimal(0);
}
return data.divide(TenThousand, 2, RoundingMode.HALF_UP);
}
public static void main(String[] args) {
BigDecimal a = new BigDecimal(103);
int c = 8350;
Integer c1 = 1000000;
String str ="1000";
long bb = 13544L;
Long bb1 = 3624L;

System.out.println(relativeNumberFormat(a, CH));
System.out.println(relativeNumberFormat(c, CH));
System.out.println(relativeNumberFormat(c1, CH));
System.out.println(relativeNumberFormat(str, CH));
System.out.println(relativeNumberFormat(bb, CH));
System.out.println(relativeNumberFormat(bb1, CH));
System.out.println(relativeNumberFormat(a, PY));
System.out.println(relativeNumberFormat(c, PY));
System.out.println(relativeNumberFormat(c1, PY));
System.out.println(relativeNumberFormat(str, PY));
System.out.println(relativeNumberFormat(bb, PY));
System.out.println(relativeNumberFormat(bb1, PY));
}
}
