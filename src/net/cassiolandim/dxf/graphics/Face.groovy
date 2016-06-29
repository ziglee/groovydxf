package net.cassiolandim.dxf.graphics

import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.lldxf.ClassifiedTags
import net.cassiolandim.dxf.lldxf.DXFAttr
import net.cassiolandim.dxf.lldxf.DXFAttributes

class Face extends Trace {

    Face(ClassifiedTags tags, Drawing drawing = null) {
        super(tags, drawing)
    }

    Face(String handle, Map dxfAttribs, Drawing drawing) {
        super(handle, dxfAttribs, drawing)
    }

    @Override
    ClassifiedTags getTemplate() {
        return ClassifiedTags.fromText(TRACE_TPL.replace('TRACE', '3DFACE'))
    }

    @Override
    DXFAttributes getDefaulDxfAttribs() {
        return makeAttribs([
                'vtx0': new DXFAttr(10, 'Point3D'),
                'vtx1': new DXFAttr(11, 'Point3D'),
                'vtx2': new DXFAttr(12, 'Point3D'),
                'vtx3': new DXFAttr(13, 'Point3D'),
                'invisible_edge': new DXFAttr(70, null, 0)
        ])
    }
}
