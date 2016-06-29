package net.cassiolandim.dxf.graphics

import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.lldxf.ClassifiedTags
import net.cassiolandim.dxf.lldxf.DXFAttr
import net.cassiolandim.dxf.lldxf.DXFAttributes

/**
 * SHAPE is not tested with real world DXF drawings!
 */
class Shape extends GraphicEntity {

    private static final String SHAPE_TPL = """  0
SHAPE
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
  2
NAME
 50
0.0
 41
1.0
 51
0.0
"""

    Shape(ClassifiedTags tags, Drawing drawing = null) {
        super(tags, drawing)
    }

    Shape(String handle, Map dxfAttribs, Drawing drawing) {
        super(handle, dxfAttribs, drawing)
    }

    @Override
    ClassifiedTags getTemplate() {
        return ClassifiedTags.fromText(SHAPE_TPL)
    }

    @Override
    DXFAttributes getDefaulDxfAttribs() {
        return makeAttribs([
                'insert': new DXFAttr(10, 'Point2D/3D'),
                'size': new DXFAttr(40),
                'name': new DXFAttr(2),
                'rotation': new DXFAttr(50, null, 0.0),
                'xscale': new DXFAttr(41, null, 1.0),
                'oblique': new DXFAttr(51, null, 0.0),
        ])
    }
}
