package net.cassiolandim.dxf.graphics

import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.lldxf.ClassifiedTags
import net.cassiolandim.dxf.lldxf.DXFAttr
import net.cassiolandim.dxf.lldxf.DXFAttributes

class Text extends GraphicEntity {

    private static final def TEXT_ALIGN_FLAGS = [
        'LEFT': [0, 0],
        'CENTER': [1, 0],
        'RIGHT': [2, 0],
        'ALIGNED': [3, 0],
        'MIDDLE': [4, 0],
        'FIT': [5, 0],
        'BOTTOM_LEFT': [0, 1],
        'BOTTOM_CENTER': [1, 1],
        'BOTTOM_RIGHT': [2, 1],
        'MIDDLE_LEFT': [0, 2],
        'MIDDLE_CENTER': [1, 2],
        'MIDDLE_RIGHT': [2, 2],
        'TOP_LEFT': [0, 3],
        'TOP_CENTER': [1, 3],
        'TOP_RIGHT': [2, 3],
    ]

    private static final def TEXT_ALIGNMENT_BY_FLAGS = [:]

    static {
        for (entry in TEXT_ALIGN_FLAGS.entrySet()) {
            TEXT_ALIGNMENT_BY_FLAGS.put(entry.value, entry.key)
        }
    }

    private static final String TEXT_TPL = """  0
TEXT
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
TEXTCONTENT
 50
0.0
 51
0.0
  7
STANDARD
 41
1.0
 71
0
 72
0
 73
0
 11
0.0
 21
0.0
 31
0.0
"""

    Text(ClassifiedTags tags, Drawing drawing = null) {
        super(tags, drawing)
    }

    Text(String handle, Map dxfAttribs, Drawing drawing) {
        super(handle, dxfAttribs, drawing)
    }

    @Override
    ClassifiedTags getTemplate() {
        return ClassifiedTags.fromText(TEXT_TPL)
    }

    @Override
    DXFAttributes getDefaulDxfAttribs() {
        return makeAttribs([
                'insert': new DXFAttr(10, 'Point2D/3D'),
                'height': new DXFAttr(40),
                'text': new DXFAttr(1),
                'rotation': new DXFAttr(50, null, 0.0), // in degrees (circle = 360deg)
                'oblique': new DXFAttr(51, null, 0.0), // in degrees, vertical = 0deg
                'style': new DXFAttr(7, null, 'STANDARD'), // text style
                'width': new DXFAttr(41, null, 1.0), // width FACTOR!
                'text_generation_flag': new DXFAttr(71, null, 0), // 2 = backward (mirr-x), 4 = upside down (mirr-y)
                'halign': new DXFAttr(72, null, 0), // horizontal justification
                'valign': new DXFAttr(73,  null, 0), // vertical justification
                'align_point': new DXFAttr(11, 'Point2D/3D')
        ])
    }

    def setPos(def p1, def p2 = null, String align = null) {
        if (!align) align = getAlign()
        align = align.toUpperCase()
        setAlign(align)

        setDxfAttrib('insert', p1)

        if (align.equals('ALIGNED') || align.equals('FIT')) {
            if (!p2)
             throw new IllegalStateException("Alignment '${align}' requires a second alignment point.")
        } else {
            p2 = p1
        }

        setDxfAttrib('align_point', p2)

        return this
    }

    String getAlign() {
        def hAlign = getDxfAttrib('halign', 0)
        def vAlign = getDxfAttrib('valign', 0)
        if (hAlign > 2)
            vAlign = 0
        return TEXT_ALIGNMENT_BY_FLAGS.get([hAlign, vAlign], 'LEFT')
    }

    def setAlign(String align = 'LEFT') {
        align = align.toUpperCase()
        def tuple = TEXT_ALIGN_FLAGS.get(align)
        setDxfAttrib('halign', tuple[0])
        setDxfAttrib('valign', tuple[1])
        return this
    }
}
