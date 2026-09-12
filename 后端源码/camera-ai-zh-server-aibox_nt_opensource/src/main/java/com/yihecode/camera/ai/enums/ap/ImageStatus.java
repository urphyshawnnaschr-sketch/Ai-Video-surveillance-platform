package com.yihecode.camera.ai.enums.ap;

//0- not Annotation, 1- Smart can Annotation, 4- Person work Annotation Lock,5- Person work Annotation,11- Quality Check Lock,15- Quality Check Pass,16- Type return
public enum ImageStatus {
WAIT(0,"not Annotation"),
ALGORITHM(1,"Smart can Annotation"),
ING(4,"Person work Annotation Lock"),
DONE(5,"Person work Annotation"),
IMPORT_PROGRESS_FINSH(6,"Import Annotation"),
REVIEWING(11,"Quality Check Lock"),
PASS(15,"Quality Check Pass"),
REJECTED(16,"Rejected");

private Integer type;

private String text;

ImageStatus(Integer type, String text) {
this.type = type;
this.text = text;
}

public static boolean toLabeled(Integer imageStatus) {
return WAIT.type.equals(imageStatus) || ALGORITHM.type.equals(imageStatus) || IMPORT_PROGRESS_FINSH.type.equals(imageStatus);
}

public static boolean labeled(Integer imageStatus) {
return ING.type.equals(imageStatus) || DONE.type.equals(imageStatus) || REVIEWING.type.equals(imageStatus)
|| PASS.type.equals(imageStatus) || REJECTED.type.equals(imageStatus);
}

public Integer getType() {
return type;
}

public void setType(Integer type) {
this.type = type;
}

public String getText() {
return text;
}

public void setText(String text) {
this.text = text;
}
}
