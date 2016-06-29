package net.cassiolandim.dxf.graphics

import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.lldxf.ClassifiedTags
import net.cassiolandim.dxf.lldxf.DXFAttr
import net.cassiolandim.dxf.lldxf.DXFAttributes

class Block extends GraphicEntity {

    private static String BLOCK_TPL = """  0
BLOCK
  5
0
  8
0
  2
BLOCKNAME
  3
BLOCKNAME
 70
0
 10
0.0
 20
0.0
 30
0.0
  1

"""

    Block(ClassifiedTags tags, Drawing drawing = null) {
        super(tags, drawing)
    }

    Block(String handle, Map dxfAttribs, Drawing drawing) {
        super(handle, dxfAttribs, drawing)
    }

    @Override
    ClassifiedTags getTemplate() {
        return ClassifiedTags.fromText(BLOCK_TPL)
    }

    @Override
    DXFAttributes getDefaulDxfAttribs() {
        return makeAttribs([
                'name': new DXFAttr(2),
                'name2': new DXFAttr(3),
                'flags': new DXFAttr(70),
                'base_point': new DXFAttr(10, 'Point2D/3D'),
                'xref_path': new DXFAttr(1)
        ])
    }
}
