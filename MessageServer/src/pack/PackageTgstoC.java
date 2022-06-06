package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class PackageTgstoC {
    @StructField(order = 0)
    public byte status;
    @StructField(order = 1)
    @ArrayLengthMarker(fieldName = "EkCTgs")
    public int EkCTgsLen;
    @StructField(order = 2)
    public byte[] EkCTgs;
    public PackageTgstoC(byte[] EkCTgs1){
        this.status=4;
        this.EkCTgsLen=EkCTgs1.length;
        this.EkCTgs=EkCTgs1;
    }
}
