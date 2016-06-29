package net.cassiolandim.dxf.graphics

import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.lldxf.ClassifiedTags
import net.cassiolandim.dxf.lldxf.DXFAttr
import net.cassiolandim.dxf.lldxf.DXFAttributes

/**
 * IMPORTANT: Bug in AutoCAD 2010
 * attribsfollow = 0, for NO attribsfollow, does not work with ACAD 2010
 * if no attribs attached to the INSERT entity, omit attribsfollow tag
 */
class Insert extends GraphicEntity {

    private static String INSERT_TPL = """  0
INSERT
  5
0
  8
0
  2
BLOCKNAME
 10
0.0
 20
0.0
 30
0.0
 41
1.0
 42
1.0
 43
1.0
 50
0.0
"""

    Insert(ClassifiedTags tags, Drawing drawing = null) {
        super(tags, drawing)
    }

    Insert(String handle, Map dxfAttribs, Drawing drawing) {
        super(handle, dxfAttribs, drawing)
    }

    @Override
    ClassifiedTags getTemplate() {
        return ClassifiedTags.fromText(INSERT_TPL)
    }

    @Override
    DXFAttributes getDefaulDxfAttribs() {
        return makeAttribs([
                'attribs_follow': new DXFAttr(66, null, 0),
                'name': new DXFAttr(2),
                'insert': new DXFAttr(10, 'Point2D/3D'),
                'xscale': new DXFAttr(41, null, 1.0),
                'yscale': new DXFAttr(42, null, 1.0),
                'zscale': new DXFAttr(43, null, 1.0),
                'rotation': new DXFAttr(50, null, 0.0),
                'column_count': new DXFAttr(70, null, 1),
                'row_count': new DXFAttr(71, null, 1),
                'column_spacing': new DXFAttr(44, null, 0.0),
                'row_spacing': new DXFAttr(45, null, 0.0)
        ])
    }
}
