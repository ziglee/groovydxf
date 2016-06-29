package net.cassiolandim.dxf.graphics

import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.lldxf.ClassifiedTags
import net.cassiolandim.dxf.lldxf.DXFAttr
import net.cassiolandim.dxf.lldxf.DXFAttributes

class EndBlk extends GraphicEntity {

    EndBlk(ClassifiedTags tags, Drawing drawing = null) {
        super(tags, drawing)
    }

    EndBlk(String handle, Map dxfAttribs, Drawing drawing) {
        super(handle, dxfAttribs, drawing)
    }

    @Override
    ClassifiedTags getTemplate() {
        return ClassifiedTags.fromText("  0\nENDBLK\n  5\n0\n")
    }

    @Override
    DXFAttributes getDefaulDxfAttribs() {
        return new DXFAttributes(['handle': new DXFAttr(5)])
    }
}
