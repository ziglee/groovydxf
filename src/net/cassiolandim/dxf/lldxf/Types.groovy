package net.cassiolandim.dxf.lldxf

class Types {

    private static TAG_STRING_FORMAT = '%3d\n%s\n'

    static def toInternalType = { value ->
        return value
    }

    // ustr
    static Closure<String> toStr = { value ->
        return value == null ? '' : value.toString()
    }

    static def toPoint = { value ->
        Float x
        Float y
        Float z

        if (value[0] instanceof Integer || value[0] instanceof Float || value[0] instanceof Double || value[0] instanceof BigDecimal)
            x = value[0]
        else
            x = Float.parseFloat(value[0])

        if (value[1] instanceof Integer || value[1] instanceof Float || value[1] instanceof Double || value[1] instanceof BigDecimal)
            y = value[1]
        else
            y = Float.parseFloat(value[1])

        def point = [x, y]

        if (value.size() > 2) {
            if (value[2] instanceof Integer || value[2] instanceof Float || value[2] instanceof Double || value[2] instanceof BigDecimal)
                z = value[2]
            else
                z = Float.parseFloat(value[2])

            point << z
        }

        return point
    }

    static Closure<Float> toFloat = { value ->
        if (value instanceof Float) return value
        if (value instanceof Double) return value.toFloat()
        return Float.parseFloat(value)
    }

    static Closure<Integer> toInt = { value ->
        if (value instanceof Integer) return value
        return Integer.parseInt(value)
    }

    static def buildTypeTable(types) {
        def table = [:]
        types.each { row ->
            row[1].each{
                table.put it, row[0]
            }
        }
        return table
    }

    static def TYPE_TABLE = buildTypeTable([
        [toInternalType, [-10]], // spacial tags for internal use
        [toStr, (0..9)],
        [toPoint, (10..19)],  // 2d or 3d points
        [toFloat, (20..59)],  // code 20-39 belongs to 2d/3d points and should not appear alone
        [toInt, (60..99)],
        [toStr, (100..105)],
        [toPoint, (110..112)],  // 110, 111, 112 - UCS definition
        [toFloat, (113..149)],  // 113-139 belongs to UCS definition and should not appear alone
        [toInt, (160..169)],
        [toInt, (170..179)],
        [toPoint, [210]],  // extrusion direction
        [toFloat, (211..239)],  // code 220, 230 belongs to extrusion direction and should not appear alone
        [toInt, (270..289)],
        [toInt, (290..299)],  // bool 1=True 0=False
        [toStr, (300..369)],
        [toInt, (370..389)],
        [toStr, (390..399)],
        [toInt, (400..409)],
        [toStr, (410..419)],
        [toInt, (420..429)],
        [toStr, (430..439)],
        [toInt, (440..459)],
        [toFloat, (460..469)],
        [toStr, (470..479)],
        [toStr, (480..481)],
        [toStr, (999..1009)],
        [toPoint, (1010..1019)],
        [toFloat, (1020..1059)],  // code 1020-1039 belongs to 2d/3d points and should not appear alone
        [toInt, (1060..1071)],
        ])

    static String strTag2(DXFTag tag) {
        def code = tag.code
        if (isPointCode(code)) {
            def s = ""
            for (coord in tag.value) {
                s += strTag(new DXFTag(code, coord))
                code += 10
            }
            return s
        } else {
            return strTag(tag)
        }
    }

    static String strTag(DXFTag tag) {
        return sprintf(TAG_STRING_FORMAT, tag.code, tag.value == null ? '' : tag.value)
    }

    static boolean isPointCode(code) {
        return (10 <= code && code <= 19) || code == 210 || (110 <= code && code <= 112) || (1010 <= code && code <= 1019)
    }

    static DXFTag castTag(DXFTag tag, def types = TYPE_TABLE) {
        def caster = types.get(tag.code) ?: toStr
        try {
            return new DXFTag(tag.code, caster(tag.value))
        } catch (NumberFormatException nfe) {
            if (caster.equals(toInt)) { // convert float to int
                return new DXFTag(tag.code, (int) (Float.parseFloat(tag.value)))
            } else {
                throw nfe
            }
        }
    }

    static def castTagValue(int code, def value, def types = TYPE_TABLE) {
        def caster = types.get(code) ?: toStr
        return caster(value)
    }
}
