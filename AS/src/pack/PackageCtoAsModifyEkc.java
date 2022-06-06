package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;
/**
 * @author  xyb
 * @version 1.0
 */
@StructClass
public class PackageCtoAsModifyEkc {
    @StructField(order=0)
    @ArrayLengthMarker(fieldName = "IDc")
    public int IDcLen;
    @StructField(order=1)
    public char[] IDc;
    @StructField(order=2)
    @ArrayLengthMarker(fieldName = "TS1")
    public int TS1Len;
    @StructField(order=3)
    public char[] TS1;
    @StructField(order=4)
    public byte[] redundancy;
    @StructField(order=5)
    @ArrayLengthMarker(fieldName = "oldPwd")
    public int oldPwdLen;
    @StructField(order=6)
    public byte[] oldPwd;
    @StructField(order=7)
    @ArrayLengthMarker(fieldName = "newPwd")
    public int newPwdLen;
    @StructField(order=8)
    public byte[] newPwd;
    public PackageCtoAsModifyEkc(String IDc1,String TS11,byte[] oldPwd1,byte[] newPwd1){
        this.IDcLen=IDc1.length();
        this.IDc=IDc1.toCharArray();
        this.TS1Len=TS11.length();
        this.TS1=TS11.toCharArray();
        this.redundancy=new byte[10];
        this.oldPwdLen=oldPwd1.length;
        this.oldPwd=oldPwd1;
        this.newPwdLen=newPwd1.length;
        this.newPwd=newPwd1;
    }
}
