package net.cassiolandim.dxf

class StringUtil {

    static String ustr(def obj) {
        return obj.toString()
    }

    /**
     * convert into Hexadecimal notation of Unicode.<br>
     * example)a?\u0061
     * @param str
     * @return
     */
    public static String toHexString(String str) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            sb.append(toHexString(str.charAt(i)));
        }
        return sb.toString();
    }

    /**
     * convert into Hexadecimal notation of Unicode.<br>
     * example)a?\u0061
     * @param ch
     * @return
     */
    public static String toHexString(char ch) {
        String hex = Integer.toHexString((int) ch);
        while (hex.length() < 4) {
            hex = "0" + hex;
        }
        hex = "\\u+" + hex;
        return hex.toUpperCase();
    }
}
