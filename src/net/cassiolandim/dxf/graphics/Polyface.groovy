package net.cassiolandim.dxf.graphics

import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.lldxf.ClassifiedTags

class Polyface extends Polyline {

    Polyface(ClassifiedTags tags, Drawing drawing = null) {
        super(tags, drawing)
    }

    Polyface(String handle, Map dxfAttribs, Drawing drawing) {
        super(handle, dxfAttribs, drawing)
    }

    static Polyface convert(Polyline polyline) {
        return new Polyface(polyline.tags, polyline.drawing)
    }

    // TODO PolyfaceMixin
}
