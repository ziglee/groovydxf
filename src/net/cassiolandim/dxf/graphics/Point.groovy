package net.cassiolandim.dxf.graphics

import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.lldxf.ClassifiedTags
import net.cassiolandim.dxf.lldxf.DXFAttr
import net.cassiolandim.dxf.lldxf.DXFAttributes

class Point extends GraphicEntity {

    private static final String POINT_TPL = """  0
POINT
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
"""

    Point(ClassifiedTags tags, Drawing drawing = null) {
        super(tags, drawing)
    }

    Point(String handle, Map dxfAttribs, Drawing drawing) {
        super(handle, dxfAttribs, drawing)
    }

    @Override
    ClassifiedTags getTemplate() {
        return ClassifiedTags.fromText(POINT_TPL)
    }

    @Override
    DXFAttributes getDefaulDxfAttribs() {
        return makeAttribs([
                'location': new DXFAttr(10, 'Point2D/3D')
        ])
    }
}
