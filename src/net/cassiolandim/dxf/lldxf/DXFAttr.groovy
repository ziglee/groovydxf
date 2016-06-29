package net.cassiolandim.dxf.lldxf

class DXFAttr {

    int code
    String xType
    def defauult
    String dxfVersion

    DXFAttr(int code, String xType = null, def defauult = null, String dxfVersion = null) {
        this.code = code
        this.xType = xType
        this.defauult = defauult
        this.dxfVersion = dxfVersion
    }
}
