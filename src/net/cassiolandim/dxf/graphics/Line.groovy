package net.cassiolandim.dxf.graphics

import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.lldxf.ClassifiedTags
import net.cassiolandim.dxf.lldxf.DXFAttr
import net.cassiolandim.dxf.lldxf.DXFAttributes

class Line extends GraphicEntity {

    private static final String LINE_TPL = """  0
LINE
  5
0
  8
0
 10
0.0
 20
0.0
 30
0.0
 11
1.0
 21
1.0
 31
1.0
"""

    Line(ClassifiedTags tags, Drawing drawing = null) {
        super(tags, drawing)
    }

    Line(String handle, Map dxfAttribs, Drawing drawing) {
        super(handle, dxfAttribs, drawing)
    }

    @Override
    ClassifiedTags getTemplate() {
        return ClassifiedTags.fromText(LINE_TPL)
    }

    @Override
    DXFAttributes getDefaulDxfAttribs() {
        return makeAttribs([
                'start': new DXFAttr(10, 'Point2D/3D'),
                'end': new DXFAttr(11, 'Point2D/3D'),
        ])
    }
}
