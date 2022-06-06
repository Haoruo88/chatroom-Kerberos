package pack;

import struct.StructClass;
import struct.StructField;
/**
 * @author  xyb
 * @version 1.0
 */
@StructClass
public class PackageAstoCRegister {
    @StructField(order=0)
    public byte status;
    @StructField(order = 1)
    public byte registerStatus;
    public PackageAstoCRegister(byte registerStatus1){
        this.status=1;
        this.registerStatus=registerStatus1;
    }
}
