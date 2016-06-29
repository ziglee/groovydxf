package net.cassiolandim.dxf.graphics

import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.lldxf.ClassifiedTags

class Polymesh extends Polyline {

    Polymesh(ClassifiedTags tags, Drawing drawing = null) {
        super(tags, drawing)
    }

    Polymesh(String handle, Map dxfAttribs, Drawing drawing) {
        super(handle, dxfAttribs, drawing)
    }

    static Polymesh convert(Polyline polyline) {
        return new Polymesh(polyline.tags, polyline.drawing)
    }

    // TODO PolymeshMixin
}
