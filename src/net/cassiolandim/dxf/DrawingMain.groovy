package net.cassiolandim.dxf

import net.cassiolandim.dxf.legacy.layouts.DXF12BlockLayout

class DrawingMain {

    public static void main(String[] args) {
        def drawing = new Drawing()
        def modelSpace = drawing.modelSpace
        modelSpace.addLine([0, 0], [2, 2])

        // block creation
        DXF12BlockLayout block = drawing.blocks.newBlock('TEST')
        block.addLine([-1, -1], [1, 1])
        block.addLine([-1, 1], [1, -1])
        // block usage
        modelSpace.addBlockRef('TEST', [5, 5], ['rotation': 45f])

        //use set_pos() for proper TEXT alignment - the relations between halign, valign, insert and align_point are tricky.
        modelSpace.addText("simple text").setPos([2, 3], null, 'MIDDLE_RIGHT')

        drawing.save('sample.dxf')
    }
}
