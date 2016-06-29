package net.cassiolandim.dxf.tableEntries

import net.cassiolandim.dxf.DXFEntity
import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.lldxf.ClassifiedTags
import net.cassiolandim.dxf.lldxf.DXFAttr
import net.cassiolandim.dxf.lldxf.DXFAttributes

class Layer extends DXFEntity {

    private static final def FROZEN = 0b00000001
    private static final def THAW = 0b11111110
    private static final def LOCK = 0b00000100
    private static final def UNLOCK = 0b11111011

    private static ClassifiedTags TEMPLATE = ClassifiedTags.fromText(LAYERTEMPLATE)
    private static DXFAttributes DXFATTRIBS = new DXFAttributes([
    'handle': new DXFAttr(5),
    'name': new DXFAttr(2),
    'flags': new DXFAttr(70),
    'color': new DXFAttr(62), // dxf color index, if < 0 layer is off
    'linetype': new DXFAttr(6)
    ])

    private static final String LAYERTEMPLATE = """  0
LAYER
  5
0
  2
LAYERNAME
 70
0
 62
7
  6
CONTINUOUS
"""

    Layer(ClassifiedTags tags, Drawing drawing) {
        super(tags, drawing)

    }

    // TODO
    @Override
    ClassifiedTags getTemplate() {
        return TEMPLATE
    }

    // TODO
    @Override
    DXFAttributes getDefaulDxfAttribs() {
        return DXFATTRIBS
    }
}
