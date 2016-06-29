package net.cassiolandim.dxf.graphics

import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.lldxf.ClassifiedTags
import net.cassiolandim.dxf.lldxf.DXFAttr
import net.cassiolandim.dxf.lldxf.DXFAttributes

class Dimension extends GraphicEntity {

    private static final String DIMENSION_TPL = """  0
DIMENSION
  5
0
  2
*BLOCKNAME
  3
DIMSTYLE
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
 70
0
  1

 13
0.0
 23
0.0
 33
0.0
 14
0.0
 24
0.0
 34
0.0
 15
0.0
 25
0.0
 35
0.0
 16
0.0
 26
0.0
 36
0.0
 40
1.0
 50
0.0
"""

    Dimension(ClassifiedTags tags, Drawing drawing = null) {
        super(tags, drawing)
    }

    Dimension(String handle, Map dxfAttribs, Drawing drawing) {
        super(handle, dxfAttribs, drawing)
    }

    @Override
    ClassifiedTags getTemplate() {
        return ClassifiedTags.fromText(DIMENSION_TPL)
    }

    @Override
    DXFAttributes getDefaulDxfAttribs() {
        return makeAttribs([
                'geometry': new DXFAttr(2), // name of pseudo-Block containing the current dimension  entity geometry
                'dimstyle': new DXFAttr(3),
                'defpoint': new DXFAttr(10, 'Point2D/3D'),
                'text_midpoint': new DXFAttr(11, 'Point2D/3D'),
                'translation_vector': new DXFAttr(12, 'Point3D'),
                'dimtype': new DXFAttr(70),
                'user_text': new DXFAttr(1),
                'defpoint2': new DXFAttr(13, 'Point2D/3D'),
                'defpoint3': new DXFAttr(14, 'Point2D/3D'),
                'defpoint4': new DXFAttr(15, 'Point2D/3D'),
                'defpoint5': new DXFAttr(16, 'Point2D/3D'),
                'leader_length': new DXFAttr(40),
                'angle': new DXFAttr(50),
                'horizontal_direction': new DXFAttr(51),
                'oblique_angle': new DXFAttr(52),
                'dim_text_rotation': new DXFAttr(53)
        ])
    }
}
