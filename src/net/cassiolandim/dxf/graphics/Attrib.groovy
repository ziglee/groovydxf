package net.cassiolandim.dxf.graphics

import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.lldxf.ClassifiedTags
import net.cassiolandim.dxf.lldxf.DXFAttr
import net.cassiolandim.dxf.lldxf.DXFAttributes

class Attrib extends Text {

    private static final String ATTRIB_TPL = """  0
ATTRIB
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
  1
DEFAULTTEXT
  2
TAG
 70
0
 50
0.0
 51
0.0
 41
1.0
  7
STANDARD
 71
0
 72
0
 73
0
 74
0
 11
0.0
 21
0.0
 31
0.0
"""

    Attrib(ClassifiedTags tags, Drawing drawing = null) {
        super(tags, drawing)
    }

    Attrib(String handle, Map dxfAttribs, Drawing drawing) {
        super(handle, dxfAttribs, drawing)
    }

    @Override
    ClassifiedTags getTemplate() {
        return ClassifiedTags.fromText(ATTRIB_TPL)
    }

    @Override
    DXFAttributes getDefaulDxfAttribs() {
        return makeAttribs([
                'insert': new DXFAttr(10, 'Point2D/3D'),
                'height': new DXFAttr(40),
                'text': new DXFAttr(1),
                'tag': new DXFAttr(2),
                'flags': new DXFAttr(70),
                'field_length': new DXFAttr(73, null, 0),
                'rotation': new DXFAttr(50, null, 0.0),
                'oblique': new DXFAttr(51, null, 0.0),
                'width': new DXFAttr(41, null, 1.0), // width factor
                'style': new DXFAttr(7, null, 'STANDARD'),
                'text_generation_flag': new DXFAttr(71, null, 0), // 2 = backward (mirr-x), 4 = upside down (mirr-y)
                'halign': new DXFAttr(72, null, 0), // horizontal justification
                'valign': new DXFAttr(74, null, 0), // vertical justification
                'align_point': new DXFAttr(11, 'Point2D/3D')
        ])
    }
}
