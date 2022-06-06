package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class Authenticator {
    @StructField(order = 0)
    @ArrayLengthMarker(fieldName = "IdClient")
    public int IdClientLength; // Client的标识的长度

    @StructField(order = 1)
    public char[] IdClient; // Client的标识，这里使用Client的账号作为标识

    @StructField(order = 2)
    @ArrayLengthMarker(fieldName = "AddressClient")
    public int AddressClientLength; // Client的网络地址的长度

    @StructField(order = 3)
    public char[] AddressClient; // Client的标识，这里使用Client的网络地址作为标识

    @StructField(order = 4)
    @ArrayLengthMarker(fieldName = "TimeStamp")
    public int TimeStampLength; // Client生成票据时的时间戳的长度

    @StructField(order = 5)
    public char[] TimeStamp; // Client生成的票据的时间

    @StructField(order = 6)
    public byte[] redundancy; // 冗余位，方便扩展功能

    public Authenticator(String IdClient_, String AddressClient_, String TimeStamp_) {
        this.IdClientLength = IdClient_.length();
        this.IdClient = IdClient_.toCharArray();
        this.AddressClientLength = AddressClient_.length();
        this.AddressClient = AddressClient_.toCharArray();
        this.TimeStampLength = TimeStamp_.length();
        this.TimeStamp = TimeStamp_.toCharArray();
        this.redundancy = new byte[10];
    }
}
