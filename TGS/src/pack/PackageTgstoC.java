package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class PackageTgstoC {
    @StructField(order = 0)
    public byte status;

    @StructField(order = 1)
    @ArrayLengthMarker(fieldName = "EncryptKeyClientTgs")
    public int EncryptKeyClientTgsLength;

    @StructField(order = 2)
    public byte[] EncryptKeyClientTgs;

    public PackageTgstoC(byte[] EkCTgs1) {
        this.status = 4;
        this.EncryptKeyClientTgsLength = EkCTgs1.length;
        this.EncryptKeyClientTgs = EkCTgs1;
    }
}
