package net.cassiolandim.dxf.graphics

import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.lldxf.ClassifiedTags
import net.cassiolandim.dxf.lldxf.DXFAttr
import net.cassiolandim.dxf.lldxf.DXFAttributes

class Circle extends GraphicEntity {

    private static String CIRCLE_TPL = """  0
CIRCLE
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
 40
1.0
"""

    Circle(ClassifiedTags tags, Drawing drawing = null) {
        super(tags, drawing)
    }

    Circle(String handle, Map dxfAttribs, Drawing drawing) {
        super(handle, dxfAttribs, drawing)
    }

    @Override
    ClassifiedTags getTemplate() {
        return ClassifiedTags.fromText(CIRCLE_TPL)
    }

    @Override
    DXFAttributes getDefaulDxfAttribs() {
        return makeAttribs([
                'center': new DXFAttr(10, 'Point2D/3D'),
                'radius': new DXFAttr(40)
        ])
    }
}
