package com.ethereal.client.Request.Annotation;

public class InvokeTypeFlags {
    public static final int Local = 0x1;
    public static final int Remote = 0x2;
    public static final int Fail = 0x4;
    public static final int Success = 0x8;
    public static final int ReturnLocal = 0x10;
    public static final int ReturnRemote = 0x20;
    public static final int Timeout = 0x40;
    public static final int All = 0x80;
}
