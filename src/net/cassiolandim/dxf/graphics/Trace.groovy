package net.cassiolandim.dxf.graphics

import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.lldxf.ClassifiedTags
import net.cassiolandim.dxf.lldxf.DXFAttr
import net.cassiolandim.dxf.lldxf.DXFAttributes

class Trace extends GraphicEntity {

    protected static final String TRACE_TPL = """  0
TRACE
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
0.0
 21
0.0
 31
0.0
 12
0.0
 22
0.0
 32
0.0
 13
0.0
 23
0.0
 33
0.0
"""

    Trace(ClassifiedTags tags, Drawing drawing = null) {
        super(tags, drawing)
    }

    Trace(String handle, Map dxfAttribs, Drawing drawing) {
        super(handle, dxfAttribs, drawing)
    }

    @Override
    ClassifiedTags getTemplate() {
        return ClassifiedTags.fromText(TRACE_TPL)
    }

    @Override
    DXFAttributes getDefaulDxfAttribs() {
        return makeAttribs([
                'vtx0': new DXFAttr(10, 'Point2D/3D'),
                'vtx1': new DXFAttr(11, 'Point2D/3D'),
                'vtx2': new DXFAttr(12, 'Point2D/3D'),
                'vtx3': new DXFAttr(13, 'Point2D/3D')
        ])
    }

    //QuadrilateralMixin
    //def __getitem__(self, num):
    //return self.get_dxf_attrib(VERTEXNAMES[num])

    //def __setitem__(self, num, value):
    //return self.set_dxf_attrib(VERTEXNAMES[num], value)
}
