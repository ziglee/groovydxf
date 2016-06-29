package net.cassiolandim.dxf.legacy.layouts

import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.sections.EntitySection

/**
 * The Layout container
 */
class DXF12Layouts {

    DXF12Layout modelSpace
    DXF12Layout paperSpace

    DXF12Layouts(Drawing drawing) {
        EntitySection entities = drawing.sections.entities
        def modelSpace = entities.getLayoutSpace(0)
        this.modelSpace = new DXF12Layout(modelSpace, drawing.dxfFactory, 0)
        def paperSpace = entities.getLayoutSpace(1)
        this.paperSpace = new DXF12Layout(paperSpace, drawing.dxfFactory, 1)
    }

    DXF12Layout get(String name = "") {
        // AC1009 supports only one paperspace/layout
        return paperSpace
    }
}
