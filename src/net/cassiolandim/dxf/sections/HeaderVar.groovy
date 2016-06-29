package net.cassiolandim.dxf.sections

import net.cassiolandim.dxf.lldxf.DXFTag
import net.cassiolandim.dxf.lldxf.Types

class HeaderVar {

    DXFTag tag

    public HeaderVar(DXFTag tag) {
        this.tag = tag
    }

    int getCode() {
        return tag.code
    }

    String getValue() {
        return tag.value
    }

    boolean isPoint() {
        return tag.code == 10
    }

    @Override
    String toString() {
        if (isPoint()) {
            def s = []
            def code = tag.code
            for (coord in tag.value) {
                s.add(Types.strTag(new DXFTag(code, coord)))
                code += 10
            }
            return s.join('')
        } else {
            return Types.strTag(tag)
        }
    }
}
