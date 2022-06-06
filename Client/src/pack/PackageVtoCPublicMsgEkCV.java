package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class PackageVtoCPublicMsgEkCV {
    @StructField(order = 0)
    @ArrayLengthMarker(fieldName = "SenderName")
    public int SenderNameLen;
    @StructField(order = 1)
    public char[] SenderName;
    @StructField(order = 2)
    @ArrayLengthMarker(fieldName = "SenderID")
    public int SenderIDLen;
    @StructField(order = 3)
    public char[] SenderID;
    @StructField(order = 4)
    @ArrayLengthMarker(fieldName = "TS")
    public int TSLen;
    @StructField(order = 5)
    public char[] TS;
    @StructField(order = 6)
    @ArrayLengthMarker(fieldName = "Content")
    public int ContentLen;
    @StructField(order = 7)
    public char[] Content;
    @StructField(order = 8)
    @ArrayLengthMarker(fieldName = "EscMd5")
    public int EscMd5Len;
    @StructField(order = 9)
    public byte[] EscMd5;
    @StructField(order = 10)
    public byte[] redundancy;
    public PackageVtoCPublicMsgEkCV (String SenderName1, String SenderID1, String TS1, String Content1, byte[] EscMd51){
        this.SenderNameLen = SenderName1.length();
        this.SenderName = SenderName1.toCharArray();
        this.SenderIDLen = SenderID1.length();
        this.SenderID = SenderID1.toCharArray();
        this.TSLen = TS1.length();
        this.TS = TS1.toCharArray();
        this.ContentLen = Content1.length();
        this.Content = Content1.toCharArray();
        this.EscMd5Len = EscMd51.length;
        this.EscMd5 = EscMd51;
        this.redundancy = new byte[10];
    }
}
