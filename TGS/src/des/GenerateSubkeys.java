package des;

import java.io.UnsupportedEncodingException;

/**
 * 产生子密钥
 */

public class GenerateSubkeys {

	/**
	 * 传入密钥，返回保存16个48位子密钥的int型二维数组
	 * 
	 * @param key 密钥
	 * @return subkeys 子密钥
	 * @throws UnsupportedEncodingException
	 */
	public static int[][] generateSubkeys(String key) throws UnsupportedEncodingException {
		int subkeys[][] = new int[16][48];// 子密钥
		while (key.getBytes("GBK").length < 8) {// 密钥不足8字节则补
			key += key;
		}

		byte[] key_byte = key.getBytes("GBK");
		int[] key_bit = new int[64];
		String key_string = "";
		for (int i = 0; i < 8; i++) {
			key_string = Integer.toBinaryString(key_byte[i] & 0xff);// 按位与操作，这里相当于将整型转化为对应的二进制字符串
			if (key_string.length() < 8) {// 不足8位则在前面补0
				for (int j = 0; j <= 8 - key_string.length(); j++) {
					key_string = "0" + key_string;
				}
			}

			for (int j = 0; j < 8; j++) {// 将对应的二进制字符串转化为int型数组储存
				int b = Integer.valueOf(key_string.charAt(j));
				if (b == 48) {
					b = 0;
				} else {// b==49
					b = 1;
				}
				key_bit[i * 8 + j] = b;
			}
		}

		int[] pc1_key_bit = new int[56];
		for (int i = 0; i < 56; i++) {// 压缩置换，64 --> 56
			pc1_key_bit[i] = key_bit[Table.PC_1[i] - 1];
		}

		int[] l0 = new int[28];
		int[] r0 = new int[28];
		System.arraycopy(pc1_key_bit, 0, l0, 0, 28);
		System.arraycopy(pc1_key_bit, 28, r0, 0, 28);// 将pc1_key_bit的28位开始的后28位复制到r0数组0坐标开始的位置
		for (int i = 0; i < 16; i++) {// 产生16个子密钥
			int[] l1 = new int[28];
			int[] r1 = new int[28];
			if (Table.LEFT_SHIFT[i] == 1) {// 左右两部分分别左移1位
				System.arraycopy(l0, 1, l1, 0, 27);
				l1[27] = l0[0];
				System.arraycopy(r0, 1, r1, 0, 27);
				r1[27] = r0[0];
			} else {// 左右两部分分别左移2位
				System.arraycopy(l0, 2, l1, 0, 26);
				l1[26] = l0[0];
				l1[27] = l0[1];
				System.arraycopy(r0, 2, r1, 0, 26);
				r1[26] = r0[0];
				r1[27] = r0[1];
			}

			int[] pc2_key_bit = new int[56];
			System.arraycopy(l1, 0, pc2_key_bit, 0, 28);
			System.arraycopy(r1, 0, pc2_key_bit, 28, 28);
			for (int j = 0; j < 48; j++) {// pc2压缩置换，56 --> 48
				subkeys[i][j] = pc2_key_bit[Table.PC_2[j] - 1];
			}

			l0 = l1;
			r0 = r1;
		}

		return subkeys;
	}

}
