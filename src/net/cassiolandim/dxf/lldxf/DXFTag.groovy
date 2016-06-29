package net.cassiolandim.dxf.lldxf

class DXFTag {

    int code
    def value

    public DXFTag(int code, def value) {
        this.code = code
        this.value = value
    }

    @Override
    boolean equals(Object obj) {
        if (!obj) return false
        if (!obj instanceof  DXFTag) return false
        def other = (DXFTag) obj
        return code == other.code && value.equals(other.value)
    }

    @Override
    int hashCode() {
        return code + value.hashCode()
    }

    @Override
    String toString() {
        return "DXFTag(code=${code}, value='${value}')"
    }
}
