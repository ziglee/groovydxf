package net.cassiolandim.dxf.lldxf

class DXFAttributes extends LinkedHashMap<String, DXFAttr3> {

    def subClasses = []
    def attribs = [:]

    DXFAttributes(Map<String, DXFAttr> subClassDef) {
        addSubClass(subClassDef)
    }

    void addSubClass(Map<String, DXFAttr> subClass) {
        int size = subClasses.size()
        subClasses << subClass
        addSubClassAttribs(subClass, size)
    }

    private void addSubClassAttribs(Map<String, DXFAttr> subClass, int index) {
        for (Entry<String, DXFAttr> entry in subClass.entrySet()) {
            String name = entry.key
            DXFAttr dxfAttrib = entry.value
            attribs.put(name, new DXFAttr3(dxfAttrib.code, dxfAttrib.xType, index, dxfAttrib.defauult, dxfAttrib.dxfVersion))
        }
    }

    @Override
    DXFAttr3 get(Object key) {
        return attribs.get(key)
    }
}
