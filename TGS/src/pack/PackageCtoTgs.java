package pack;

import struct.ArrayLengthMarker;
import struct.StructClass;
import struct.StructField;

@StructClass
public class PackageCtoTgs {
    @StructField(order = 0)
    @ArrayLengthMarker(fieldName = "IdServer")
    public int IdServerLength; // 服务器标识的长度

    @StructField(order = 1)
    public char[] IdServer; // 服务器标识，这里选用服务器的IP地址作为标识

    @StructField(order = 2)
    @ArrayLengthMarker(fieldName = "TicketTgs")
    public int TicketTgsLength; // Client给TGS的票据的长度

    @StructField(order = 3)
    public byte[] TicketTgs; // Client给TGS的票据

    @StructField(order = 4)
    @ArrayLengthMarker(fieldName = "Authenticator")
    public int AuthenticatorLength; // 认证者长度

    @StructField(order = 5)
    public byte[] Authenticator; // 认证者

    @StructField(order = 6)
    public byte[] redundancy; // 冗余

    public PackageCtoTgs(String IdServer_, byte[] TicketTgs_, byte[] Authenticator_) {
        this.IdServerLength = IdServer_.length();
        this.IdServer = IdServer_.toCharArray();
        this.TicketTgsLength = TicketTgs_.length;
        this.TicketTgs = TicketTgs_;
        this.AuthenticatorLength = Authenticator_.length;
        this.Authenticator = Authenticator_;
        this.redundancy = new byte[10];
    }
}
