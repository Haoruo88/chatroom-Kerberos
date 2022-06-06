package Utils;

import java.io.UnsupportedEncodingException;

/**
 * 对明文（密文）进行分组
 *
 */

public class GroupText {

	/**
	 * 对传入的明文（密文）分组，以64bit为一组，最后一组不足64bit则补全，以二进制int型数组形式返回分组结果
	 * 
	 * @param text_byte 明文（密文）
	 * @return int_group_result 分组结果
	 * @throws UnsupportedEncodingException
	 */
	public static int[][] groupText(byte[] text_byte) throws UnsupportedEncodingException {
		int text_byte_length = text_byte.length;// 明文（密文）字节数
		int padding_num = 8 - text_byte_length % 8;// 要补充的字节数

		byte[] text_padding;// 补充后是明文（密文）数据
		if (padding_num != 8) {// 填充至8的整数
			text_padding = new byte[text_byte_length + padding_num];
			System.arraycopy(text_byte, 0, text_padding, 0, text_byte_length);
			for (int i = 0; i < padding_num; i++) {
				text_padding[text_byte_length + i] = (byte) padding_num;
			}
		} else {
			text_padding = text_byte;
		}

		int group_num = text_padding.length / 8;// 共有几组明文（密文）
		String text_bit = "";
		String text_bit_tmp = "";
		int[][] group_result_int = new int[group_num][64];
		for (int i = 0; i < group_num; i++) {// 将每组的byte型转化为二进制int型数组返回
			for (int j = 0; j < 8; j++) {// 组内8个字节转为string型二进制数据
				text_bit_tmp = Integer.toBinaryString(text_padding[i * 8 + j] & 0xff);
				while (text_bit_tmp.length() % 8 != 0) {
					text_bit_tmp = "0" + text_bit_tmp;
				}
				text_bit += text_bit_tmp;
			}

			for (int z = 0; z < 64; z++) {// string型二进制数据转为int型二进制数据
				int p_t = Integer.valueOf(text_bit.charAt(z));
				if (p_t == 48) {
					p_t = 0;
				} else {// p_t== 49
					p_t = 1;
				}
				group_result_int[i][z] = p_t;
			}

			text_bit = "";
			text_bit_tmp = "";
		}

		return group_result_int;
	}

}
