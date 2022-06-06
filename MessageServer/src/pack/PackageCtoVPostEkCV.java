package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class PackageCtoVPostEkCV {
    @StructField(order = 0)
    @ArrayLengthMarker(fieldName = "ReceiverID")
    public int ReceiverIDLen;
    @StructField(order = 1)
    public char[] ReceiverID;
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
    public int SubjectLen;
    @StructField(order = 7)
    public int TextLen;
    @StructField(order = 8)
    @ArrayLengthMarker(fieldName = "AttachmentName")
    public int AttachmentNameLen;
    @StructField(order = 9)
    public char[] AttachmentName;
    @StructField(order = 10)
    public int AttachmentLen;
    @StructField(order = 11)
    @ArrayLengthMarker(fieldName = "Content")
    public int ContentLen;
    @StructField(order = 12)
    public byte[] Content;
    @StructField(order = 13)
    @ArrayLengthMarker(fieldName = "EscMd5")
    public int EscMd5Len;
    @StructField(order = 14)
    public byte[] EscMd5;
    @StructField(order = 15)
    public byte[] redundancy;
    public PackageCtoVPostEkCV(String ReceiverID1,String SenderID1,String TS1,int SubjectLen1,int TextLen1,String AttachmentName1,int AttachmentLen1,byte[] Content1,byte[] EscMd51){
        this.ReceiverIDLen=ReceiverID1.length();
        this.ReceiverID=ReceiverID1.toCharArray();
        this.SenderIDLen=SenderID1.length();
        this.SenderID=SenderID1.toCharArray();
        this.TSLen=TS1.length();
        this.TS=TS1.toCharArray();
        this.SubjectLen=SubjectLen1;
        this.TextLen=TextLen1;
        this.AttachmentNameLen=AttachmentName1.length();
        this.AttachmentName=AttachmentName1.toCharArray();
        this.AttachmentLen=AttachmentLen1;
        this.ContentLen=Content1.length;
        this.Content=Content1;
        this.EscMd5Len=EscMd51.length;
        this.EscMd5=EscMd51;
        this.redundancy=new byte[10];
    }
}
