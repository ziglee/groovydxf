package net.cassiolandim.dxf.lldxf

class DXFAttr3 {

    int code
    def xType
    int subClass
    def defauult
    def dxfVersion

    DXFAttr3(int code, def xType = null, Integer subClass = null, def defauult = null, def dxfVersion = null) {
        this.code = code
        this.xType = xType
        this.subClass = subClass
        this.defauult = defauult
        this.dxfVersion = dxfVersion
    }
}
